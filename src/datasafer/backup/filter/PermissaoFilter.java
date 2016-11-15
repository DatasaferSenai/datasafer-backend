package datasafer.backup.filter;

import java.io.IOException;
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

import org.springframework.http.HttpStatus;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import datasafer.backup.model.Usuario;
import datasafer.backup.model.Usuario.Permissao;

@WebFilter(filterName = "PermissaoFilter")
public class PermissaoFilter implements Filter {

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

			Usuario solicitante = (Usuario) request.getAttribute("solicitante");
			Usuario usuario = (Usuario) request.getAttribute("usuario");

			Set<Permissao> permissoes = solicitante.getSuperior() != null && solicitante.getSuperior()
																						.getLogin()
																						.equals(usuario.getLogin()) ? solicitante.getDelegacoes()
																								: solicitante.getPermissoes();

			if (permissoes == null) {
				response.sendError(HttpStatus.FORBIDDEN.value(), "O usuário não possui permissão para realizar a operação solicitada");
				return;
			}

			if (request	.getRequestURI()
						.contains("usuario")
					|| request	.getRequestURI()
								.contains("usuarios")) {

				if ((request.getMethod() == "GET" && !permissoes.contains(Permissao.VISUALIZAR_USUARIOS))
						|| (request.getMethod() == "POST" && !permissoes.contains(Permissao.INSERIR_USUARIOS))
						|| (request.getMethod() == "PUT" && !permissoes.contains(Permissao.MODIFICAR_USUARIOS))
						|| (request.getMethod() == "DELETE" && !permissoes.contains(Permissao.EXCLUIR_USUARIOS))) {
					response.sendError(HttpStatus.FORBIDDEN.value(), "O usuário não possui permissão para realizar a operação solicitada");
					return;
				}

			}

			if (request	.getRequestURI()
						.contains("estacao")
					|| request	.getRequestURI()
								.contains("estacoes")) {

				if ((request.getMethod() == "GET" && !permissoes.contains(Permissao.VISUALIZAR_ESTACOES))
						|| (request.getMethod() == "POST" && !permissoes.contains(Permissao.INSERIR_ESTACOES))
						|| (request.getMethod() == "DELETE" && !permissoes.contains(Permissao.EXCLUIR_ESTACOES))) {
					response.sendError(HttpStatus.FORBIDDEN.value(), "O usuário não possui permissão para realizar a operação solicitada");
					return;
				}

			}
			if (request	.getRequestURI()
						.contains("backup")
					|| request	.getRequestURI()
								.contains("backups")) {

				if ((request.getMethod() == "GET" && !permissoes.contains(Permissao.VISUALIZAR_BACKUPS))
						|| (request.getMethod() == "POST" && !permissoes.contains(Permissao.INSERIR_BACKUPS))
						|| (request.getMethod() == "PUT" && !permissoes.contains(Permissao.MODIFICAR_BACKUPS))
						|| (request.getMethod() == "DELETE" && !permissoes.contains(Permissao.EXCLUIR_BACKUPS))) {
					response.sendError(HttpStatus.FORBIDDEN.value(), "O usuário não possui permissão para realizar a operação solicitada");
					return;
				}
			}
			if (request	.getRequestURI()
						.contains("operacao")
					|| request	.getRequestURI()
								.contains("operacoes")) {

				if ((request.getMethod() == "GET" && !permissoes.contains(Permissao.VISUALIZAR_OPERACOES))
						|| (request.getMethod() == "POST" && !permissoes.contains(Permissao.INSERIR_OPERACOES))
						|| (request.getMethod() == "DELETE" && !permissoes.contains(Permissao.EXCLUIR_OPERACOES))) {
					response.sendError(HttpStatus.FORBIDDEN.value(), "O usuário não possui permissão para realizar a operação solicitada");
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
