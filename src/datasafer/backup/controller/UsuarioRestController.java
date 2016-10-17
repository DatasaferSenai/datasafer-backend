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

import datasafer.backup.bo.PrivilegioBo;
import datasafer.backup.bo.UsuarioBo;
import datasafer.backup.model.Host;
import datasafer.backup.model.Privilegio;
import datasafer.backup.model.Usuario;

@RestController
public class UsuarioRestController {
	public static final String SECRET = "J0pjgqSuFXmCw8RQMPWaYT8XSBTneN0nDfMjLgUQ37Tp6l6I2SjQmhn5i7jCLZpO";
	public static final String ISSUER = "http://www.sp.senai.br";
	public static final long EXPIRES_IN_SECONDS = 60 * 60 * 24;

	@Autowired
	private UsuarioBo usuarioBo;

	@RequestMapping(value = "/gerenciamento/usuario", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<String> inserirUsuario(@RequestHeader(name = "usuario") String login_usuario,
			@RequestBody Usuario novo_usuario) {
		try {
			usuarioBo.inserirUsuario(login_usuario, novo_usuario);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/gerenciamento/usuario", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Usuario> obterUsuario(@RequestHeader(name = "usuario") String login_usuario) {
		try {
			Usuario usuario = usuarioBo.obterUsuario(login_usuario);
			if (usuario != null) {
				return ResponseEntity.ok().body(usuario);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/gerenciamento/usuario/hosts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<List<Host>> listarOperacoes(@RequestHeader(name = "usuario") String login_usuario) {
		try {
			Usuario usuario = usuarioBo.obterUsuario(login_usuario);
			if (usuario != null) {
				return ResponseEntity.ok().body(usuario.getHosts());
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/gerenciamento/login", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<String> logar(@RequestBody Usuario usuario) {
		try {
			usuario = usuarioBo.logar(usuario);
			if (usuario != null) {
				// data de emissão do token
				long iat = System.currentTimeMillis() / 1000;
				long exp = iat + EXPIRES_IN_SECONDS;
				JWTSigner signer = new JWTSigner(SECRET);
				HashMap<String, Object> claims = new HashMap<>();
				claims.put("iat", iat);
				claims.put("exp", exp);
				claims.put("iss", ISSUER);
				claims.put("login_usuario", usuario.getLogin());

				String jwt = signer.sign(claims);

				JSONObject token = new JSONObject();
				token.put("token", jwt);

				return ResponseEntity.ok().body(token.toString());
			} else {
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
