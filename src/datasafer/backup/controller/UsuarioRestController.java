package datasafer.backup.controller;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
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
import datasafer.backup.model.Estacao;
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
	public ResponseEntity<String> inserir(HttpServletRequest req, @RequestHeader(name = "Authorization") String token, @RequestBody String corpo_usuario) {
		try {

			String login_solicitante = (String) new JWTVerifier(UsuarioRestController.SECRET)	.verify(token)
																								.get("login_usuario");

			String login_superior;
			try {
				login_superior = req.getHeader("usuario");
			} catch (Exception e) {
				login_superior = login_solicitante;
			}

			JSONObject job = new JSONObject(corpo_usuario);

			Usuario usuario = new Usuario();
			usuario.setNome(job.getString("nome"));
			usuario.setEmail(job.getString("email"));
			usuario.setLogin(job.getString("login"));
			usuario.setSenha(job.getString("senha"));
			usuario.setArmazenamento(job.getLong("armazenamento"));

			usuarioDao.inserir(login_superior, login_superior, usuario);

			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/gerenciamento/usuario", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<String> modificar(HttpServletRequest req, @RequestHeader(name = "Authorization") String token, @RequestBody String corpo_usuario) {
		try {

			String login_solicitante = (String) new JWTVerifier(UsuarioRestController.SECRET)	.verify(token)
																								.get("login_usuario");

			String login_usuario;
			try {
				login_usuario = req.getHeader("usuario");
			} catch (Exception e) {
				login_usuario = login_solicitante;
			}

			Usuario usuario = usuarioDao.obter(login_usuario);
			if (usuario != null) {
				JSONObject job = new JSONObject(corpo_usuario);

				if (job.has("nome")) {
					usuario.setNome(job.getString("nome"));
				}
				if (job.has("login")) {
					usuario.setLogin(job.getString("login"));
				}
				if (job.has("senha")) {
					usuario.setSenha(job.getString("senha"));
				}
				if (job.has("email")) {
					usuario.setEmail(job.getString("email"));
				}
				if (job.has("armazenamento")) {
					usuario.setArmazenamento(job.getLong("armazenamento"));
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
	public ResponseEntity<String> obter(@RequestHeader(name = "Authorization") String token) {
		try {
			Usuario usuario = usuarioDao.obter((String) new JWTVerifier(UsuarioRestController.SECRET)	.verify(token)
																										.get("login_usuario"));
			if (usuario != null) {
				JSONObject job = new JSONObject();
				job.put("nome", usuario.getNome());
				job.put("operacoes", usuario.getOperacoes());
				job.put("armazenamento", usuario.getArmazenamento());
				job.put("ocupado", usuario.getOcupado());
				job.put("privilegio", usuario	.getPrivilegio()
												.getNome());
				return ResponseEntity	.ok()
										.body(job.toString());
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/gerenciamento/usuario/usuarios", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<String> listarUsuarios(@RequestHeader(name = "Authorization") String token) {
		try {
			Usuario usuario = usuarioDao.obter((String) new JWTVerifier(UsuarioRestController.SECRET)	.verify(token)
																										.get("login_usuario"));
			if (usuario != null) {
				JSONObject job = new JSONObject();
				job.put("nome", usuario.getNome());
				job.put("email", usuario.getEmail());
				job.put("operacoes", usuario.getOperacoes());
				job.put("armazenamento", usuario.getArmazenamento());
				job.put("ocupado", usuario.getOcupado());
				job.put("privilegio", usuario	.getPrivilegio()
												.getNome());
				return ResponseEntity	.ok()
										.body(job.toString());
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/gerenciamento/usuario/estacoes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<String> listarEstacoes(@RequestHeader(name = "Authorization") String token) {
		try {
			Usuario usuario = usuarioDao.obter((String) new JWTVerifier(UsuarioRestController.SECRET)	.verify(token)
																										.get("login_usuario"));
			if (usuario != null) {
				JSONArray jarray = new JSONArray();
				for (Estacao e : usuario.getEstacoes()) {
					JSONObject job = new JSONObject();
					job.put("nome", e.getNome());
					job.put("descricao", e.getDescricao());
					job.put("operacoes", e.getOperacoes());
					jarray.put(job);
				}
				return ResponseEntity	.ok()
										.body(jarray.toString());
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
			JSONObject job = new JSONObject(corpo_usuario);
			String login_usuario = job.getString("login");
			String senha_usuario = job.getString("senha");
			boolean expira = job.getBoolean("expira");

			Usuario usuario = usuarioDao.obter(login_usuario);
			if (usuario == null || usuario.getExcluidoEm() != null || usuario.getExcluidoPor() != null) {
				return ResponseEntity	.status(HttpStatus.UNAUTHORIZED)
										.body(new JSONObject()	.put("erro", "Usuário ou senha inválidos")
																.toString());
			} else if (usuario.getStatus() != Status.ATIVO) {
				return ResponseEntity	.status(HttpStatus.UNAUTHORIZED)
										.body(new JSONObject()	.put("erro", usuario.getStatus())
																.toString());
			} else if (usuario.getTentativas() == null || usuario.getTentativas() < 3) {
				if (usuarioDao.logar(usuario) != null) {
					long iat = System.currentTimeMillis() / 1000;
					long exp = iat + EXPIRES_IN_SECONDS;
					JWTSigner signer = new JWTSigner(SECRET);
					HashMap<String, Object> claims = new HashMap<>();
					claims.put("iat", iat);
					claims.put("exp", expira ? exp : Long.MAX_VALUE);
					claims.put("iss", ISSUER);
					claims.put("login_usuario", login_usuario);

					String jwt = signer.sign(claims);

					usuario.setTentativas(0);
					usuario.setUltimaTentativa(null);
					usuarioDao.modificar("Sistema", usuario);

					return ResponseEntity	.status(HttpStatus.OK)
											.body(new JSONObject()	.put("token", jwt)
																	.toString());
				}
			}

			usuario.setTentativas(usuario.getTentativas() == null ? 1 : usuario.getTentativas() + 1);
			usuario.setUltimaTentativa(Calendar	.getInstance(TimeZone.getDefault())
												.getTime());
			usuarioDao.modificar("Sistema", usuario);

			return ResponseEntity	.status(HttpStatus.UNAUTHORIZED)
									.body(new JSONObject()	.put("erro", "Usuário ou senha inválidos")
															.toString());

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
