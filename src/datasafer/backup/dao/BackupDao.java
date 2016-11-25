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
public class BackupDao {

	@PersistenceContext
	private EntityManager manager;

	@Autowired
	private Modificador modificador;

	// @Transactional
	public Backup obtemBackup(	Usuario proprietario,
								Estacao estacao,
								String nome_backup) {
		List<Backup> resultadosBackup = manager	.createQuery(
															"SELECT backup FROM Backup backup "
																	+ "WHERE backup.proprietario.id = :id_proprietario "
																	+ "AND backup.estacao.id = :id_estacao "
																	+ "AND backup.nome = :nome_backup ",
															Backup.class)
												.setParameter("id_proprietario", proprietario.getId())
												.setParameter("id_estacao", estacao.getId())
												.setParameter("nome_backup", nome_backup)
												.getResultList();
		return resultadosBackup.isEmpty()	? null
											: resultadosBackup.get(0);
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

		Backup existente = this.obtemBackup(proprietario, estacao, backup.getNome());
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

	@Transactional
	public void modificaBackup(	Usuario solicitante,
								Backup backup,
								Backup valores) {

		solicitante = (solicitante == null	? null
											: manager.find(Usuario.class, solicitante.getId()));
		backup = manager.find(Backup.class, backup.getId());

		if (valores.getNome() != null && !valores	.getNome()
													.equals(backup.getNome())) {

			Backup existente = this.obtemBackup(backup.getProprietario(), backup.getEstacao(), valores.getNome());
			if (existente != null) {
				throw new DataIntegrityViolationException("Backup '" + valores.getNome() + "' já existente");
			}
		}

		backup	.getRegistros()
				.addAll(modificador.modifica(solicitante, backup, valores));

		manager.persist(backup);

	}

	@Transactional
	public List<Operacao> obtemOperacoes(Backup backup) {
		return manager	.createQuery(
									"SELECT operacao FROM Operacao operacao "
											+ "JOIN FETCH operacao.backup backup "
											+ "WHERE backup.id = :id_backup ",
									Operacao.class)
						.setParameter("id_backup", backup.getId())
						.getResultList();
	}

	// @Transactional
	public Backup carregaInfos(Backup backup) {

		@SuppressWarnings("unchecked")
		List<Object> resultadosStatusOperacoes = manager.createQuery(
																		"SELECT operacao.status, COUNT(DISTINCT operacao.backup) FROM Operacao operacao "
																				+ "WHERE operacao.backup.id = :id_backup "
																				+ "GROUP BY operacao.status ")
														.setParameter("id_backup", backup.getId())
														.getResultList();

		for (Iterator<Object> iterator = resultadosStatusOperacoes.iterator(); iterator.hasNext();) {
			Object obj[] = (Object[]) iterator.next();
			backup	.getStatusOperacoes()
					.put((Operacao.Status) obj[0], (Long) obj[1]);
		}

		List<Operacao> resultadosUltimaOperacao = manager	.createQuery(
																		"SELECT operacao FROM Operacao operacao "
																				+ "WHERE operacao.backup.id = :id_backup "
																				+ "AND operacao.data = (SELECT MAX(ultimaOperacao.data) FROM Operacao ultimaOperacao WHERE ultimaOperacao.backup = operacao.backup)",
																		Operacao.class)
															.setParameter("id_backup", backup.getId())
															.getResultList();

		backup.setUltimaOperacao(
									!resultadosUltimaOperacao.isEmpty()	? resultadosUltimaOperacao.get(0)
																		: null);

		List<Long> resultadosArmazenamentoOcupado = manager	.createQuery(
																		"SELECT SUM(operacao.tamanho) FROM Operacao operacao "
																				+ "WHERE operacao.backup.id = :id_backup "
																				+ "AND operacao.data = (SELECT MAX(ultimaOperacao.data) FROM Operacao ultimaOperacao WHERE operacao.backup = ultimaOperacao.backup AND ultimaOperacao.status = :status_operacao) ",
																		Long.class)
															.setParameter("id_backup", backup.getId())
															.setParameter("status_operacao", Operacao.Status.SUCESSO)
															.getResultList();

		backup.setArmazenamentoOcupado(
										!resultadosArmazenamentoOcupado.isEmpty()
												&& resultadosArmazenamentoOcupado.get(0) != null	? resultadosArmazenamentoOcupado.get(0)
																									: 0L);

		return backup;
	}

	// @Transactional
	public List<Backup> carregaInfos(
										List<Backup> backups) {
		for (Backup backup : backups) {
			this.carregaInfos(backup);
		}
		return backups;
	}

	// @Transactional
	public Usuario obtemProprietario(Backup backup) {

		List<Usuario> resultadosProprietario = manager	.createQuery(
																	"SELECT b.proprietario "
																			+ "FROM Backup b "
																			+ "WHERE b.id = :id_backup",
																	Usuario.class)
														.setParameter("id_backup", backup.getId())
														.getResultList();

		return resultadosProprietario.isEmpty()	? null
												: resultadosProprietario.get(0);
	}

}
