package datasafer.backup.filter;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
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

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;

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
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		if (request	.getRequestURI()
					.contains("login")) {
			chain.doFilter(req, resp);
			return;
		}

		try {
			String token = request.getHeader("Authorization");

			Map<String, Object> claims;
			try {
				claims = new JWTVerifier(UsuarioRestController.SECRET).verify(token);
			} catch (NoSuchAlgorithmException | InvalidKeyException | IllegalStateException | IOException | SignatureException | JWTVerifyException e) {
				claims = null;

				if (token == null) {
					response.sendError(HttpStatus.UNAUTHORIZED.value(), new JSONObject().put("erro", "Autoriza��o nula")
																						.toString());
				} else {
					response.sendError(HttpStatus.FORBIDDEN.value(), new JSONObject()	.put("erro", "Autoriza��o inv�lida")
																						.toString());
				}
			}

			if (claims != null) {
				Usuario usuario = usuarioDao.obter((String) claims.get("login_usuario"));

				if (usuario == null || usuario.getExcluidoEm() != null || usuario.getExcluidoPor() != null) {
					response.sendError(HttpStatus.FORBIDDEN.value(), "Usu�rio inv�lido ou n�o encontrado");
				} else if (usuario.getStatus() != Status.ATIVO) {
					response.sendError(HttpStatus.FORBIDDEN.value(), usuario.getStatus()
																			.toString());
				} else {
					chain.doFilter(req, resp);
				}
			}

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
