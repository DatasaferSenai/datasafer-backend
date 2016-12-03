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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import datasafer.backup.dao.AutorizacaoDao;
import datasafer.backup.dao.utility.Carregador;
import datasafer.backup.dao.utility.Modificador;
import datasafer.backup.model.Autorizacao;
import datasafer.backup.model.Usuario;

@RestController
public class AutorizacaoRestController {

	@Autowired
	private AutorizacaoDao autorizacaoDao;

	@Autowired
	private Modificador modificador;
	@Autowired
	private Carregador carregador;

	@RequestMapping(value = "/login", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
					produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> login(HttpServletRequest req,
										@RequestBody Usuario usuario) throws NullPointerException, AccessDeniedException, NoSuchFieldException {

		try {
			Usuario logado = carregador.obtemEntidade(Usuario.class, "login", usuario.getLogin(), "senha", usuario.getSenha());
			if (logado == null) {

				Usuario existente = carregador.obtemEntidade(Usuario.class, "login", usuario.getLogin());
				if (existente != null) {
					Integer tentativas = existente.getTentativas();
					if (tentativas < 3) {
						existente.setTentativas(++tentativas);
						existente.setUltimaTentativa(Timestamp.from(LocalDateTime	.now()
																					.toInstant(ZoneOffset.UTC)));

						modificador.modifica(null, existente);
					}
					if (tentativas >= 3) {
						if (existente.getAtivo()) {
							existente.setAtivo(false);
							modificador.modifica(null, existente);
						}
					}
				}

				return new ResponseEntity<>(new JSONObject().put("erro", "Usuário ou senha inválidos")
															.toString(),
											HttpStatus.UNAUTHORIZED);
			}

			if (!logado.getAtivo()) {
				if (logado.getTentativas() < 3) {
					return new ResponseEntity<>(new JSONObject().put("erro", "Usuário ou senha inválidos")
																.toString(),
												HttpStatus.UNAUTHORIZED);
				} else {
					return new ResponseEntity<>(new JSONObject().put("erro", "Usuário suspenso por excesso de tentativas")
																.toString(),
												HttpStatus.UNAUTHORIZED);
				}
			}

			if (logado.getTentativas() > 0) {
				logado.setTentativas(0);
				modificador.modifica(null, logado);
			}

			Autorizacao token = autorizacaoDao.emiteAutorizacao(logado, req.getRemoteAddr() != null	? req.getRemoteAddr()
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

		autorizacaoDao.revogaToken(autorizacaoDao.obtemAutorizacao(	req.getRemoteAddr() != null	? req.getRemoteAddr()
																								: req.getLocalAddr(),
																	token_autorizacao));

		return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
	}

}
