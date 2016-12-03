package datasafer.backup.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import datasafer.backup.controller.utlility.NotificadorOneSignal;
import datasafer.backup.dao.utility.Carregador;
import datasafer.backup.dao.utility.Modificador;
import datasafer.backup.model.Backup;
import datasafer.backup.model.Notificacao;
import datasafer.backup.model.Operacao;
import datasafer.backup.model.Usuario;

@RestController
public class RestControllerGenerico {

	@Autowired
	private Carregador carregador;
	@Autowired
	private Modificador modificador;

	@Transactional
	@RequestMapping(value = "/{objeto}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> obtemGenerico(HttpServletRequest req,
												@RequestAttribute Usuario solicitante,
												@PathVariable String objeto) throws Exception {

		return new ResponseEntity<>(carregador.carregaTransientes(	solicitante,
																	req.getAttribute(objeto)),
									HttpStatus.OK);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/{objeto}/{colecao}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> obtemColecaoGenerico(	HttpServletRequest req,
														@RequestAttribute Usuario solicitante,
														@PathVariable String objeto,
														@PathVariable String colecao) throws Exception {

		return new ResponseEntity<>(carregador.carregaTransientes(	solicitante,
																	carregador.obtemAtributo(solicitante, req.getAttribute(objeto), colecao, List.class)),
									HttpStatus.OK);
	}

	@RequestMapping(value = "/{objeto}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
					produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> modificaGenerico(	HttpServletRequest req,
													@RequestAttribute Usuario solicitante,
													@PathVariable String objeto,
													@RequestBody String valores)	throws Exception {

		modificador.modifica(solicitante, req.getAttribute(objeto), valores);
		return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
	}

	@RequestMapping(value = "/{objeto}/{colecao}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
					produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> insereGenerico(	HttpServletRequest req,
													@RequestAttribute Usuario solicitante,
													@RequestAttribute Usuario usuario,
													@PathVariable String objeto,
													@PathVariable String colecao,
													@RequestBody String valor) throws Exception {

		modificador.insere(solicitante, usuario, req.getAttribute(objeto), colecao, valor);
		return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/backup/operacoes", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
					produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> insereOperacao(	HttpServletRequest req,
													@RequestAttribute Usuario solicitante,
													@RequestAttribute Usuario usuario,
													@RequestParam Backup backup,
													@RequestBody Operacao operacao) throws Exception {

		modificador.insere(solicitante, usuario, backup, "operacoes", operacao);

		if (operacao.getStatus().equals(Operacao.Status.EXECUTADO)
				|| operacao.getStatus().equals(Operacao.Status.RESTAURADO)
				|| operacao.getStatus().equals(Operacao.Status.FALHA)) {
			NotificadorOneSignal.envia(	(List<Notificacao>) carregador.obtemAtributo(solicitante, usuario, "notificoes"),
										"O backup " + backup.getNome() + " do " + backup.getEstacao()
																						.getNome()
												+ (operacao	.getStatus()
															.equals(Operacao.Status.EXECUTADO)	? " foi concluído com " + operacao.getStatus()
																								: " falhou"));
		}

		return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
	}

}
