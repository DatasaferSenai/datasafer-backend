package datasafer.backup.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import datasafer.backup.bo.BackupBo;
import datasafer.backup.bo.PrivilegioBo;
import datasafer.backup.model.Backup;
import datasafer.backup.model.Host;
import datasafer.backup.model.Operacao;

@RestController
public class BackupRestController {

	@Autowired
	private BackupBo backupBo;
	@Autowired
	private PrivilegioBo privilegioBo;
	
	@RequestMapping(value = "/gerenciamento/backup", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Void> inserirBackup(@RequestHeader(name="usuario") String login_usuario, @RequestHeader(name="host") String nome_host, @RequestBody Backup backup){
		try {
			backupBo.inserirBackup(login_usuario, nome_host, backup);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/gerenciamento/backup", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Backup> obter(@RequestHeader(name="usuario") String login_usuario, @RequestHeader(name="host") String nome_host,
			@RequestHeader(name="backup") String nome_backup) {
		try {
			Backup backup = backupBo.obterBackup(login_usuario, nome_host, nome_backup);
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

	
	@RequestMapping(value = "/gerenciamento/backup/operacoes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<List<Operacao>> listarOperacoes(@RequestHeader(name="usuario") String login_usuario, @RequestHeader(name="host") String nome_host,
			@RequestHeader(name="backup") String nome_backup) {
		try {
			Backup backup = backupBo.obterBackup(login_usuario, nome_host, nome_backup);
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
