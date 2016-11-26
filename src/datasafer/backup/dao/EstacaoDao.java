package datasafer.backup.dao;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import datasafer.backup.dao.utility.Modificador;
import datasafer.backup.model.Backup;
import datasafer.backup.model.Estacao;
import datasafer.backup.model.Operacao;
import datasafer.backup.model.Usuario;

@Repository
public class EstacaoDao {

	@PersistenceContext
	private EntityManager manager;

	@Autowired
	private BackupDao backupDao;

	@Autowired
	private Modificador modificador;

	// @Transactional
	public Estacao obtemEstacao(String nome_estacao) {
		List<Estacao> resultadosEstacao = manager	.createQuery(
																"SELECT estacao FROM Estacao estacao "
																		+ "WHERE estacao.nome = :nome_estacao ",
																Estacao.class)
													.setParameter("nome_estacao", nome_estacao)
													.getResultList();

		return resultadosEstacao.isEmpty()	? null
											: resultadosEstacao.get(0);
	}

	@Transactional
	public void modificaEstacao(Usuario solicitante,
								Estacao estacao,
								Estacao valores) {

		solicitante = (solicitante == null	? null
											: manager.find(Usuario.class, solicitante.getId()));
		estacao = manager.find(Estacao.class, estacao.getId());

		if (valores.getNome() != null && !valores	.getNome()
													.equals(estacao.getNome())) {

			Estacao existente = this.obtemEstacao(valores.getNome());
			if (existente != null) {
				throw new DataIntegrityViolationException("Usuário '" + valores.getNome() + "' já existente");
			}
		}

		estacao	.getRegistros()
				.addAll(modificador.modifica(solicitante, estacao, valores));

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
	public Estacao carregaInfos(Usuario proprietario,
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
	public Estacao carregaInfos(Estacao estacao) {

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
	public List<Estacao> carregaInfos(	Usuario proprietario,
										List<Estacao> estacoes) {
		for (Estacao estacao : estacoes) {
			this.carregaInfos(proprietario, estacao);
		}
		return estacoes;
	}

	// @Transactional
	public List<Estacao> carregaInfos(
										List<Estacao> estacoes) {
		for (Estacao estacao : estacoes) {
			this.carregaInfos(estacao);
		}
		return estacoes;
	}

	// @Transactional
	public Usuario obtemGerenciador(Estacao estacao) {
		List<Usuario> resultadosGerenciador = manager	.createQuery(
																	"SELECT e.gerenciador "
																			+ "FROM Estacao e "
																			+ "WHERE e.id = :id_estacao",
																	Usuario.class)
														.setParameter("id_estacao", estacao.getId())
														.getResultList();

		return resultadosGerenciador.isEmpty()	? null
												: resultadosGerenciador.get(0);
	}

	@Transactional
	public void insereBackup(	Usuario solicitante,
								Usuario proprietario,
								Estacao estacao,
								Backup backup) {

		solicitante = (solicitante == null	? null
											: manager.find(Usuario.class, solicitante.getId()));
		proprietario = manager.find(Usuario.class, proprietario.getId());
		estacao = manager.find(Estacao.class, estacao.getId());

		Backup existente = backupDao.obtemBackup(proprietario, estacao, backup.getNome());
		if (existente != null) {
			existente	.getRegistros()
						.addAll(modificador.modifica(solicitante, existente, backup));
			backup = existente;
		}
		// else {
		// backup .getRegistros()
		// .addAll(modificador.modifica(solicitante, backup, null));
		// }

		Operacao operacao = new Operacao();
		operacao.setData(Timestamp.from(LocalDateTime	.now()
														.atZone(ZoneId.systemDefault())
														.toInstant()));
		operacao.setStatus(Operacao.Status.AGENDADO);
		operacao.setTamanho(null);
		// operacao.getRegistros().addAll(modificador.modifica(solicitante,
		// operacao, null));

		backup	.getOperacoes()
				.add(operacao);
		operacao.setBackup(backup);

		proprietario.getBackups()
					.add(backup);
		backup.setProprietario(proprietario);

		estacao	.getBackups()
				.add(backup);
		backup.setEstacao(estacao);

		manager.persist(backup);
	}

}
