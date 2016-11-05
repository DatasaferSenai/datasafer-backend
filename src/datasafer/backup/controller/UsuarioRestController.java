package datasafer.backup.controller;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;

import datasafer.backup.dao.UsuarioDao;
import datasafer.backup.model.Backup;
import datasafer.backup.model.Estacao;
import datasafer.backup.model.Operacao;
import datasafer.backup.model.Usuario;
import datasafer.backup.model.Usuario.Status;

@RestController
public class UsuarioRestController {
	public static final String SECRET = "J0pjgqSuFXmCw8RQMPWaYT8XSBTneN0nDfMjLgUQ37Tp6l6I2SjQmhn5i7jCLZpO";
	public static final String ISSUER = "http://www.sp.senai.br";
	public static final long EXPIRES_IN_SECONDS = 60 * 60 * 24;

	@Autowired
	private UsuarioDao usuarioDao;

	@RequestMapping(value = "/gerenciamento/usuario", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Void> inserir(HttpServletRequest req, @RequestHeader(name = "Authorization") String token, @RequestBody Usuario usuario) {
		try {

			String login_solicitante = (String) new JWTVerifier(UsuarioRestController.SECRET)	.verify(token)
																								.get("login_usuario");

			String login_superior = req.getHeader("usuario") != null ? req.getHeader("usuario") : login_solicitante;

			try {
				usuarioDao.inserir(login_solicitante, login_superior, usuario);
			} catch (Exception e) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/gerenciamento/usuario", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Void> modificar(HttpServletRequest req, @RequestHeader(name = "Authorization") String token, @RequestBody String corpo_usuario) {
		try {

			String login_solicitante = (String) new JWTVerifier(UsuarioRestController.SECRET)	.verify(token)
																								.get("login_usuario");

			String login_usuario = req.getHeader("usuario") != null ? req.getHeader("usuario") : login_solicitante;

			Usuario usuario = usuarioDao.obter(login_usuario);
			if (usuario != null) {
				JSONObject jobj = new JSONObject(corpo_usuario);

				if (jobj.has("nome")) {
					usuario.setNome(jobj.getString("nome"));
				}
				if (jobj.has("login")) {
					usuario.setLogin(jobj.getString("login"));
				}
				if (jobj.has("senha")) {
					usuario.setSenha(jobj.getString("senha"));
				}
				if (jobj.has("email")) {
					usuario.setEmail(jobj.getString("email"));
				}
				if (jobj.has("armazenamento")) {
					usuario.setArmazenamento(jobj.getLong("armazenamento"));
				}

				usuarioDao.modificar(login_solicitante, usuario);

				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/gerenciamento/usuario", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Usuario> obter(HttpServletRequest req, @RequestHeader(name = "Authorization") String token) {
		try {

			String login_solicitante = (String) new JWTVerifier(UsuarioRestController.SECRET)	.verify(token)
																								.get("login_usuario");

			Usuario usuario = usuarioDao.obter(login_solicitante);
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
	public ResponseEntity<List<Usuario>> listarUsuarios(HttpServletRequest req, @RequestHeader(name = "Authorization") String token) {
		try {

			String login_solicitante = (String) new JWTVerifier(UsuarioRestController.SECRET)	.verify(token)
																								.get("login_usuario");

			String login_superior = req.getHeader("usuario") != null ? req.getHeader("usuario") : login_solicitante;

			List<Usuario> usuarios = usuarioDao.listarUsuarios(login_superior);
			if (usuarios != null) {
				return ResponseEntity	.ok()
										.body(usuarios);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/gerenciamento/usuario/estacoes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<List<Estacao>> listarEstacoes(HttpServletRequest req, @RequestHeader(name = "Authorization") String token) {
		try {

			String login_solicitante = (String) new JWTVerifier(UsuarioRestController.SECRET)	.verify(token)
																								.get("login_usuario");

			String login_proprietario = req.getHeader("usuario") != null ? req.getHeader("usuario") : login_solicitante;

			List<Estacao> estacoes = usuarioDao.listarEstacoes(login_proprietario);
			if (estacoes != null) {
				return ResponseEntity	.ok()
										.body(estacoes);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/gerenciamento/usuario/backups", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<List<Backup>> listarBackups(HttpServletRequest req, @RequestHeader(name = "Authorization") String token) {
		try {

			String login_solicitante = (String) new JWTVerifier(UsuarioRestController.SECRET)	.verify(token)
																								.get("login_usuario");

			String login_proprietario = req.getHeader("usuario") != null ? req.getHeader("usuario") : login_solicitante;

			List<Backup> backups = usuarioDao.listarBackups(login_proprietario);
			if (backups != null) {
				return ResponseEntity	.ok()
										.body(backups);
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

			usuario = usuarioDao.logar(usuario);

			if (usuario == null || usuario.getExcluidoEm() != null || usuario.getExcluidoPor() != null) {
				return ResponseEntity	.status(HttpStatus.UNAUTHORIZED)
										.body(new JSONObject()	.put("erro", "Usuário ou senha inválidos")
																.toString());
			} else if (usuario.getStatus() != Status.ATIVO) {
				return ResponseEntity	.status(HttpStatus.UNAUTHORIZED)
										.body(new JSONObject()	.put("erro", usuario.getStatus())
																.toString());
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

				usuario.setTentativas(0);
				usuario.setUltimaTentativa(null);
				usuarioDao.modificar("system", usuario);

				return ResponseEntity	.status(HttpStatus.OK)
										.body(new JSONObject()	.put("token", jwt)
																.toString());
			}

			usuario.setTentativas(usuario.getTentativas() == null ? 1 : usuario.getTentativas() + 1);
			usuario.setUltimaTentativa(Calendar	.getInstance(TimeZone.getDefault())
												.getTime());
			usuarioDao.modificar("system", usuario);

			return ResponseEntity	.status(HttpStatus.UNAUTHORIZED)
									.body(new JSONObject()	.put("erro", "Usuário ou senha inválidos")
															.toString());

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
