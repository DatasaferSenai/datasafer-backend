package datasafer.backup.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import datasafer.backup.dao.OperacaoDao;
import datasafer.backup.model.Backup;
import datasafer.backup.model.Operacao;
import datasafer.backup.model.Usuario;

@CrossOrigin(maxAge = 3600)
@RestController
public class OperacaoRestController {

	@Autowired
	private OperacaoDao operacaoDao;

	@RequestMapping(value = "/gerenciamento/operacao", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> obtem(@RequestAttribute Operacao operacao) {
		try {
			return new ResponseEntity<>(operacao, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/gerenciamento/operacao", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> insere(	@RequestAttribute Usuario solicitante,
											@RequestAttribute Backup backup,
											@RequestBody Operacao operacao) {
		try {

			try {
				operacaoDao.insereOperacao(solicitante, backup, operacao);
				return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
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

}
