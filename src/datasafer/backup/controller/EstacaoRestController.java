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
import datasafer.backup.dao.EstacaoDao;
import datasafer.backup.model.Estacao;
import datasafer.backup.model.Usuario;

@RestController
public class EstacaoRestController {

	@Autowired
	private EstacaoDao estacaoDao;
	@Autowired
	private BackupDao backupDao;

	@RequestMapping(value = "/estacao", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> obtem(@RequestAttribute Usuario usuario,
										@RequestAttribute Estacao estacao) {

		return new ResponseEntity<>(estacaoDao.carregaInfos(usuario, estacao), HttpStatus.OK);
	}

	@RequestMapping(value = "/estacao", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> insere(	@RequestAttribute Usuario solicitante,
											@RequestAttribute Usuario usuario,
											@RequestBody Estacao estacao) {

		estacaoDao.insereEstacao(solicitante, usuario, estacao);
		return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
	}

	@RequestMapping(value = "/estacao/backups", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> listarBackups(@RequestAttribute Usuario usuario,
												@RequestAttribute Estacao estacao) {

		return new ResponseEntity<>(backupDao.carregaInfos(estacaoDao.obtemBackups(usuario, estacao)), HttpStatus.OK);
	}

}
