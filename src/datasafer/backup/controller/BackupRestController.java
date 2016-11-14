package datasafer.backup.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import datasafer.backup.dao.BackupDao;
import datasafer.backup.model.Backup;
import datasafer.backup.model.Usuario;

@CrossOrigin(maxAge = 3600)
@RestController
public class BackupRestController {

	@Autowired
	private BackupDao backupDao;

	@RequestMapping(value = "/gerenciamento/backup", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> inserirBackup(@RequestAttribute Usuario solicitante,
												@RequestAttribute Usuario usuario,
												@RequestHeader("estacao") String nome_estacao,
												@RequestBody Backup backup) {
		try {
			try {
				backupDao.inserir(solicitante.getLogin(), usuario.getLogin(), nome_estacao, backup);
				return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
			} catch (DataRetrievalFailureException e) {
				return new ResponseEntity<>(new JSONObject().put("erro", e.getMessage())
															.toString(),
						HttpStatus.NOT_FOUND);
			} catch (DataIntegrityViolationException e) {
				return new ResponseEntity<>(new JSONObject().put("erro", e.getMessage())
															.toString(),
						HttpStatus.CONFLICT);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/gerenciamento/backup", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> obterBackup(	@RequestAttribute Usuario usuario,
												@RequestHeader(name = "estacao") String nome_estacao,
												@RequestHeader(name = "backup") String nome_backup) {
		try {

			try {
				return new ResponseEntity<>(backupDao.obter(usuario.getLogin(), nome_estacao, nome_backup), HttpStatus.OK);
			} catch (DataRetrievalFailureException e) {
				return new ResponseEntity<>(new JSONObject().put("erro", e.getMessage())
															.toString(),
						HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/gerenciamento/backup/operacoes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> obterOperacoes(	@RequestAttribute Usuario usuario,
													@RequestHeader(name = "estacao") String nome_estacao,
													@RequestHeader(name = "backup") String nome_backup) {
		try {

			try {
				return new ResponseEntity<>(backupDao	.obter(usuario.getLogin(), nome_estacao, nome_backup)
														.getOperacoes(),
						HttpStatus.OK);
			} catch (DataRetrievalFailureException e) {
				return new ResponseEntity<>(new JSONObject().put("erro", e.getMessage())
															.toString(),
						HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
