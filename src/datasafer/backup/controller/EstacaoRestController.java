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

import datasafer.backup.dao.EstacaoDao;
import datasafer.backup.dao.utility.Carregador;
import datasafer.backup.model.Backup;
import datasafer.backup.model.Estacao;
import datasafer.backup.model.Usuario;

@RestController
public class EstacaoRestController {

	@Autowired
	private EstacaoDao estacaoDao;
	@Autowired
	private Carregador carregador;

	@RequestMapping(value = "/estacao", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> obtem(@RequestAttribute Usuario usuario,
										@RequestAttribute Estacao estacao) {

		return new ResponseEntity<>(this.carregador.carregaTransientes(estacao), HttpStatus.OK);
	}

	@RequestMapping(value = "/estacao/backups", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> listarBackups(@RequestAttribute Usuario usuario,
												@RequestAttribute Estacao estacao) {

		return new ResponseEntity<>(this.carregador.carregaTransientes(this.estacaoDao.obtemBackups(usuario, estacao)), HttpStatus.OK);
	}

	@RequestMapping(value = "/estacao/backups", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> insereBackup(	@RequestAttribute Usuario solicitante,
												@RequestAttribute Usuario usuario,
												@RequestAttribute Estacao estacao,
												@RequestBody Backup backup) {

		estacaoDao.insereBackup(solicitante, usuario, estacao, backup);
		return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
	}

}
