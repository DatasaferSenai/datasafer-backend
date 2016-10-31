package datasafer.backup.controller;

import java.util.Set;

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

import com.auth0.jwt.JWTVerifier;

import datasafer.backup.dao.PrivilegioDao;
import datasafer.backup.model.Privilegio;
import datasafer.backup.model.Usuario;

@RestController
public class PrivilegioRestController {

	@Autowired
	private PrivilegioDao privilegioDao;

	@RequestMapping(value = "/gerenciamento/privilegio", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Privilegio> obter(HttpServletRequest req, @RequestHeader(name = "Authorization") String token,
			@RequestHeader(name = "privilegio") String nome_privilegio) {
		try {

			String login_solicitante = (String) new JWTVerifier(UsuarioRestController.SECRET)	.verify(token)
																								.get("login_usuario");

			String login_proprietario = req.getHeader("usuario") != null ? req.getHeader("usuario") : login_solicitante;

			Privilegio privilegio = privilegioDao.obter(login_proprietario, nome_privilegio);
			if (privilegio != null) {
				return ResponseEntity	.ok()
										.body(privilegio);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/gerenciamento/privilegio", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Void> inserir(HttpServletRequest req, @RequestHeader(name = "Authorization") String token, @RequestBody Privilegio privilegio) {
		try {

			String login_solicitante = (String) new JWTVerifier(UsuarioRestController.SECRET)	.verify(token)
																								.get("login_usuario");

			String login_proprietario = req.getHeader("usuario") != null ? req.getHeader("usuario") : login_solicitante;

			if (privilegio != null) {
				privilegioDao.inserir(login_solicitante, login_proprietario, privilegio);
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/gerenciamento/privilegio", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Void> modificar(HttpServletRequest req, @RequestHeader(name = "Authorization") String token, @RequestBody String corpo_privilegio) {
		try {

			String login_solicitante = (String) new JWTVerifier(UsuarioRestController.SECRET)	.verify(token)
																								.get("login_usuario");

			String login_proprietario = req.getHeader("usuario") != null ? req.getHeader("usuario") : login_solicitante;

			String nome_privilegio = req.getHeader("privilegio");
			
			Privilegio privilegio = privilegioDao.obter(login_proprietario, nome_privilegio);
			if ( privilegio != null) {
				JSONObject jobj = new JSONObject(corpo_privilegio);

				if (jobj.has("nome")) {
					privilegio.setNome(jobj.getString("nome"));
				}
				if (jobj.has("persmissoes")) {
					@SuppressWarnings("unchecked")
					Set<Privilegio.Permissao> permissoes = (Set<Privilegio.Permissao>) jobj.get("persmissoes");
					privilegio.setPermissoes(permissoes);
				}
				privilegioDao.modificar(login_solicitante, privilegio);

				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
