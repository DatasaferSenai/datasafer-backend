package datasafer.backup.controller;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
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
	public ResponseEntity<Void> inserir(HttpServletRequest req, @RequestHeader(name = "Authorization") String token, @RequestBody String corpo_usuario) {
		try {

			String login_solicitante = (String) new JWTVerifier(UsuarioRestController.SECRET)	.verify(token)
																								.get("login_usuario");

			String login_superior;
			try {
				login_superior = req.getHeader("usuario");
			} catch (Exception e) {
				login_superior = login_solicitante;
			}

			JSONObject jobj = new JSONObject(corpo_usuario);

			Usuario usuario = new Usuario();
			usuario.setNome(jobj.getString("nome"));
			usuario.setEmail(jobj.getString("email"));
			usuario.setLogin(jobj.getString("login"));
			usuario.setSenha(jobj.getString("senha"));
			usuario.setArmazenamento(jobj.getLong("armazenamento"));

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

			String login_usuario;
			try {
				login_usuario = req.getHeader("usuario");
			} catch (Exception e) {
				login_usuario = login_solicitante;
			}

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
	public ResponseEntity<String> obter(@RequestHeader(name = "Authorization") String token) {
		try {
			Usuario usuario = usuarioDao.obter((String) new JWTVerifier(UsuarioRestController.SECRET)	.verify(token)
																										.get("login_usuario"));
			if (usuario != null) {
				JSONObject jobj = new JSONObject();

				jobj.put("nome", usuario.getNome());
				jobj.put("armazenamento", usuario.getArmazenamento());
				jobj.put("privilegio", usuario	.getPrivilegio()
												.getNome());

				jobj.put("ocupado", usuarioDao	.listarOperacoes(usuario.getNome())
												.stream()
												.mapToLong(Operacao::getTamanho)
												.sum());

				JSONObject operacoes_jobj = new JSONObject();
				Arrays	.asList(Operacao.Status.values())
						.forEach(s -> {
							try {
								operacoes_jobj.put(s.toString(), usuarioDao	.listarOperacoes(usuario.getNome())
																			.stream()
																			.filter(o -> o.getStatus() == s)
																			.count());
							} catch (Exception e) {
								e.printStackTrace();
							}
						});
				jobj.put("operacoes",operacoes_jobj);

				return ResponseEntity	.ok()
										.body(jobj.toString());
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
			String login_superior = (String) new JWTVerifier(UsuarioRestController.SECRET)	.verify(token)
																							.get("login_usuario");

			List<Usuario> usuarios = usuarioDao.listarUsuarios(login_superior);
			if (usuarios != null) {

				JSONArray jarray = new JSONArray();
				for (Usuario u : usuarios) {
					JSONObject jobj = new JSONObject();
					jobj.put("nome", u.getNome());
					jobj.put("email", u.getEmail());
					jobj.put("armazenamento", u.getArmazenamento());
					jobj.put("privilegio", u.getPrivilegio()
											.getNome());

					// jobj.put("operacoes", u.getOperacoes());
					// jobj.put("ocupado", u.getOcupado());

					jarray.put(jobj);
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

	@RequestMapping(value = "/gerenciamento/usuario/estacoes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<String> listarEstacoes(@RequestHeader(name = "Authorization") String token) {
		try {

			String login_proprietario = (String) new JWTVerifier(UsuarioRestController.SECRET)	.verify(token)
																								.get("login_usuario");

			List<Estacao> estacoes = usuarioDao.listarEstacoes(login_proprietario);
			if (estacoes != null) {
				JSONArray jarray = new JSONArray();
				for (Estacao e : estacoes) {

					JSONObject jobj = new JSONObject();
					jobj.put("nome", e.getNome());
					jobj.put("descricao", e.getDescricao());
					jobj.put("operacoes", e.getOperacoes());

					jarray.put(jobj);
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

	@RequestMapping(value = "/gerenciamento/usuario/backups", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<String> listarBackups(@RequestHeader(name = "Authorization") String token) {
		try {

			String login_proprietario = (String) new JWTVerifier(UsuarioRestController.SECRET)	.verify(token)
																								.get("login_usuario");

			List<Backup> backups = usuarioDao.listarBackups(login_proprietario);
			if (backups != null) {
				JSONArray jarray = new JSONArray();
				for (Backup b : backups) {
					JSONObject jobj = new JSONObject();

					jobj.put("nome", b.getNome());
					jobj.put("descricao", b.getDescricao());
					jobj.put("pasta", b.getPasta());
					jobj.put("intervalo", new SimpleDateFormat("HH:mm:ss").format(b.getIntervalo()));
					jobj.put("inicio", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(b.getInicio()));

					jarray.put(jobj);
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

	@RequestMapping(value = "/gerenciamento/usuario/operacoes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<String> listarOperacoes(@RequestHeader(name = "Authorization") String token) {
		try {

			String login_proprietario = (String) new JWTVerifier(UsuarioRestController.SECRET)	.verify(token)
																								.get("login_usuario");

			List<Operacao> operacoes = usuarioDao.listarOperacoes(login_proprietario);
			if (operacoes != null) {
				JSONArray jarray = new JSONArray();
				for (Operacao o : operacoes) {
					JSONObject jobj = new JSONObject();

					jobj.put("data", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(o.getData()));
					jobj.put("status", o.getStatus()
										.toString());
					jobj.put("tamanho", o.getTamanho());

					jarray.put(jobj);
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
				usuarioDao.modificar("Sistema", usuario);

				return ResponseEntity	.status(HttpStatus.OK)
										.body(new JSONObject()	.put("token", jwt)
																.toString());
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
