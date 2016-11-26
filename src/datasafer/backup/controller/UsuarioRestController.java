package datasafer.backup.controller;

import java.nio.file.AccessDeniedException;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import datasafer.backup.dao.UsuarioDao;
import datasafer.backup.dao.utility.Carregador;
import datasafer.backup.model.Estacao;
import datasafer.backup.model.Usuario;

@RestController
public class UsuarioRestController {

	@Autowired
	private UsuarioDao usuarioDao;
	@Autowired
	private Carregador carregador;

	@RequestMapping(value = "/usuario", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
					produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> insereUsuario(@RequestAttribute Usuario solicitante,
												@RequestAttribute Usuario usuario,
												@RequestBody Usuario novo)	throws DataIntegrityViolationException, AccessDeniedException,
																			ConstraintViolationException {

		usuarioDao.insereUsuario(solicitante, usuario, novo);
		return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
	}

	@RequestMapping(value = "/usuario", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> obtemUsuario(@RequestAttribute Usuario usuario) {

		return new ResponseEntity<>(carregador.carregaTransientes(usuario), HttpStatus.OK);
	}

	@RequestMapping(value = "/usuario", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
					produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> modificaUsuario(	@RequestAttribute Usuario solicitante,
													@RequestAttribute Usuario usuario,
													@RequestBody Usuario valores) {

		usuarioDao.modificaUsuario(solicitante, usuario, valores);
		return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
	}

	@RequestMapping(value = "/usuario", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> inativaUsuario(	@RequestAttribute Usuario solicitante,
													@RequestAttribute Usuario usuario) {

		usuario.setStatus(Usuario.Status.INATIVO);
		usuarioDao.modificaUsuario(solicitante, usuario, usuario);
		return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
	}

	@RequestMapping(value = "/usuario/usuarios", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> obtemUsuarios(@RequestAttribute Usuario usuario) {

		return new ResponseEntity<>(carregador.carregaTransientes(usuarioDao.obtemColaboradores(usuario)), HttpStatus.OK);
	}

	@RequestMapping(value = "/usuario/estacoes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> obtemEstacoes(@RequestAttribute Usuario usuario) {

		return new ResponseEntity<>(carregador.carregaTransientes(usuarioDao.obtemEstacoes(usuario)), HttpStatus.OK);
	}

	@RequestMapping(value = "/usuario/backups", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> obtemBackups(@RequestAttribute Usuario usuario) {

		return new ResponseEntity<>(carregador.carregaTransientes(usuarioDao.obtemBackups(usuario)), HttpStatus.OK);
	}

	@RequestMapping(value = "/usuario/estacoes", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> insereEstacao(@RequestAttribute Usuario solicitante,
												@RequestAttribute Usuario usuario,
												@RequestBody Estacao estacao) {

		usuarioDao.insereEstacao(solicitante, usuario, estacao);
		return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
	}
	
	
	
}
