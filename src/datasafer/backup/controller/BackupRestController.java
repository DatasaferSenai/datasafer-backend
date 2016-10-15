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

import datasafer.backup.bo.BackupBo;
import datasafer.backup.model.Backup;
import datasafer.backup.model.Operacao;

@RestController
public class BackupRestController {

	@Autowired
	private BackupBo backupBo;

	@RequestMapping(value = "/{nome_usuario}/{nome_host}/{nome_backup}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Backup> obter(@PathVariable String nome_usuario, @PathVariable String nome_host,
			@PathVariable String nome_backup) {
		try {
			Backup backup = backupBo.obter(nome_usuario, nome_host, nome_backup);
			if (backup != null) {
				return ResponseEntity.ok().body(backup);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	
	@RequestMapping(value = "/{nome_usuario}/{nome_host}/{nome_backup}/operacoes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<List<Operacao>> listarOperacoes(@PathVariable String nome_usuario, @PathVariable String nome_host,
			@PathVariable String nome_backup) {
		try {
			Backup backup = backupBo.obter(nome_usuario, nome_host, nome_backup);
			if (backup != null) {
				return ResponseEntity.ok().body(backup.getOperacoes());
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
