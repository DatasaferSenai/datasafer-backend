package datasafer.backup.controller;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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

			String login_solicitante = (String) new JWTVerifier(UsuarioRestController.SECRET)	.verify(token)
																								.get("login_usuario");

			Estacao estacao = estacaoDao.obter(login_solicitante, nome_estacao);
			if (estacao != null) {
				JSONObject jobj = new JSONObject();

				jobj.put("nome", estacao.getNome());
				jobj.put("descricao", estacao.getDescricao());

				JSONObject operacoes_jobj = new JSONObject();
				Arrays	.asList(Operacao.Status.values())
						.forEach(s -> {
							try {
								operacoes_jobj.put(s.toString(), estacaoDao	.listarOperacoes(login_solicitante, nome_estacao)
																			.stream()
																			.filter(o -> o.getStatus() == s)
																			.count());
							} catch (Exception e) {
								e.printStackTrace();
							}
						});
				jobj.put("operacoes", operacoes_jobj);

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
	public ResponseEntity<Void> inserir(HttpServletRequest req, @RequestHeader(name = "Authorization") String token, @RequestBody String corpo_estacao) {
		try {

			String login_solicitante = (String) new JWTVerifier(UsuarioRestController.SECRET)	.verify(token)
																								.get("login_usuario");

			String login_proprietario;
			try {
				login_proprietario = req.getHeader("usuario");
			} catch (Exception e) {
				login_proprietario = login_solicitante;
			}

			JSONObject jobj = new JSONObject(corpo_estacao);

			Estacao estacao = new Estacao();
			estacao.setNome(jobj.getString("nome"));
			estacao.setDescricao(jobj.getString("descricao"));

			try {
				estacaoDao.inserir(login_solicitante, login_proprietario, estacao);
			} catch (Exception e) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

			return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/gerenciamento/estacao/backups", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<String> listarBackups(@RequestHeader(name = "Authorization") String token, @RequestHeader(name = "estacao") String nome_estacao) {
		try {

			String login_solicitante = (String) new JWTVerifier(UsuarioRestController.SECRET)	.verify(token)
																								.get("login_usuario");

			Estacao estacao = estacaoDao.obter(login_solicitante, nome_estacao);
			if (estacao != null) {
				JSONArray jarray = new JSONArray();

				for (Backup b : estacao.getBackups()) {
					JSONObject jobj = new JSONObject();

					jobj.put("nome", b.getNome());
					jobj.put("descricao", b.getDescricao());
					jobj.put("pasta", b.getPasta());
					jobj.put("intervalo", new SimpleDateFormat("HH:mm:ss").format(b.getIntervalo()));
					jobj.put("inicio", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(b.getInicio()));

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
	public ResponseEntity<String> listarOperacoes(@RequestHeader(name = "Authorization") String token, @RequestHeader(name = "estacao") String nome_estacao) {
		try {

			String login_solicitante = (String) new JWTVerifier(UsuarioRestController.SECRET)	.verify(token)
																								.get("login_usuario");

			List<Operacao> operacoes = estacaoDao.listarOperacoes(login_solicitante, nome_estacao);
			if (operacoes != null) {
				JSONArray jarray = new JSONArray();
				for (Operacao o : operacoes) {
					JSONObject jobj = new JSONObject();

					jobj.put("data", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(o.getData()));
					jobj.put("status", o.getStatus()
										.toString());
					jobj.put("tamanho", o.getTamanho());

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

}
