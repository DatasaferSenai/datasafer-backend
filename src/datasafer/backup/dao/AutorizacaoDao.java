package datasafer.backup.dao;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import datasafer.backup.model.Autorizacao;
import datasafer.backup.model.Usuario;

@Repository
public class AutorizacaoDao {

	@PersistenceContext
	private EntityManager manager;

	private static final SecureRandom randomGenerator = new SecureRandom();

	@Transactional
	public Autorizacao obtemAutorizacao(String ip,
										String token_autorizacao) {
		List<Autorizacao> resultadosToken = manager	.createQuery(
																"SELECT a FROM Autorizacao a "
																		+ "WHERE a.token = :token_autorizacao "
																		+ "AND a.ip = :ip_autorizacao ",
																Autorizacao.class)
													.setParameter("token_autorizacao", token_autorizacao)
													.setParameter("ip_autorizacao", ip)
													.getResultList();

		Autorizacao autorizacao = resultadosToken.isEmpty()	? null
															: resultadosToken.get(0);

		if (autorizacao != null) {
			autorizacao.setUltimoAcesso(Timestamp.from(LocalDateTime.now()
																	.toInstant(ZoneOffset.UTC)));
		}

		return autorizacao;
	}

	@Transactional
	public Autorizacao emiteAutorizacao(Usuario usuario,
										String ip) {

		usuario = manager.find(Usuario.class, usuario.getId());

		manager	.createQuery("DELETE "
				+ "FROM Autorizacao a "
				+ "WHERE a.ip = :ip_autorizacao "
				+ "AND a.usuario.id = :id_usuario ")
				.setParameter("ip_autorizacao", ip)
				.setParameter("id_usuario", usuario.getId())
				.executeUpdate();

		String novo_token = null;
		do {
			novo_token = new BigInteger(635, randomGenerator).toString(32);
		} while (this.obtemAutorizacao(ip, novo_token) != null);

		Autorizacao autorizacao = new Autorizacao();
		autorizacao.setToken(novo_token);
		autorizacao.setIp(ip);
		autorizacao.setEmissao(Timestamp.from(LocalDateTime	.now()
															.toInstant(ZoneOffset.UTC)));

		usuario	.getAutorizacoes()
				.add(autorizacao);
		autorizacao.setUsuario(usuario);

		manager.persist(autorizacao);

		return autorizacao;
	}

	@Transactional
	public void revogaAutorizacoes(Usuario usuario) {

		manager	.createQuery("DELETE "
				+ "FROM Autorizacao a "
				+ "WHERE a.usuario.id = :id_usuario ")
				.setParameter("id_usuario", usuario.getId())
				.executeUpdate();

	}

	@Transactional
	public void revogaToken(Autorizacao autorizacao) {

		manager	.createQuery("DELETE "
				+ "FROM Autorizacao a "
				+ "WHERE a.token = :token_autorizacao ")
				.setParameter("token_autorizacao", autorizacao.getToken())
				.executeUpdate();

	}

	@Transactional
	public void limpaAutorizacoes(Integer dias) {

		manager	.createQuery("DELETE "
				+ "FROM Autorizacao a "
				+ "WHERE a.ultimoAcesso IS NOT NULL "
				+ "AND a.ultimoAcesso < :data ")
				.setParameter("data", Timestamp.from(LocalDateTime	.now().minusDays(dias)
																	.toInstant(ZoneOffset.UTC)))
				.executeUpdate();
	}

}
