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

import datasafer.backup.dao.EstacaoDao;
import datasafer.backup.model.Backup;
import datasafer.backup.model.Estacao;

@CrossOrigin(maxAge = 3600)
@RestController
public class EstacaoRestController {

	@Autowired
	private EstacaoDao estacaoDao;

	@RequestMapping(value = "/gerenciamento/estacao", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> obter(@RequestAttribute String login_usuario,
										@RequestHeader(name = "estacao") String nome_estacao) {
		try {

			try {
				return new ResponseEntity<>(new JSONObject(estacaoDao.obter(nome_estacao)).toString(), HttpStatus.OK);
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

	@RequestMapping(value = "/gerenciamento/estacao", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> inserir(	@RequestAttribute String login_solicitante,
											@RequestAttribute String login_usuario,
											@RequestBody Estacao estacao) {
		try {

			try {

				estacaoDao.inserir(login_solicitante, login_usuario, estacao);
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

	@RequestMapping(value = "/gerenciamento/estacao/backups", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> listarBackups(@RequestAttribute String login_usuario,
												@RequestHeader(name = "estacao") String nome_estacao) {
		try {

			Estacao estacao = estacaoDao.obter(nome_estacao);

			if (estacao != null) {

				List<Backup> backups = new ArrayList<Backup>();
				for (Backup b : estacao.getBackups()) {
					if (b	.getProprietario()
							.getLogin()
							.equals(login_usuario)) {
						backups.add(b);
					}
				}

				return ResponseEntity	.ok()
										.body(backups);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
