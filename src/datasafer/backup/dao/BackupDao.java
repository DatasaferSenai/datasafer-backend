package datasafer.backup.dao;

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

	@Transactional
	public void insereOperacao(	Usuario solicitante,
								Backup backup,
								Operacao operacao) {

		solicitante = (solicitante == null	? null
											: manager.find(Usuario.class, solicitante.getId()));
		backup = manager.find(Backup.class, backup.getId());

		// operacao.getRegistros()
		// .addAll(modificador.modifica(solicitante, operacao, null));

		backup	.getOperacoes()
				.add(operacao);
		operacao.setBackup(backup);

		manager.persist(operacao);
	}

}
