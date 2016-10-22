package datasafer.backup.controller;

import java.util.HashMap;
import java.util.List;

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

@RestController
public class UsuarioRestController {
	public static final String SECRET = "J0pjgqSuFXmCw8RQMPWaYT8XSBTneN0nDfMjLgUQ37Tp6l6I2SjQmhn5i7jCLZpO";
	public static final String ISSUER = "http://www.sp.senai.br";
	public static final long EXPIRES_IN_SECONDS = 60 * 60 * 24;

	@Autowired
	private UsuarioDao usuarioDao;

	@RequestMapping(value = "/gerenciamento/usuario", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<String> inserir(@RequestHeader(name = "Authorization") String token,
			@RequestBody String corpo_usuario) {
		try {
			JSONObject job = new JSONObject(corpo_usuario);
			Usuario usuario = new Usuario();
			usuario.setNome(job.getString("nome"));
			usuario.setLogin(job.getString("login"));
			usuario.setSenha(job.getString("senha"));
			usuario.setArmazenamento(job.getLong("armazenamento"));
			
//			SimpleDateFormat formatter = new SimpleDateFormat ("dd/mm/yyyy hh:MM:ss");
//			(formatter.format(job.getString("inicio")));
			//usuarioDao.inserir(login_superior, usuario);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//	@RequestMapping(value = "/gerenciamento/usuario", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//	public ResponseEntity<String> obter(@RequestHeader(name = "Authorization") String token, @RequestHeader(name = "usuario") String login_usuario) {
//		try {
//			Usuario usuario = usuarioDao.obter(login_usuario);
//			if (usuario != null) {
//				JSONObject job = new JSONObject();
//				job.put("nome", usuario.getNome());
//				job.put("operacoes", usuario.getOperacoes());
//				job.put("armazenamento", usuario.getArmazenamento());
//				job.put("ocupado", usuario.getOcupado());
//				job.put("privilegio", usuario.getPrivilegio().getNome());
//				return ResponseEntity.ok().body(job.toString());
//			} else {
//				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
	
	@RequestMapping(value = "/gerenciamento/usuario", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<String> obter(@RequestHeader(name = "Authorization") String token) {
		try {
			Usuario usuario = usuarioDao
					.obter((String) new JWTVerifier(UsuarioRestController.SECRET).verify(token).get("login_usuario"));
			if (usuario != null) {
				JSONObject job = new JSONObject();
				job.put("nome", usuario.getNome());
				job.put("operacoes", usuario.getOperacoes());
				job.put("armazenamento", usuario.getArmazenamento());
				job.put("ocupado", usuario.getOcupado());
				job.put("privilegio", usuario.getPrivilegio().getNome());
				return ResponseEntity.ok().body(job.toString());
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/gerenciamento/usuario/estacoes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<List<Estacao>> listarEstacoes(@RequestHeader(name = "Authorization") String token) {
		try {
			Usuario usuario = usuarioDao.obter((String) new JWTVerifier(UsuarioRestController.SECRET).verify(token).get("login_usuario"));
			if (usuario != null) {
				return ResponseEntity.ok().body(usuario.getEstacaos());
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
			Usuario usuario = new Usuario();
			usuario.setLogin(job.getString("login"));
			usuario.setSenha(job.getString("senha"));
			boolean expira = job.getBoolean("expira");
			
			usuario = usuarioDao.logar(usuario);
			if (usuario != null) {
				// data de emissão do token
				long iat = System.currentTimeMillis() / 1000;
				long exp = iat + EXPIRES_IN_SECONDS;
				JWTSigner signer = new JWTSigner(SECRET);
				HashMap<String, Object> claims = new HashMap<>();
				claims.put("iat", iat);
				claims.put("exp", expira ? exp : Long.MAX_VALUE);
				claims.put("iss", ISSUER);
				claims.put("login_usuario", usuario.getLogin());

				String jwt = signer.sign(claims);

				return ResponseEntity.status(HttpStatus.OK).body(new JSONObject().put("token", jwt).toString());
			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new JSONObject().put("erro", "Usuário ou senha inválidos").toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
