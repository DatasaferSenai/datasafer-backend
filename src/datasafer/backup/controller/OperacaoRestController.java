package datasafer.backup.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
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

import datasafer.backup.dao.OperacaoDao;
import datasafer.backup.model.Operacao;

@CrossOrigin(maxAge = 3600)
@RestController
public class OperacaoRestController {

	@Autowired
	private OperacaoDao operacaoDao;

	@RequestMapping(value = "/gerenciamento/operacao", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Operacao> obter(	@RequestAttribute String login_solicitante,
											@RequestAttribute String login_usuario,
											@RequestHeader(name = "estacao") String nome_estacao,
											@RequestHeader(name = "backup") String nome_backup,
											@RequestHeader(name = "operacao") Date data_operacao) {
		try {

			Operacao operacao = operacaoDao.obter(login_usuario, nome_estacao, nome_backup, data_operacao);
			if (operacao != null) {
				return ResponseEntity	.ok()
										.body(operacao);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/gerenciamento/operacao", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Void> inserir(@RequestAttribute String login_solicitante,
										@RequestAttribute String login_usuario,
										@RequestHeader(name = "estacao") String nome_estacao,
										@RequestHeader(name = "backup") String nome_backup,
										@RequestBody Operacao operacao) {
		try {

			try {
				operacaoDao.inserir(login_solicitante, login_usuario, nome_estacao, nome_backup, operacao);
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			} catch (Exception e) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
