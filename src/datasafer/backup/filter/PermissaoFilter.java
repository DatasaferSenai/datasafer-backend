package datasafer.backup.filter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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

import datasafer.backup.dao.UsuarioDao;
import datasafer.backup.model.Usuario;
import datasafer.backup.model.Usuario.Permissao;

@WebFilter(filterName = "PermissaoFilter")
public class PermissaoFilter implements Filter {

	@Autowired
	UsuarioDao usuarioDao;

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

			final Usuario solicitante = (Usuario) request.getAttribute("solicitante");
			final Usuario usuario = (Usuario) request.getAttribute("usuario");

			final Set<Permissao> permissoes = new HashSet<Permissao>();

			if (solicitante	.getLogin()
							.equals(usuario.getLogin())) {
				permissoes.addAll(usuario.getPermissoes());
			} else {

				for (Usuario relacionado = usuarioDao.obtemSuperior(usuario); relacionado != null; relacionado = usuarioDao.obtemSuperior(relacionado)) {
					if (relacionado	.getLogin()
									.equals(solicitante.getLogin())) {
						permissoes.addAll(solicitante.getPermissoes());
						break;
					}
				}

				for (Usuario relacionado = usuarioDao.obtemSuperior(solicitante); relacionado != null; relacionado = usuarioDao.obtemSuperior(
																																				relacionado)) {

					if (permissoes.isEmpty()) {
						permissoes.addAll(solicitante.getDelegacoes());
					} else {
						permissoes.retainAll(solicitante.getDelegacoes());
					}

					if (relacionado	.getLogin()
									.equals(usuario.getLogin())) {
						break;
					}
				}

			}

			if (request	.getRequestURI()
						.contains("usuario")
					|| request	.getRequestURI()
								.contains("usuarios")) {

				if (request	.getMethod()
							.equals("OPTIONS")) {
					response.setHeader("Allow", "GET, POST, PUT, DELETE");
					return;
				} else if ((request	.getMethod()
									.equals("GET")
						&& !permissoes.contains(Permissao.VISUALIZAR_USUARIOS))
						|| (request	.getMethod()
									.equals("POST")
								&& !permissoes.contains(Permissao.INSERIR_USUARIOS))
						|| (request	.getMethod()
									.equals("PUT")
								&& !permissoes.contains(Permissao.MODIFICAR_USUARIOS))
						|| (request	.getMethod()
									.equals("DELETE")
								&& !permissoes.contains(Permissao.EXCLUIR_USUARIOS))) {

					response.setStatus(HttpServletResponse.SC_FORBIDDEN);
					response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
					response.getWriter().write(new JSONObject().put("erro", "O usuário não possui permissão para realizar a operação solicitada").toString());
					return;
				}
			}

			if (request	.getRequestURI()
						.contains("estacao")
					|| request	.getRequestURI()
								.contains("estacoes")) {

				if (request	.getMethod()
							.equals("OPTIONS")) {
					response.setHeader("Allow", "GET, POST, DELETE");
					return;
				} else if ((request	.getMethod()
									.equals("GET")
						&& !permissoes.contains(Permissao.VISUALIZAR_ESTACOES))
						|| (request	.getMethod()
									.equals("POST")
								&& !permissoes.contains(Permissao.INSERIR_ESTACOES))
						|| (request	.getMethod()
									.equals("DELETE")
								&& !permissoes.contains(Permissao.EXCLUIR_ESTACOES))) {
					
					response.setStatus(HttpServletResponse.SC_FORBIDDEN);
					response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
					response.getWriter().write(new JSONObject().put("erro", "O usuário não possui permissão para realizar a operação solicitada").toString());
					return;
				}

			}

			if (request	.getRequestURI()
						.contains("backup")
					|| request	.getRequestURI()
								.contains("backups")) {

				if (request	.getMethod()
							.equals("OPTIONS")) {
					response.setHeader("Allow", "GET, POST, PUT, DELETE");
					return;
				} else if ((request	.getMethod()
									.equals("GET")
						&& !permissoes.contains(Permissao.VISUALIZAR_BACKUPS))
						|| (request	.getMethod()
									.equals("POST")
								&& !permissoes.contains(Permissao.INSERIR_BACKUPS))
						|| (request	.getMethod()
									.equals("PUT")
								&& !permissoes.contains(Permissao.MODIFICAR_BACKUPS))
						|| (request	.getMethod()
									.equals("DELETE")
								&& !permissoes.contains(Permissao.EXCLUIR_BACKUPS))) {

					response.setStatus(HttpServletResponse.SC_FORBIDDEN);
					response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
					response.getWriter().write(new JSONObject().put("erro", "O usuário não possui permissão para realizar a operação solicitada").toString());
					return;
				}
			}
			if (request	.getRequestURI()
						.contains("operacao")
					|| request	.getRequestURI()
								.contains("operacoes")) {

				if (request	.getMethod()
							.equals("OPTIONS")) {
					response.setHeader("Allow", "GET, POST, DELETE");
					return;
				} else if ((request	.getMethod()
									.equals("GET")
						&& !permissoes.contains(Permissao.VISUALIZAR_OPERACOES))
						|| (request	.getMethod()
									.equals("POST")
								&& !permissoes.contains(Permissao.INSERIR_OPERACOES))
						|| (request	.getMethod()
									.equals("DELETE")
								&& !permissoes.contains(Permissao.EXCLUIR_OPERACOES))) {

					response.setStatus(HttpServletResponse.SC_FORBIDDEN);
					response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
					response.getWriter().write(new JSONObject().put("erro", "O usuário não possui permissão para realizar a operação solicitada").toString());
					return;
				}
			}

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
