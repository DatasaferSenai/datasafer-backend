package datasafer.backup.controller;

import java.util.ArrayList;
import java.util.List;

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

import datasafer.backup.bo.BackupBo;
import datasafer.backup.model.Backup;
import datasafer.backup.model.Operacao;

@CrossOrigin(maxAge = 3600)
@RestController
public class BackupRestController {

	@Autowired
	private BackupBo backupBo;

	@RequestMapping(value = "/gerenciamento/backup", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<String> inserir(	@RequestAttribute String login_solicitante,
											@RequestAttribute String login_usuario,
											@RequestHeader(name = "estacao") String nome_estacao,
											@RequestBody Backup backup) {
		try {
			try {
				backupBo.inserir(login_solicitante, login_usuario, nome_estacao, backup);
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
	public ResponseEntity<String> obter(@RequestAttribute String login_solicitante,
										@RequestAttribute String login_usuario,
										@RequestHeader(name = "estacao") String nome_estacao,
										@RequestHeader(name = "backup") String nome_backup) {
		try {

			try {
				return new ResponseEntity<>(new JSONObject(backupBo.obter(login_usuario, nome_estacao, nome_backup)).toString(), HttpStatus.OK);
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
	public ResponseEntity<String> listarOperacoes(	@RequestAttribute String login_solicitante,
													@RequestAttribute String login_usuario,
													@RequestHeader(name = "estacao") String nome_estacao,
													@RequestHeader(name = "backup") String nome_backup) {
		try {

			try {
				
				Backup backup = backupBo.obter(login_usuario, nome_estacao, nome_backup);
				List<Operacao> operacoes = new ArrayList<Operacao>();
				
				for(Operacao o : backup.getOperacoes()){
					operacoes.add(o);
				}
				return new ResponseEntity<>(new JSONObject(operacoes).toString(),
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
