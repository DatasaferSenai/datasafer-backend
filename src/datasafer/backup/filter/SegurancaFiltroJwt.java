package datasafer.backup.filter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import datasafer.backup.dao.TokenDao;
import datasafer.backup.dao.UsuarioDao;
import datasafer.backup.model.Token;
import datasafer.backup.model.Usuario;
import datasafer.backup.model.Usuario.Permissao;
import datasafer.backup.model.Usuario.Status;

@Service
@WebFilter("/gerenciamento/*")
public class SegurancaFiltroJwt implements Filter {

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

			// boolean relacionados = true;
			//
			// if (!relacionados) {
			// resp.sendError(HttpStatus.FORBIDDEN.value(), "Usuário inválido ou
			// não encontrado");
			// return;
			// }

			Set<Permissao> permissoes = solicitante.getPermissoes();
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

			} else if (request	.getRequestURI()
								.contains("estacao")
					|| request	.getRequestURI()
								.contains("estacoes")) {

				if ((request.getMethod() == "GET" && !permissoes.contains(Permissao.VISUALIZAR_ESTACOES))
						|| (request.getMethod() == "POST" && !permissoes.contains(Permissao.INSERIR_ESTACOES))
						|| (request.getMethod() == "PUT" && !permissoes.contains(Permissao.MODIFICAR_ESTACOES))
						|| (request.getMethod() == "DELETE" && !permissoes.contains(Permissao.EXCLUIR_ESTACOES))) {
					response.sendError(HttpStatus.FORBIDDEN.value(), "O usuário não possui permissão para realizar a operação solicitada");
					return;
				}

			} else if (request	.getRequestURI()
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
			} else if (request	.getRequestURI()
								.contains("operacao")
					|| request	.getRequestURI()
								.contains("operacoes")) {

				if ((request.getMethod() == "GET" && !permissoes.contains(Permissao.VISUALIZAR_OPERACOES))
						|| (request.getMethod() == "POST" && !permissoes.contains(Permissao.INSERIR_OPERACOES))
						|| (request.getMethod() == "POST" && !permissoes.contains(Permissao.MODIFICAR_OPERACOES))
						|| (request.getMethod() == "DELETE" && !permissoes.contains(Permissao.EXCLUIR_OPERACOES))) {
					response.sendError(HttpStatus.FORBIDDEN.value(), "O usuário não possui permissão para realizar a operação solicitada");
					return;
				}
			}

			request.setAttribute("login_solicitante", solicitante.getLogin());
			request.setAttribute("login_usuario", usuario.getLogin());

			chain.doFilter(request, response);

		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}

	}

	@Override
	public void destroy() {

	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, filterConfig.getServletContext());
	}

}
