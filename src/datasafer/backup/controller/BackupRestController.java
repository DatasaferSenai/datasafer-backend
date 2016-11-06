package datasafer.backup.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWTVerifier;

import datasafer.backup.bo.BackupBo;
import datasafer.backup.model.Backup;
import datasafer.backup.model.Operacao;

@CrossOrigin(maxAge = 3600)
@RestController
public class BackupRestController {

	@Autowired
	private BackupBo backupBo;

	@RequestMapping(value = "/gerenciamento/backup", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Void> inserir(HttpServletRequest req, @RequestHeader(name = "Authorization") String token,
			@RequestHeader(name = "estacao") String nome_estacao, @RequestBody Backup backup) {
		try {

			String login_solicitante = (String) new JWTVerifier(UsuarioRestController.SECRET)	.verify(token)
																								.get("login_usuario");

			String login_proprietario = req.getHeader("usuario") != null ? req.getHeader("usuario") : login_solicitante;

			try {
				backupBo.inserir(login_solicitante, login_proprietario, nome_estacao, backup);
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			} catch (DataRetrievalFailureException e) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			} catch (DataIntegrityViolationException e) {
				return new ResponseEntity<>(HttpStatus.CONFLICT);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/gerenciamento/backup", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Backup> obter(HttpServletRequest req, @RequestHeader(name = "Authorization") String token,
			@RequestHeader(name = "estacao") String nome_estacao, @RequestHeader(name = "backup") String nome_backup) {
		try {

			String login_solicitante = (String) new JWTVerifier(UsuarioRestController.SECRET)	.verify(token)
																								.get("login_usuario");

			String login_proprietario = req.getHeader("usuario") != null ? req.getHeader("usuario") : login_solicitante;

			try {
				return new ResponseEntity<>(backupBo.obter(login_proprietario, nome_estacao, nome_backup), HttpStatus.OK);
			} catch (DataRetrievalFailureException e) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/gerenciamento/backup/operacoes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<List<Operacao>> listarOperacoes(HttpServletRequest req, @RequestHeader(name = "Authorization") String token,
			@RequestHeader(name = "estacao") String nome_estacao, @RequestHeader(name = "backup") String nome_backup) {
		try {

			String login_solicitante = (String) new JWTVerifier(UsuarioRestController.SECRET)	.verify(token)
																								.get("login_usuario");

			String login_proprietario = req.getHeader("usuario") != null ? req.getHeader("usuario") : login_solicitante;

			try {
				return new ResponseEntity<>(backupBo.obter(login_proprietario, nome_estacao, nome_backup)
													.getOperacoes(),
						HttpStatus.OK);
			} catch (DataRetrievalFailureException e) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
