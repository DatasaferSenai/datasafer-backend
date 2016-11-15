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
	public Token obtemToken(String ip,
							String chave_token)
			throws DataRetrievalFailureException {
		List<Token> resultadosToken = manager	.createQuery(
				"SELECT t FROM Token t "
						+ "WHERE t.chave = :chave_token "
						+ "AND t.ip = :ip_token ",
				Token.class)
												.setParameter("chave_token", chave_token)
												.setParameter("ip_token", ip)
												.getResultList();

		return resultadosToken.isEmpty() ? null : resultadosToken.get(0);
	}

	@Transactional
	public Token emiteToken(Usuario usuario,
							String ip,
							long expiracao)
			throws DataRetrievalFailureException {

		usuario = manager.find(Usuario.class, usuario.getId());

		manager	.createQuery("DELETE "
				+ "FROM Token token "
				+ "WHERE token.ip = :ip_token "
				+ "AND token.usuario.id = :id_usuario ")
				.setParameter("ip_token", ip)
				.setParameter("id_usuario", usuario.getId())
				.executeUpdate();

		Token token = new Token();
		token.setChave(new BigInteger(635, random).toString(32));
		token.setIp(ip);
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
	public void revogaTokens(Usuario usuario) throws DataRetrievalFailureException {

		manager	.createQuery("DELETE "
				+ "FROM Token t "
				+ "WHERE t.usuario.id = :id_usuario ")
				.setParameter("id_usuario", usuario.getId())
				.executeUpdate();

	}

	@Transactional
	public void modificaUltimaUtilizacao(	Token token,
											Date dataUtilizacao) {
		manager	.createQuery("UPDATE Token t "
				+ "SET t.ultimaUtilizacao = :dataUtilizacao "
				+ "WHERE t.chave = :chave_token ")
				.setParameter("dataUtilizacao", dataUtilizacao)
				.setParameter("chave_token", token.getChave())
				.executeUpdate();

	}

}
