package datasafer.backup.dao;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import datasafer.backup.model.Token;
import datasafer.backup.model.Usuario;

@Repository
public class TokenDao {

	public static final String SECRET = "J0pjgqSuFXmCw8RQMPWaYT8XSBTneN0nDfMjLgUQ37Tp6l6I2SjQmhn5i7jCLZpO";
	public static final String ISSUER = "http://www.sp.senai.br";
	public static final long EXPIRES_IN_SECONDS = 60 * 60 * 24;

	@PersistenceContext
	private EntityManager manager;

	private SecureRandom random = new SecureRandom();

	// Transactional
	public Token obtem(String chave_token) throws DataRetrievalFailureException {
		return manager.find(Token.class, chave_token);
	}

	@Transactional
	public Token emitir(Usuario usuario,
						long expiracao)
			throws DataRetrievalFailureException {

		usuario = manager.find(Usuario.class, usuario.getId());

		Token token = new Token();
		token.setToken(new BigInteger(635, random).toString(32));
		token.setEmissao(Date.from(LocalDateTime.now()
												.atZone(ZoneId.systemDefault())
												.toInstant()));
		if (expiracao > 0) {
			token.setExpiracao(expiracao == 0 ? null : Date.from(LocalDateTime	.now()
																				.atZone(ZoneId.systemDefault())
																				.toInstant()
																				.plusSeconds(expiracao)));
		}

		usuario	.getTokens()
				.add(token);
		token.setUsuario(usuario);

		manager.persist(token);

		return token;
	}

	@Transactional
	public void revogar(Usuario usuario) throws DataRetrievalFailureException {

		usuario = manager.find(Usuario.class, usuario.getId());

		List<Token> tokens = usuario.getTokens();
		if (tokens != null) {
			tokens.clear();
		}

		manager.persist(usuario);
	}
}
