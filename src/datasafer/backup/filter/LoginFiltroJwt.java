package datasafer.backup.filter;

import java.io.IOException;
import java.util.Map;

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

import com.auth0.jwt.JWTVerifier;

import datasafer.backup.controller.UsuarioRestController;

@WebFilter("/Datasafer/gerenciamento/*")
public class LoginFiltroJwt implements Filter {
	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		if (request.getRequestURI().contains("login")) {
			chain.doFilter(req, resp);
			return;
		}

		String token = request.getHeader("Authorization");
		String login_usuario = request.getHeader("usuario");
		try {
			JWTVerifier verifier = new JWTVerifier(UsuarioRestController.SECRET);
			Map<String, Object> claims = verifier.verify(token);
			
			if(login_usuario == claims.get("login_usuario")){
				chain.doFilter(req, resp);
			} else {
				response.sendError(HttpStatus.FORBIDDEN.value());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			if (token == null) {
				response.sendError(HttpStatus.UNAUTHORIZED.value());
			} else {
				response.sendError(HttpStatus.FORBIDDEN.value());
			}
		}

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
	}

}
