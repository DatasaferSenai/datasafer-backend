package datasafer.backup.filter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import datasafer.backup.dao.TokenDao;
import datasafer.backup.dao.UsuarioDao;
import datasafer.backup.model.Token;
import datasafer.backup.model.Usuario;
import datasafer.backup.model.Usuario.Status;

@WebFilter(filterName = "SegurancaFilter")
public class SegurancaFilter implements Filter {

	@Autowired
	private UsuarioDao usuarioDao;
	@Autowired
	private TokenDao tokenDao;

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
			String chave_token = request.getHeader("Authorization");
			if (chave_token == null) {
				response.sendError(HttpStatus.UNAUTHORIZED.value(), "Autorização nula");
				return;
			}

			Token token = tokenDao.obter(chave_token);
			if (token == null) {
				response.sendError(HttpStatus.FORBIDDEN.value(), "Autorização inválida");
				return;
			}

			Date agora = Date.from(LocalDateTime.now()
												.atZone(ZoneId.systemDefault())
												.toInstant());

			if (token.getExpiracao() != null && token	.getExpiracao()
														.before(agora)) {
				response.sendError(HttpStatus.FORBIDDEN.value(), "Autorização inválida");
				return;
			}

			Usuario solicitante = token.getUsuario();
			Usuario usuario = request.getHeader("usuario") != null ? usuarioDao.obter(request.getHeader("usuario")) : solicitante;

			if (solicitante == null || solicitante.getStatus() == Status.INATIVO) {
				response.sendError(HttpStatus.FORBIDDEN.value(), "Usuário inválido ou não encontrado");
				return;
			}

			if (solicitante.getStatus() != Status.ATIVO) {
				response.sendError(HttpStatus.FORBIDDEN.value(), solicitante.getStatus()
																			.toString());
				return;
			}

			if (usuario == null || usuario.getStatus() == Status.INATIVO) {
				response.sendError(HttpStatus.FORBIDDEN.value(), "Usuário inválido ou não encontrado");
				return;
			}

			if (usuario.getStatus() != Status.ATIVO) {
				response.sendError(HttpStatus.FORBIDDEN.value(), usuario.getStatus()
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
