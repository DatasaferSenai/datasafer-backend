package datasafer.backup.controller;

import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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

import datasafer.backup.dao.BackupDao;
import datasafer.backup.model.Backup;
import datasafer.backup.model.Operacao;

@CrossOrigin(maxAge = 3600)
@RestController
public class BackupRestController {

	@Autowired
	private BackupDao backupDao;

	@RequestMapping(value = "/gerenciamento/backup", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Void> inserir(HttpServletRequest req, @RequestHeader(name = "Authorization") String token,
			@RequestHeader(name = "estacao") String nome_estacao, @RequestBody String corpo_backup) {
		try {

			String login_solicitante = (String) new JWTVerifier(UsuarioRestController.SECRET)	.verify(token)
																								.get("login_usuario");

			String login_proprietario;
			try {
				login_proprietario = req.getHeader("usuario");
			} catch (Exception e) {
				login_proprietario = login_solicitante;
			}

			JSONObject jobj = new JSONObject(corpo_backup);

			Backup backup = new Backup();
			backup.setNome(jobj.getString("nome"));
			backup.setDescricao(jobj.getString("descricao"));
			backup.setPasta(jobj.getString("pasta"));
			backup.setIntervalo(jobj.getInt("intervalo"));
			backup.setInicio(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(jobj.getString("inicio")));

			backupDao.inserir(login_solicitante, login_proprietario, nome_estacao, backup);

			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/gerenciamento/backup", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<String> obter(@RequestHeader(name = "Authorization") String token, @RequestHeader(name = "estacao") String nome_estacao,
			@RequestHeader(name = "backup") String nome_backup) {
		try {

			String login_solicitante = (String) new JWTVerifier(UsuarioRestController.SECRET)	.verify(token)
																								.get("login_usuario");

			Backup backup = backupDao.obter(login_solicitante, nome_estacao, nome_backup);
			if (backup != null) {
				JSONObject jobj = new JSONObject();

				jobj.put("nome", backup.getNome());
				jobj.put("descricao", backup.getDescricao());
				jobj.put("pasta", backup.getPasta());
				jobj.put("intervalo", new SimpleDateFormat("HH:mm:ss").format(backup.getIntervalo()));
				jobj.put("inicio", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(backup.getInicio()));

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

	@RequestMapping(value = "/gerenciamento/backup/operacoes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<String> listarOperacoes(@RequestHeader(name = "Authorization") String token, @RequestHeader(name = "estacao") String nome_estacao,
			@RequestHeader(name = "backup") String nome_backup) {
		try {

			String login_solicitante = (String) new JWTVerifier(UsuarioRestController.SECRET)	.verify(token)
																								.get("login_usuario");

			Backup backup = backupDao.obter(login_solicitante, nome_estacao, nome_backup);
			if (backup != null) {
				JSONArray jarray = new JSONArray();
				for (Operacao o : backup.getOperacoes()) {
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
