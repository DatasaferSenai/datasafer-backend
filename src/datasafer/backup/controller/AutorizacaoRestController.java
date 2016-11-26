package datasafer.backup.controller;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import datasafer.backup.dao.AutorizacaoDao;
import datasafer.backup.dao.UsuarioDao;
import datasafer.backup.model.Autorizacao;
import datasafer.backup.model.Usuario;

@RestController
public class AutorizacaoRestController {

	@Autowired
	private UsuarioDao usuarioDao;
	@Autowired
	private AutorizacaoDao tokenDao;

	@RequestMapping(value = "/login", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
					produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> logar(HttpServletRequest req,
										@RequestBody String corpo_usuario) {
		try {
			JSONObject jobj = new JSONObject(corpo_usuario);

			Usuario usuario = new Usuario();
			usuario.setLogin(jobj.getString("login"));
			usuario.setSenha(jobj.getString("senha"));

			Usuario existente = usuarioDao.obtemUsuario(usuario.getLogin());
			if (existente == null || !existente.getStatus().equals(Usuario.Status.ATIVO)) {
				return new ResponseEntity<>(new JSONObject().put("erro", "Usuário ou senha inválidos")
															.toString(),
											HttpStatus.UNAUTHORIZED);
			}

			Usuario logado = usuarioDao.login(usuario);
			if (logado == null) {
				return new ResponseEntity<>(new JSONObject().put("erro", "Usuário ou senha inválidos")
															.toString(),
											HttpStatus.UNAUTHORIZED);
			}

			if (!logado.getStatus().equals(Usuario.Status.ATIVO)) {
				return new ResponseEntity<>(new JSONObject().put("erro", logado.getStatus())
															.toString(),
											HttpStatus.FORBIDDEN);
			}

			int tentativas = existente.getTentativas();
			Date ultimaTentativa = existente.getUltimaTentativa();
			if (ultimaTentativa == null || tentativas < 3) {

				Autorizacao token = tokenDao.emiteAutorizacao(existente, req.getRemoteAddr() != null	? req.getRemoteAddr()
																										: req.getLocalAddr());

				existente.setTentativas(0);
				existente.setUltimaTentativa(null);

				return new ResponseEntity<>(token, HttpStatus.OK);
			} else {

				existente.setTentativas(++tentativas);
				existente.setUltimaTentativa(Timestamp.from(LocalDateTime	.now()
																			.atZone(ZoneId.systemDefault())
																			.toInstant()));

				usuarioDao.modificaUsuario(null, usuario, usuario);

				return new ResponseEntity<>(new JSONObject().put("erro", "Usuário ou senha inválidos")
															.toString(),
											HttpStatus.UNAUTHORIZED);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
