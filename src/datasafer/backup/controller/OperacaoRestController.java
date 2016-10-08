package datasafer.backup.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import datasafer.backup.dao.OperacaoDao;
import datasafer.backup.model.Operacao;

@RestController
public class OperacaoRestController {

	@Autowired
	private OperacaoDao operacaoDao;
	
	@RequestMapping(value = "/operacao/{idOperacao}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Operacao> obter(@PathVariable Long idOperacao){
		Operacao operacao = operacaoDao.obter(idOperacao);
		if (operacao != null) {
			return ResponseEntity.ok().body(operacao);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
}
