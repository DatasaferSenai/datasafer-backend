package datasafer.backup.dao;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
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
public class BackupDao {

	@PersistenceContext
	private EntityManager manager;

	// @Transactional
	public Backup obtemBackup(Usuario proprietario,
						Estacao estacao,
						String nome_backup) {
		List<Backup> resultadosBackup = manager	.createQuery(
				"SELECT backup FROM Backup backup "
						+ "JOIN FETCH backup.proprietario proprietario "
						+ "JOIN FETCH backup.estacao estacao "
						+ "WHERE proprietario.id = :id_proprietario "
						+ "AND estacao.id = :id_estacao "
						+ "AND backup.nome = :nome_backup ",
				Backup.class)
												.setParameter("id_proprietario", proprietario.getId())
												.setParameter("id_estacao", estacao.getId())
												.setParameter("nome_backup", nome_backup)
												.getResultList();
		return resultadosBackup.isEmpty() ? null : resultadosBackup.get(0);
	}

	@Transactional
	public void insereBackup(Usuario solicitante,
						Usuario proprietario,
						Estacao estacao,
						Backup backup) {

		solicitante = (solicitante == null ? null : manager.find(Usuario.class, solicitante.getId()));
		proprietario = manager.find(Usuario.class, proprietario.getId());
		estacao = manager.find(Estacao.class, estacao.getId());

		Validador.validar(backup);

		Backup existente = this.obtemBackup(proprietario, estacao, backup.getNome());
		if (existente != null) {
			existente	.getRegistros()
						.addAll(Registrador.modifica(solicitante, existente, backup));
			backup = existente;
		} else {
			backup	.getRegistros()
					.addAll(Registrador.insere(solicitante, backup));
		}

		Operacao operacao = new Operacao();
		operacao.setData(Date.from(LocalDateTime.now()
												.atZone(ZoneId.systemDefault())
												.toInstant()));
		operacao.setStatus(Operacao.Status.AGENDADO);
		operacao.setTamanho(null);
		operacao.setRegistros(Registrador.insere(solicitante, operacao));

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

		solicitante = (solicitante == null ? null : manager.find(Usuario.class, solicitante.getId()));
		backup = manager.find(Backup.class, backup.getId());

		if (valores.getNome() != null && !valores	.getNome()
													.equals(backup.getNome())) {

			Backup existente = this.obtemBackup(backup.getProprietario(), backup.getEstacao(), valores.getNome());
			if (existente != null) {
				throw new DataIntegrityViolationException("Backup '" + valores.getNome() + "' já existente");
			}
		}

		List<Registro> registros = Registrador.modifica(solicitante, backup, valores);
		if (backup.getRegistros() == null) {
			backup.setRegistros(registros);
		} else {
			backup	.getRegistros()
					.addAll(registros);
		}

		Validador.validar(backup);

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
	public Backup carregaStatusOperacoes(Backup backup) {

		@SuppressWarnings("unchecked")
		List<Object> resultadosStatusOperacoes = manager.createQuery(
				"SELECT operacao.status, COUNT(DISTINCT operacao.backup) FROM Operacao operacao "
						+ "WHERE operacao.backup.id = :id_backup "
						+ "AND operacao.data = (SELECT MAX(ultimaOperacao.data) FROM Operacao ultimaOperacao WHERE operacao.backup = ultimaOperacao.backup) "
						+ "GROUP BY operacao.status ")
														.setParameter("id_backup", backup.getId())
														.getResultList();

		for (Iterator<Object> iterator = resultadosStatusOperacoes.iterator(); iterator.hasNext();) {
			Object obj[] = (Object[]) iterator.next();
			backup	.getStatusOperacoes()
					.put((Operacao.Status) obj[0], (Long) obj[1]);
		}

		return backup;
	}

	// @Transactional
	public List<Backup> carregaStatusOperacoes(
												List<Backup> backups) {
		for (Backup backup : backups) {
			this.carregaStatusOperacoes(backup);
		}
		return backups;
	}

	// @Transactional
	public Backup carregaUltimaOperacao(Backup backup) {
		System.out.println("--- OPERACOES --- ");
		List<Operacao> resultadosUltimaOperacao = manager	.createQuery(
				"SELECT operacao FROM Operacao operacao "
						+ "WHERE operacao.backup.id = :id_backup "
						+ "AND operacao.data = (SELECT MAX(ultimaOperacao.data) FROM Operacao ultimaOperacao WHERE ultimaOperacao.backup = operacao.backup)",
				Operacao.class)
															.setParameter("id_backup", backup.getId())
															.getResultList();
		System.out.println("--- FIM OPERACOES --- ");
		backup.setUltimaOperacao(
				!resultadosUltimaOperacao.isEmpty() ? resultadosUltimaOperacao.get(0) : null);

		return backup;
	}

	// @Transactional
	public List<Backup> carregaUltimaOperacao(List<Backup> backups) {
		for (Backup backup : backups) {
			this.carregaUltimaOperacao(backup);
		}
		return backups;
	}

}
