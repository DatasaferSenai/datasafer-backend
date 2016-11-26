package datasafer.backup.controller;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
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
	public ResponseEntity<Object> login(HttpServletRequest req,
										@RequestBody Usuario usuario) {

		try {

			Usuario logado = usuarioDao.login(usuario);
			if (logado == null) {

				Usuario existente = usuarioDao.obtemUsuario(usuario.getLogin());
				if (existente != null) {
					Integer tentativas = existente.getTentativas();
					if (tentativas < 3) {
						existente.setTentativas(++tentativas);
						existente.setUltimaTentativa(Timestamp.from(LocalDateTime	.now()
																					.toInstant(ZoneOffset.UTC)));

						usuarioDao.modificaUsuario(null, existente, existente);
					}
					if (tentativas >= 3) {
						existente.setStatus(Usuario.Status.SUSPENSO_TENTATIVAS);
						usuarioDao.modificaUsuario(null, existente, existente);
					}
				}

				return new ResponseEntity<>(new JSONObject().put("erro", "Usuário ou senha inválidos")
															.toString(),
											HttpStatus.UNAUTHORIZED);
			}

			if (!logado.getStatus().equals(Usuario.Status.ATIVO)) {
				return new ResponseEntity<>(new JSONObject().put("erro", logado.getStatus())
															.toString(),
											HttpStatus.FORBIDDEN);
			}

			if (logado.getTentativas() > 0) {
				logado.setTentativas(0);
				usuarioDao.modificaUsuario(null, logado, logado);
			}

			Autorizacao token = tokenDao.emiteAutorizacao(logado, req.getRemoteAddr() != null	? req.getRemoteAddr()
																								: req.getLocalAddr());
			return new ResponseEntity<>(token, HttpStatus.OK);

		} catch (JSONException e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = "/logout", method = RequestMethod.DELETE,
					produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Void> logout(	HttpServletRequest req,
										@RequestHeader("Authorization") String token_autorizacao) {

		tokenDao.revogaToken(tokenDao.obtemAutorizacao(	req.getRemoteAddr() != null	? req.getRemoteAddr()
																					: req.getLocalAddr(),
														token_autorizacao));

		return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
	}

}
