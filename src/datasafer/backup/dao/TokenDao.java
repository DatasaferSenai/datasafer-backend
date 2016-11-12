package datasafer.backup.dao;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
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
	@Autowired
	private UsuarioDao usuarioDao;

	private SecureRandom random = new SecureRandom();

	// Transactional
	public Token obter(String chave_token) throws DataRetrievalFailureException {
		List<Token> resultadosToken = manager	.createQuery("SELECT t FROM Token t WHERE t.token = :chave_token", Token.class)
												.setParameter("chave_token", chave_token)
												.getResultList();

		return resultadosToken.isEmpty() ? null : resultadosToken.get(0);
	}

	@Transactional
	public Token emitir(String login_usuario) throws DataRetrievalFailureException {
		return this.emitir(login_usuario, 0);
	}

	@Transactional
	public Token emitir(Usuario usuario) throws DataRetrievalFailureException {
		return this.emitir(usuario.getLogin(), 0);
	}

	@Transactional
	public Token emitir(Usuario usuario,
						long expiracao)
			throws DataRetrievalFailureException {
		return this.emitir(usuario.getLogin(), expiracao);
	}

	@Transactional
	public Token emitir(String login_usuario,
						long expiracao)
			throws DataRetrievalFailureException {

		Usuario usuario = usuarioDao.obter(login_usuario);
		if (usuario == null) {
			throw new DataRetrievalFailureException("Usuário '" + login_usuario + "' não encontrado");
		}

		Token token = new Token();
		token.setToken(new BigInteger(1275, random).toString(32));
		token.setEmissao(Date.from(LocalDateTime.now()
												.atZone(ZoneId.systemDefault())
												.toInstant()));
		if (expiracao > 0) {
			token.setExpiracao(expiracao == 0 ? null : Date.from(LocalDateTime	.now()
																				.atZone(ZoneId.systemDefault())
																				.toInstant()
																				.plusSeconds(expiracao)));
		}

		token.setUsuario(usuario);

		manager.persist(token);

		return token;
	}

	@Transactional
	public void revogar(Usuario usuario) throws DataRetrievalFailureException {
		this.revogar(usuario.getLogin());
	}

	@Transactional
	public void revogar(String login_usuario) throws DataRetrievalFailureException {

		Usuario usuario = usuarioDao.obter(login_usuario);
		if (usuario == null) {
			throw new DataRetrievalFailureException("Usuário '" + login_usuario + "' não encontrado");
		}

		List<Token> tokens = usuario.getTokens();
		if (tokens != null) {
			tokens.clear();
		}

		manager.persist(usuario);
	}

	@Transactional
	public void revogar(Usuario usuario,
						Token token)
			throws DataRetrievalFailureException {
		this.revogar(usuario.getLogin(), token.getToken());
	}

	@Transactional
	public void revogar(String login_usuario,
						Token token)
			throws DataRetrievalFailureException {
		this.revogar(login_usuario, token.getToken());
	}

	@Transactional
	public void revogar(Usuario usuario,
						String chave_token)
			throws DataRetrievalFailureException {
		this.revogar(usuario.getLogin(), chave_token);
	}

	@Transactional
	public void revogar(String login_usuario,
						String chave_token)
			throws DataRetrievalFailureException {

		Usuario usuario = usuarioDao.obter(login_usuario);
		if (usuario == null) {
			throw new DataRetrievalFailureException("Usuário '" + login_usuario + "' não encontrado");
		}

		Token token = this.obter(chave_token);
		if (token == null) {
			throw new DataRetrievalFailureException("Token não encontrado");
		}

		manager.remove(token);
	}

}