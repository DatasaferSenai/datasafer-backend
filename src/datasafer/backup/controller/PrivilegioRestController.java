package datasafer.backup.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import datasafer.backup.dao.PrivilegioDao;
import datasafer.backup.model.Privilegio;

@RestController
public class PrivilegioRestController {

	@Autowired
	private PrivilegioDao privilegioDao;

	@RequestMapping(value = "/gerenciamento/privilegio", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<String> obter(@RequestHeader(name = "privilegio") String nome_privilegio) {
		try {
			Privilegio privilegio = privilegioDao.obter(nome_privilegio);
			if (privilegio != null) {
				JSONObject job = new JSONObject();
				job.put(null, privilegio.getProprietario());
				job.put(null, privilegio.getNome());
				job.put(null, privilegio.getPermissoes());

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

//	@RequestMapping(value = "/gerenciamento/privilegios", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//	public ResponseEntity<String> listar(@RequestHeader(name = "Authorization") String token) {
//		try {
//			List<Privilegio> privilegios = privilegioDao.listar((String) new JWTVerifier(UsuarioRestController.SECRET)	.verify(token)
//																														.get("login_usuario"));
//			if (privilegios != null) {
//				JSONArray jarray = new JSONArray();
//				for (Privilegio p : privilegios) {
//					JSONObject job = new JSONObject();
//					job.put("nome", p.getNome());
//					job.put("permissoes", p.getPermissoes());
//					jarray.put(job);
//				}
//				return ResponseEntity	.ok()
//										.body(jarray.toString());
//			} else {
//				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}

}
