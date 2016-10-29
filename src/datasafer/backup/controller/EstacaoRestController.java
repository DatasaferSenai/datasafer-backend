package datasafer.backup.controller;

import java.text.SimpleDateFormat;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
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
import datasafer.backup.model.Operacao;

@RestController
public class EstacaoRestController {

	@Autowired
	private EstacaoDao estacaoDao;

	@RequestMapping(value = "/gerenciamento/estacao", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<String> obter(@RequestHeader(name = "Authorization") String token, @RequestHeader(name = "estacao") String nome_estacao) {
		try {
			Estacao estacao = estacaoDao.obter((String) new JWTVerifier(UsuarioRestController.SECRET)	.verify(token)
																										.get("login_usuario"),
					nome_estacao);
			if (estacao != null) {
				JSONObject jobj = new JSONObject();

				jobj.put("nome", estacao.getNome());
				jobj.put("descricao", estacao.getDescricao());

				return ResponseEntity	.ok()
										.body(jobj.toString());
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
	public ResponseEntity<String> listarBackups(@RequestHeader(name = "Authorization") String token, @RequestHeader(name = "estacao") String nome_estacao) {
		try {
			Estacao estacao = estacaoDao.obter((String) new JWTVerifier(UsuarioRestController.SECRET)	.verify(token)
																										.get("login_usuario"),
					nome_estacao);
			if (estacao != null) {
				JSONArray jarray = new JSONArray();

				for (Backup b : estacao.getBackups()) {
					JSONObject jobj = new JSONObject();

					jobj.put("nome", b.getNome());
					jobj.put("descricao", b.getDescricao());
					jobj.put("pasta", b.getPasta());
					jobj.put("frequencia", b.getFrequencia()
											.toString());
					jobj.put("intervalo", new SimpleDateFormat("hh:MM:ss").format(b.getIntervalo()));
					jobj.put("inicio", new SimpleDateFormat("dd/mm/yyyy hh:MM:ss").format(b.getInicio()));

					jarray.put(jobj);
				}
				return ResponseEntity	.ok()
										.body(jarray.toString());
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/gerenciamento/estacao/operacoes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<List<Operacao>> listarOperacoes(@RequestHeader(name = "Authorization") String token,
			@RequestHeader(name = "estacao") String nome_estacao) {
		try {
			Estacao estacao = estacaoDao.obter((String) new JWTVerifier(UsuarioRestController.SECRET)	.verify(token)
																										.get("login_usuario"),
					nome_estacao);
			if (estacao != null) {

				return ResponseEntity	.ok()
										.body(estacaoDao.listarOperacoes(estacao.getProprietario()
																				.getLogin(),
												estacao.getNome()));
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
