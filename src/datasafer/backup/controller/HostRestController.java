package datasafer.backup.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import datasafer.backup.bo.HostBo;
import datasafer.backup.model.Backup;
import datasafer.backup.model.Host;

@RestController
public class HostRestController {

	@Autowired
	private HostBo hostBo;

	@RequestMapping(value = "/{nome_usuario}/{nome_host}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Host> obter(@PathVariable String nome_usuario, @PathVariable String nome_host) {
		try {
			Host host = hostBo.obter(nome_usuario, nome_host);
			if (host != null) {
				return ResponseEntity.ok().body(host);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/{nome_usuario}/{nome_host}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Void> inserirBackup(@PathVariable String nome_usuario, @PathVariable String nome_host){
		try {
			Host host = hostBo.obter(nome_usuario, nome_host);
			if (host != null) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/{nome_usuario}/{nome_host}/backups", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<List<Backup>> listarBackups(@PathVariable String nome_usuario, @PathVariable String nome_host) {
		try {
			Host host = hostBo.obter(nome_usuario, nome_host);
			if (host != null) {
				return ResponseEntity.ok().body(host.getBackups());
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
