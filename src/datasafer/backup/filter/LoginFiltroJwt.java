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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.auth0.jwt.JWTVerifier;

import datasafer.backup.bo.UsuarioBo;
import datasafer.backup.controller.UsuarioRestController;
import datasafer.backup.model.Usuario;
import datasafer.backup.model.Usuario.Status;

@WebFilter("/Datasafer/gerenciamento/*")
public class LoginFiltroJwt implements Filter {

	@Autowired
	private UsuarioBo usuarioBo;

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

			Usuario usuario = usuarioBo.obterUsuario(login_usuario);

			if (usuario.getStatus() == Status.ATIVO) {
				chain.doFilter(req, resp);
			} else {
				response.sendError(HttpStatus.FORBIDDEN.value(), usuario.getStatus().toString());
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
	public void init(FilterConfig arg0) throws ServletException {

	}

}
