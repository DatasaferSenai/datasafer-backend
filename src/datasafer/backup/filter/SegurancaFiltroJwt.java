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
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		if (req	.getRequestURI()
				.contains("login")) {
			chain.doFilter(request, response);
			return;
		}

		try {
			String token = req.getHeader("Authorization");

			Map<String, Object> claims;
			try {
				claims = new JWTVerifier(UsuarioRestController.SECRET).verify(token);
			} catch (NoSuchAlgorithmException | InvalidKeyException | IllegalStateException | IOException | SignatureException | JWTVerifyException e) {
				claims = null;

				if (token == null) {
					resp.sendError(HttpStatus.UNAUTHORIZED.value(), "Autorização nula");
				} else {
					resp.sendError(HttpStatus.FORBIDDEN.value(), "Autorização inválida");
				}
			}

			if (claims != null) {

				String login_solicitante = (String) claims.get("login_usuario");

				String login_proprietario;

				if (req.getHeader("usuario") != null) {
					login_proprietario = req.getHeader("usuario");
				} else {
					login_proprietario = login_solicitante;
				}

				Usuario solicitante = usuarioDao.obter(login_solicitante);
				Usuario proprietario = usuarioDao.obter(login_proprietario);

				if (solicitante == null || solicitante.getExcluidoEm() != null || solicitante.getExcluidoPor() != null) {
					resp.sendError(HttpStatus.FORBIDDEN.value(), "Usuário inválido ou não encontrado");
				} else if (solicitante.getStatus() != Status.ATIVO) {
					resp.sendError(HttpStatus.FORBIDDEN.value(), solicitante.getStatus()
																			.toString());
				} else if (proprietario == null || proprietario.getExcluidoEm() != null || proprietario.getExcluidoPor() != null) {
					resp.sendError(HttpStatus.FORBIDDEN.value(), "Usuário inválido ou não encontrado");
				} else if (proprietario.getStatus() != Status.ATIVO) {
					resp.sendError(HttpStatus.FORBIDDEN.value(), proprietario	.getStatus()
																				.toString());
				} else {

					boolean relacionados = false;
					if (!solicitante.getLogin()
									.equals(proprietario.getLogin())) {
						for (Usuario superior = proprietario.getSuperior(); superior != null && superior != solicitante; superior = superior.getSuperior()) {
							if (solicitante	.getLogin()
											.equals(superior.getLogin())) {
								relacionados = true;
								break;
							}
						}
					} else {
						relacionados = true;
					}

					if (!relacionados) {
						resp.sendError(HttpStatus.FORBIDDEN.value(), "Usuário inválido ou não encontrado");
					} else {
						chain.doFilter(req, resp);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			resp.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value());
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
