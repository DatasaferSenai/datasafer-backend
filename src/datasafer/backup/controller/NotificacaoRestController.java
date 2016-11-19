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

import datasafer.backup.dao.NotificacaoDao;
import datasafer.backup.model.Notificacao;
import datasafer.backup.model.Usuario;

@RestController
public class NotificacaoRestController {

	@Autowired
	private NotificacaoDao notificacaoDao;

	@RequestMapping(value = "/notificacoes", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<String> insereNotificacao(@RequestAttribute Usuario solicitante,
													@RequestBody Notificacao notificacao) {
		try {
			notificacaoDao.insereNotificacao(solicitante, notificacao);

			return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void enviaNotificacao() {

	}
}
