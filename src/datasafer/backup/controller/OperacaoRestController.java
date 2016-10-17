package datasafer.backup.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import datasafer.backup.bo.OperacaoBo;
import datasafer.backup.bo.PrivilegioBo;
import datasafer.backup.model.Operacao;
import datasafer.backup.model.Privilegio;
import datasafer.backup.model.Usuario;

@RestController
public class OperacaoRestController {

	@Autowired
	private OperacaoBo operacaoBo;
	@Autowired
	private PrivilegioBo privilegioBo;

	@RequestMapping(value = "/gerenciamento/operacao", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Operacao> obterOperacao(@RequestHeader(name = "usuario") String login_usuario,
			@RequestHeader(name = "host") String nome_host, @RequestHeader(name = "backup") String nome_backup,
			@RequestHeader(name = "operacao") Date data_operacao) {
		try {
			Operacao operacao = operacaoBo.obterOperacao(login_usuario, nome_host, nome_backup, data_operacao);
			if (operacao != null) {
				return ResponseEntity.ok().body(operacao);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
