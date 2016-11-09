package datasafer.backup.controller;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;

import datasafer.backup.bo.UsuarioBo;
import datasafer.backup.model.Backup;
import datasafer.backup.model.Estacao;
import datasafer.backup.model.Usuario;
import datasafer.backup.model.Usuario.Status;

@CrossOrigin(maxAge = 3600)
@RestController
public class UsuarioRestController {
	public static final String SECRET = "J0pjgqSuFXmCw8RQMPWaYT8XSBTneN0nDfMjLgUQ37Tp6l6I2SjQmhn5i7jCLZpO";
	public static final String ISSUER = "http://www.sp.senai.br";
	public static final long EXPIRES_IN_SECONDS = 60 * 60 * 24;

	@Autowired
	private UsuarioBo usuarioBo;

	@RequestMapping(value = "/gerenciamento/usuario", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Void> inserir(@RequestAttribute String login_solicitante,
										@RequestAttribute String login_usuario,
										@RequestBody Usuario usuario_novo) {
		try {

			if (usuario_novo != null) {
				try {
					usuarioBo.inserir(login_solicitante, login_usuario, usuario_novo);
				} catch (Exception e) {
					e.printStackTrace();
					return new ResponseEntity<>(HttpStatus.NOT_FOUND);
				}
			} else {

			}

			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/gerenciamento/usuario", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Usuario> obter(@RequestAttribute String login_usuario) {
		try {

			Usuario usuario = usuarioBo.obter(login_usuario);
			if (usuario != null) {
				return ResponseEntity	.ok()
										.body(usuario);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/gerenciamento/usuario/usuarios", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<List<Usuario>> listarUsuarios(@RequestAttribute String login_usuario) {
		try {

			Usuario usuario = usuarioBo.obter(login_usuario);
			if (usuario != null) {
				return ResponseEntity	.ok()
										.body(usuario.getColaboradores());
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/gerenciamento/usuario/estacoes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<List<Estacao>> listarEstacoes(@RequestAttribute String login_solicitante,
														@RequestAttribute String login_usuario) {
		try {

			Usuario usuario = usuarioBo.obter(login_usuario);
			if (usuario != null) {
				return ResponseEntity	.ok()
										.body(usuario.getEstacoes());
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/gerenciamento/usuario/backups", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<List<Backup>> listarBackups(	HttpServletRequest req,
														@RequestHeader(name = "Authorization") String token) {
		try {

			String login_solicitante = (String) new JWTVerifier(UsuarioRestController.SECRET)	.verify(token)
																								.get("login_usuario");

			String login_proprietario = req.getHeader("usuario") != null ? req.getHeader("usuario") : login_solicitante;

			Usuario usuario = usuarioBo.obter(login_proprietario);

			if (usuario != null) {
				return ResponseEntity	.ok()
										.body(usuario.getBackups());
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/gerenciamento/login", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<String> logar(@RequestBody String corpo_usuario) {
		try {

			JSONObject jobj = new JSONObject(corpo_usuario);

			Usuario usuario = new Usuario();
			usuario.setLogin(jobj.getString("login"));
			usuario.setSenha(jobj.getString("senha"));

			boolean expira = jobj.getBoolean("expira");

			try {
				usuario = usuarioBo.logar(usuario);

			} catch (DataRetrievalFailureException e) {
				return new ResponseEntity<>(new JSONObject().put("erro", "Usuário ou senha inválidos")
															.toString(),
						HttpStatus.UNAUTHORIZED);
			}

			if (usuario.getStatus() != Status.ATIVO) {
				return new ResponseEntity<>(new JSONObject().put("erro", usuario.getStatus())
															.toString(),
						HttpStatus.UNAUTHORIZED);
			} else if (usuario.getTentativas() < 3) {

				long iat = System.currentTimeMillis() / 1000;
				long exp = iat + EXPIRES_IN_SECONDS;
				JWTSigner signer = new JWTSigner(SECRET);
				HashMap<String, Object> claims = new HashMap<>();
				claims.put("iat", iat);
				claims.put("exp", expira ? exp : Long.MAX_VALUE);
				claims.put("iss", ISSUER);
				claims.put("login_usuario", usuario.getLogin());

				String jwt = signer.sign(claims);

				return new ResponseEntity<>(new JSONObject().put("token", jwt)
															.toString(),
						HttpStatus.OK);
			}

			usuario.setTentativas(usuario.getTentativas() == null ? 1 : usuario.getTentativas() + 1);
			usuario.setUltimaTentativa(Calendar	.getInstance(TimeZone.getDefault())
												.getTime());
			usuarioBo.modificar("system", usuario);

			return new ResponseEntity<>(new JSONObject().put("erro", "Usuário ou senha inválidos")
														.toString(),
					HttpStatus.UNAUTHORIZED);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
