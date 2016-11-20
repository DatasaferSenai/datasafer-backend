package datasafer.backup.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import datasafer.backup.dao.BackupDao;
import datasafer.backup.model.Backup;
import datasafer.backup.model.Estacao;
import datasafer.backup.model.Usuario;

@RestController
public class BackupRestController {

	@Autowired
	private BackupDao backupDao;

	@RequestMapping(value = "/backup", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> insereBackup(	@RequestAttribute Usuario solicitante,
												@RequestAttribute Usuario usuario,
												@RequestAttribute Estacao estacao,
												@RequestBody Backup backup) {

		backupDao.insereBackup(solicitante, usuario, estacao, backup);
		return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
	}

	@RequestMapping(value = "/backup", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> obtemBackup(	@RequestAttribute Usuario usuario,
												@RequestAttribute Estacao estacao,
												@RequestAttribute Backup backup) {

		return new ResponseEntity<>(backupDao.carregaInfos(backup), HttpStatus.OK);
	}

	@RequestMapping(value = "/backup/operacoes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> obtemOperacoes(	@RequestAttribute Usuario usuario,
													@RequestAttribute Estacao estacao,
													@RequestAttribute Backup backup) {

		return new ResponseEntity<>(backupDao.obtemOperacoes(backup), HttpStatus.OK);
	}
}
