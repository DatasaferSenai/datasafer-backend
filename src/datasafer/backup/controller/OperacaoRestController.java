package datasafer.backup.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWTVerifier;

import datasafer.backup.dao.OperacaoDao;
import datasafer.backup.model.Operacao;

@RestController
public class OperacaoRestController {

	@Autowired
	private OperacaoDao operacaoDao;

	@RequestMapping(value = "/gerenciamento/operacao", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<String> obter(@RequestHeader(name = "Authorization") String token, @RequestHeader(name = "estacao") String nome_estacao,
			@RequestHeader(name = "backup") String nome_backup, @RequestHeader(name = "operacao") Date data_operacao) {
		try {
			Operacao operacao = operacaoDao.obter((String) new JWTVerifier(UsuarioRestController.SECRET).verify(token)
																										.get("login_usuario"),
					nome_estacao, nome_backup, data_operacao);

			if (operacao != null) {
				JSONObject jobj = new JSONObject();

				jobj.put("data", new SimpleDateFormat("dd/mm/yyyy hh:MM:ss").format(operacao.getData()));
				jobj.put("status", operacao	.getStatus()
											.toString());
				jobj.put("tamanho", operacao.getTamanho());

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

}
