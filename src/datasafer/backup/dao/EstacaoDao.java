package datasafer.backup.dao;

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import datasafer.backup.dao.helper.Registrador;
import datasafer.backup.dao.helper.Validador;
import datasafer.backup.model.Backup;
import datasafer.backup.model.Estacao;
import datasafer.backup.model.Operacao;
import datasafer.backup.model.Registro;
import datasafer.backup.model.Usuario;

@Repository
public class EstacaoDao {

	@PersistenceContext
	private EntityManager manager;

	// @Transactional
	public Estacao obtem(String nome_estacao) {
		List<Estacao> resultadosEstacao = manager	.createQuery(
				"SELECT estacao FROM Estacao estacao "
						+ "WHERE estacao.nome = :nome_estacao ",
				Estacao.class)
													.setParameter("nome_estacao", nome_estacao)
													.getResultList();

		return resultadosEstacao.isEmpty() ? null : resultadosEstacao.get(0);
	}

	@Transactional
	public void modificar(	Usuario solicitante,
							Estacao estacao,
							Estacao valores) {

		solicitante = (solicitante == null ? null : manager.find(Usuario.class, solicitante.getId()));
		estacao = manager.find(Estacao.class, estacao.getId());

		if (valores.getNome() != null && !valores	.getNome()
													.equals(estacao.getNome())) {

			Estacao existente = this.obtem(valores.getNome());
			if (existente != null) {
				throw new DataIntegrityViolationException("Usuário '" + valores.getNome() + "' já existente");
			}
		}

		List<Registro> registros = Registrador.modificar(solicitante, estacao, valores);
		if (estacao.getRegistros() == null) {
			estacao.setRegistros(registros);
		} else {
			estacao	.getRegistros()
					.addAll(registros);
		}

		Validador.validar(estacao);

		manager.persist(estacao);
	}

	@Transactional
	public void inserir(Usuario solicitante,
						Usuario gerenciador,
						Estacao estacao) {

		solicitante = (solicitante == null ? null : manager.find(Usuario.class, solicitante.getId()));
		gerenciador = manager.find(Usuario.class, gerenciador.getId());

		Validador.validar(estacao);

		Estacao existente = this.obtem(estacao.getNome());
		if (existente != null) {
			throw new DataIntegrityViolationException("Estação '" + estacao.getNome() + "' já existente");
		}

		estacao	.getRegistros()
				.addAll(Registrador.inserir(solicitante, estacao));

		gerenciador	.getEstacoes()
					.add(estacao);
		estacao.setGerenciador(gerenciador);

		manager.persist(estacao);
	}

	@Transactional
	public List<Backup> obtemBackups(	Usuario proprietario,
										Estacao estacao) {
		return manager	.createQuery(
				"SELECT backup FROM Backup backup "
						+ "JOIN FETCH backup.estacao estacao "
						+ "JOIN FETCH backup.proprietario proprietario "
						+ "WHERE estacao.id = :id_estacao "
						+ "AND proprietario.id = :id_proprietario ",
				Backup.class)
						.setParameter("id_estacao", estacao.getId())
						.setParameter("id_proprietario", proprietario.getId())
						.getResultList();
	}

	@Transactional
	public List<Backup> obtemBackups(Estacao estacao) {
		return manager	.createQuery(
				"SELECT backup FROM Backup backup "
						+ "JOIN FETCH backup.estacao estacao "
						+ "WHERE estacao.id = :id_estacao ",
				Backup.class)
						.setParameter("id_estacao", estacao.getId())
						.getResultList();
	}

	// @Transactional
	public Estacao obtemStatusBackups(	Usuario proprietario,
										Estacao estacao) {

		@SuppressWarnings("unchecked")
		List<Object> resultadosStatusBackups = manager	.createQuery(
				"SELECT operacao.status, COUNT(DISTINCT operacao.backup) FROM Operacao operacao "
						+ "WHERE operacao.backup.proprietario.id = :id_proprietario "
						+ "AND operacao.backup.estacao.id = :id_estacao  "
						+ "AND operacao.data = (SELECT MAX(ultimaOperacao.data) FROM Operacao ultimaOperacao WHERE operacao.backup = ultimaOperacao.backup) "
						+ "GROUP BY operacao.status ")
														.setParameter("id_proprietario", proprietario.getId())
														.setParameter("id_estacao", estacao.getId())
														.getResultList();

		for (Iterator<Object> iterator = resultadosStatusBackups.iterator(); iterator.hasNext();) {
			Object obj[] = (Object[]) iterator.next();
			estacao	.getStatusBackups()
					.put((Operacao.Status) obj[0], (Long) obj[1]);
		}

		return estacao;
	}

	// @Transactional
	public Estacao carregaStatusBackups(Estacao estacao) {

		@SuppressWarnings("unchecked")
		List<Object> resultadosStatusBackups = manager	.createQuery(
				"SELECT operacao.status, COUNT(DISTINCT operacao.backup) FROM Operacao operacao "
						+ "WHERE operacao.backup.estacao.id = :id_estacao "
						+ "AND operacao.data = (SELECT MAX(ultimaOperacao.data) FROM Operacao ultimaOperacao WHERE operacao.backup = ultimaOperacao.backup) "
						+ "GROUP BY operacao.status ")
														.setParameter("id_estacao", estacao.getId())
														.getResultList();

		for (Iterator<Object> iterator = resultadosStatusBackups.iterator(); iterator.hasNext();) {
			Object obj[] = (Object[]) iterator.next();
			estacao	.getStatusBackups()
					.put((Operacao.Status) obj[0], (Long) obj[1]);
		}

		return estacao;
	}

	// @Transactional
	public List<Estacao> carregaStatusBackups(Usuario proprietario,
											List<Estacao> estacoes) {
		for (Estacao estacao : estacoes) {
			this.obtemStatusBackups(proprietario, estacao);
		}
		return estacoes;
	}

	// @Transactional
	public List<Estacao> carregaStatusBackups(
											List<Estacao> estacoes) {
		for (Estacao estacao : estacoes) {
			this.carregaStatusBackups(estacao);
		}
		return estacoes;
	}

}
