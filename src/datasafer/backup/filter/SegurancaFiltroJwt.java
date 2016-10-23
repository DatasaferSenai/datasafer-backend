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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.auth0.jwt.JWTVerifier;

import datasafer.backup.controller.UsuarioRestController;
import datasafer.backup.dao.UsuarioDao;
import datasafer.backup.model.Usuario;
import datasafer.backup.model.Usuario.Status;

@Service
@WebFilter("/gerenciamento/*")
public class SegurancaFiltroJwt implements Filter {

	@Autowired
	private UsuarioDao usuarioDao;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		if (request.getRequestURI().contains("login")) {
			chain.doFilter(req, resp);
			return;
		}

		
		String token = null;
		try {
			token = request.getHeader("Authorization");
			Usuario usuario = usuarioDao.obter((String)  new JWTVerifier(UsuarioRestController.SECRET).verify(token).get("login_usuario"));
			
			if (usuario == null || usuario.getExcluidoEm() != null || usuario.getExcluidoPor() != null) {
				response.sendError(HttpStatus.FORBIDDEN.value(), "Usuário inválido ou não encontrado");
			} else if (usuario.getStatus() != Status.ATIVO) {
				response.sendError(HttpStatus.FORBIDDEN.value(), usuario.getStatus().toString());
			} else {
				chain.doFilter(req, resp);
			}

		} catch (Exception e) {
			e.printStackTrace();
			if (token == null) {
				response.sendError(HttpStatus.UNAUTHORIZED.value(), "Autorização nula");
			} else {
				response.sendError(HttpStatus.FORBIDDEN.value(), "Autorização inválida");
			}
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
