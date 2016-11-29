package datasafer.backup.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import datasafer.backup.dao.AutorizacaoDao;
import datasafer.backup.dao.utility.Carregador;
import datasafer.backup.model.Autorizacao;
import datasafer.backup.model.Usuario;
import datasafer.backup.model.Usuario.Status;

@WebFilter(filterName = "SegurancaFilter")
public class SegurancaFilter implements Filter {

	@Autowired
	private Carregador carregador;
	@Autowired
	private AutorizacaoDao tokenDao;

	@Override
	public void doFilter(	ServletRequest req,
							ServletResponse resp,
							FilterChain chain)
												throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		if (request	.getRequestURI()
					.contains("login")) {

			chain.doFilter(req, resp);
			return;
		}

		try {
			String chave_token = (String) request.getHeader("Authorization");
			if (chave_token == null) {

				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
				response.getWriter().write(new JSONObject().put("erro", "Autorização nula").toString());
				return;
			}

			Autorizacao token = tokenDao.obtemAutorizacao(	req.getRemoteAddr() != null	? req.getRemoteAddr()
																						: req.getLocalAddr(),
															chave_token);
			if (token == null) {

				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
				response.getWriter().write(new JSONObject().put("erro", "Autorização inválida").toString());
				return;
			}

			Usuario solicitante = token.getUsuario();
			Usuario usuario = request.getHeader("usuario") != null	? carregador.obtemEntidade(Usuario.class, "login", request.getHeader("usuario"))
																	: solicitante;

			if (solicitante == null) {

				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
				response.getWriter().write(new JSONObject().put("erro", "Solicitante inválido ou não encontrado").toString());
				return;
			}

			if (solicitante.getStatus() != Status.ATIVO) {

				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
				response.getWriter().write(new JSONObject()	.put("erro", solicitante.getStatus()
																					.toString())
															.toString());
				return;
			}

			if (usuario == null) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
				response.getWriter().write(new JSONObject().put("erro", "Usuário inválido ou não encontrado").toString());
				return;
			}

			if (usuario.getStatus() != Status.ATIVO) {

				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
				response.getWriter().write(new JSONObject()	.put("erro", usuario.getStatus()
																				.toString())
															.toString());
				return;
			}

			request.setAttribute("solicitante", solicitante);
			request.setAttribute("usuario", usuario);

			chain.doFilter(request, response);

		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}

	}

	@Override
	public void destroy() {}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, filterConfig.getServletContext());
	}

}
