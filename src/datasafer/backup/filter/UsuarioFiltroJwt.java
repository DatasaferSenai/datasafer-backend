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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.auth0.jwt.JWTVerifier;

import datasafer.backup.controller.UsuarioRestController;
import datasafer.backup.dao.UsuarioDao;
import datasafer.backup.model.Privilegio.Permissao;
import datasafer.backup.model.Usuario;

@Service
@WebFilter("/gerenciamento/usuario/*")
public class UsuarioFiltroJwt implements Filter {

	@Autowired
	private UsuarioDao usuarioDao;

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		try {
			Usuario usuario = usuarioDao.obter((String) new JWTVerifier(UsuarioRestController.SECRET)
					.verify(request.getHeader("Authorization")).get("login_usuario"));
			Set<Permissao> permissoes = usuario.getPrivilegio().getPermissoes();

			if (permissoes != null) {
				if (usuario.getPrivilegio().getPermissoes().contains(Permissao.ADMINISTRADOR)
						| (request.getMethod() == "GET" && permissoes.contains(Permissao.VISUALIZAR_USUARIOS))
						| (request.getMethod() == "POST" && permissoes.contains(Permissao.INSERIR_USUARIOS))
						| (request.getMethod() == "PUT" && permissoes.contains(Permissao.MODIFICAR_USUARIOS))
						| (request.getMethod() == "DELETE" && permissoes.contains(Permissao.EXCLUIR_USUARIOS))

				) {
					chain.doFilter(req, resp);
				}
			} else {
				response.sendError(HttpStatus.FORBIDDEN.value(),
						"O usuário não possui permissão para realizar a operação solicitada");
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, filterConfig.getServletContext());
	}

}
