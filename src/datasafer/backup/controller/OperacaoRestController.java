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

import datasafer.backup.controller.utlility.NotificadorOneSignal;
import datasafer.backup.dao.OperacaoDao;
import datasafer.backup.dao.UsuarioDao;
import datasafer.backup.model.Backup;
import datasafer.backup.model.Operacao;
import datasafer.backup.model.Usuario;

@RestController
public class OperacaoRestController {

	@Autowired
	private UsuarioDao usuarioDao;
	@Autowired
	private OperacaoDao operacaoDao;

	@RequestMapping(value = "/operacao", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> insereOperacao(	@RequestAttribute Usuario solicitante,
													@RequestAttribute Usuario usuario,
													@RequestAttribute Backup backup,
													@RequestBody Operacao operacao) {

		operacaoDao.insereOperacao(solicitante, backup, operacao);

		if (operacao.getStatus()
					.equals(Operacao.Status.SUCESSO)
				|| operacao	.getStatus()
							.equals(Operacao.Status.FALHA)) {
			NotificadorOneSignal.envia(	usuarioDao.obtemNotificacoes(backup.getProprietario()),
										"O backup " + backup.getNome() + " do " + backup.getEstacao()
																						.getNome()
												+ (operacao	.getStatus()
															.equals(Operacao.Status.SUCESSO)	? " foi concluído com " + operacao.getStatus()
																								: " falhou"));
		}

		return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
	}

}
