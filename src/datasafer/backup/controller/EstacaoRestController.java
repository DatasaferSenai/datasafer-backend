package datasafer.backup.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWTVerifier;

import datasafer.backup.dao.EstacaoDao;
import datasafer.backup.model.Backup;
import datasafer.backup.model.Estacao;

@RestController
public class EstacaoRestController {

	@Autowired
	private EstacaoDao estacaoDao;

	@RequestMapping(value = "/gerenciamento/estacao", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Estacao> obter(@RequestHeader(name = "Authorization") String token, @RequestHeader(name = "estacao") String nome_estacao) {
		try {
			Estacao estacao = estacaoDao.obter((String) new JWTVerifier(UsuarioRestController.SECRET)	.verify(token)
																										.get("login_usuario"),
					nome_estacao);
			if (estacao != null) {
				return ResponseEntity	.ok()
										.body(estacao);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/gerenciamento/estacao", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Void> inserir(@RequestHeader(name = "Authorization") String token, @RequestBody Estacao estacao) {
		try {
			estacaoDao.inserir(null, (String) new JWTVerifier(UsuarioRestController.SECRET)	.verify(token)
																							.get("login_usuario"),
					estacao);
			if (estacao != null) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/gerenciamento/estacao/backups", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<List<Backup>> listarBackups(@RequestHeader(name = "Authorization") String token,
			@RequestHeader(name = "estacao") String nome_estacao) {
		try {
			Estacao estacao = estacaoDao.obter((String) new JWTVerifier(UsuarioRestController.SECRET)	.verify(token)
																										.get("login_usuario"),
					nome_estacao);
			if (estacao != null) {
				return ResponseEntity	.ok()
										.body(estacao.getBackups());
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
