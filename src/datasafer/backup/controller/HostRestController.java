package datasafer.backup.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import datasafer.backup.dao.HostDao;
import datasafer.backup.model.Host;

@RestController
public class HostRestController {

	@Autowired
	private HostDao hostDao;
	
	@RequestMapping(value = "/host/{id_host}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Host> obter(@PathVariable Long id_host){
		Host host = hostDao.obter(id_host);
		if (host != null) {
			return ResponseEntity.ok().body(host);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@RequestMapping(value = "/host/{nome_usuario}/{nome_host}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Host> obter(@PathVariable String nome_usuario, @PathVariable String nome_host){
		Host host = hostDao.obter(nome_usuario,nome_host);
		if (host != null) {
			return ResponseEntity.ok().body(host);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
}
