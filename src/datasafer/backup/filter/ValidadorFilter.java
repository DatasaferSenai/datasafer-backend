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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import datasafer.backup.dao.utility.Carregador;
import datasafer.backup.model.Backup;
import datasafer.backup.model.Estacao;
import datasafer.backup.model.Usuario;

@WebFilter(filterName = "ValidadorFilter")
public class ValidadorFilter implements Filter {

	@Autowired
	private Carregador carregador;

	@Override
	public void doFilter(	ServletRequest req,
							ServletResponse resp,
							FilterChain chain)
												throws IOException, ServletException, DataIntegrityViolationException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		if (request	.getRequestURI()
					.contains("login")) {

			chain.doFilter(req, resp);
			return;
		}

		Usuario usuario = (Usuario) request.getAttribute("usuario");

		try {

			Estacao estacao = null;
			if ((request.getRequestURI()
						.contains("operacao")
					&& !request	.getRequestURI()
								.contains("operacoes"))
					|| (request	.getRequestURI()
								.contains("backup")
							&& !request	.getRequestURI()
										.contains("backups"))
					|| (request	.getRequestURI()
								.contains("estacao")
							&& !request	.getRequestURI()
										.contains("estacoes"))) {

				if (usuario == null) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
					response.getWriter().write(new JSONObject().put("erro", "Usuário não espeficado").toString());
					return;
				}

				if (request.getHeader("estacao") != null) {
					estacao = carregador.obtemEntidade(Estacao.class, "nome", (String) request.getHeader("estacao"));
				}
				if (estacao == null) {
					response.setStatus(HttpServletResponse.SC_NOT_FOUND);
					response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
					response.getWriter().write(new JSONObject().put("erro", "Estação inválida ou não encontrada").toString());
					return;
				} else {
					estacao.setProprietario(usuario);
				}

				request.setAttribute("estacao", estacao);
			}

			Backup backup = null;
			if ((request.getRequestURI()
						.contains("operacao")
					&& !request	.getRequestURI()
								.contains("operacoes"))
					|| (request	.getRequestURI()
								.contains("backup")
							&& !request	.getRequestURI()
										.contains("backups"))) {

				if (estacao == null) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
					response.getWriter().write(new JSONObject().put("erro", "Estação não espeficada").toString());
					return;
				}

				if (request.getHeader("backup") != null) {
					backup = carregador.obtemEntidade(Backup.class, "usuario", usuario, "estacao", estacao, "nome", (String) request.getHeader("backup"));
				}
				if (backup == null) {
					response.setStatus(HttpServletResponse.SC_NOT_FOUND);
					response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
					response.getWriter().write(new JSONObject().put("erro", "Backup inválido ou não encontrado").toString());
					return;
				} else {
					backup.setProprietario(usuario);
					backup.setEstacao(estacao);
				}

				request.setAttribute("backup", backup);

			}

			if ((request.getRequestURI()
						.contains("operacao")
					&& !request	.getRequestURI()
								.contains("operacoes"))) {

				if (backup == null) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
					response.getWriter().write(new JSONObject().put("erro", "Backup não espeficado").toString());
					return;
				}

			}

			chain.doFilter(req, resp);

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
