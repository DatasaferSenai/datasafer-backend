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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import datasafer.backup.dao.UsuarioDao;
import datasafer.backup.model.Backup;
import datasafer.backup.model.Estacao;
import datasafer.backup.model.Usuario;

@CrossOrigin(maxAge = 3600)
@RestController
public class UsuarioRestController {

	@Autowired
	private UsuarioDao usuarioDao;

	@RequestMapping(value = "/gerenciamento/usuario", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> inserirUsuario(	@RequestAttribute String login_solicitante,
													@RequestAttribute String login_usuario,
													@RequestBody Usuario novo) {
		try {

			try {
				usuarioDao.inserir(login_solicitante, login_usuario, novo);
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

	@RequestMapping(value = "/gerenciamento/usuario", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> obterUsuario(@RequestAttribute String login_usuario) {
		try {

			try {
				Usuario usuario = usuarioDao.obter(login_usuario);

				return new ResponseEntity<>(usuario, HttpStatus.OK);
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

	@RequestMapping(value = "/gerenciamento/usuario", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> modificarUsuario(	@RequestAttribute String login_solicitante,
													@RequestAttribute String login_usuario,
													@RequestBody Usuario valores) {
		try {

			try {
				usuarioDao.modificar(login_solicitante, login_usuario, valores);

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

	@RequestMapping(value = "/gerenciamento/usuario/usuarios", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> obterUsuarios(@RequestAttribute String login_usuario) {
		try {

			try {
				Usuario usuario = usuarioDao.obter(login_usuario);
				List<Usuario> colaboradores = new ArrayList<Usuario>(usuario.getColaboradores()
																			.size());
				colaboradores.addAll(usuario.getColaboradores());

				return new ResponseEntity<>(colaboradores, HttpStatus.OK);
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

	@RequestMapping(value = "/gerenciamento/usuario/estacoes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> obterEstacoes(@RequestAttribute String login_usuario) {
		try {

			try {
				Usuario usuario = usuarioDao.obter(login_usuario);
				List<Estacao> estacoes = new ArrayList<Estacao>(usuario	.getEstacoes()
																		.size());
				estacoes.addAll(usuario.getEstacoes());

				return new ResponseEntity<>(estacoes, HttpStatus.OK);
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

	@RequestMapping(value = "/gerenciamento/usuario/backups", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> obterBackups(@RequestAttribute String login_usuario) {
		try {

			try {
				Usuario usuario = usuarioDao.obter(login_usuario);
				List<Backup> backups = new ArrayList<Backup>(usuario.getBackups()
																	.size());
				backups.addAll(usuario.getBackups());

				return new ResponseEntity<>(backups, HttpStatus.OK);
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
}
