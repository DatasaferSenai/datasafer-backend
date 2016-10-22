package datasafer.backup.controller;

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
	public ResponseEntity<Privilegio> obter(@RequestHeader(name = "privilegio") String nome_privilegio) {
		
		try {
			Privilegio privilegio = privilegioDao.obter(nome_privilegio);
			if (privilegio != null) {
				return ResponseEntity.ok().body(privilegio);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
		
}
