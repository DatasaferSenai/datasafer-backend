package datasafer.backup.dao;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
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
	@Autowired
	UsuarioDao usuarioDao;
	@Autowired
	EstacaoDao estacaoDao;

	// @Transactional
	public Backup obter(String login_proprietario,
						String nome_estacao,
						String nome_backup) {
		List<Backup> resultadosBackup = manager	.createQuery(
				"SELECT b FROM Backup b WHERE b.estacao.nome = :nome_estacao AND b.proprietario.login = :login_proprietario AND b.nome = :nome_backup",
				Backup.class)
												.setParameter("login_proprietario", login_proprietario)
												.setParameter("nome_estacao", nome_estacao)
												.setParameter("nome_backup", nome_backup)
												.getResultList();

		return resultadosBackup.isEmpty() ? null : resultadosBackup.get(0);
	}

	@Transactional
	public void inserir(String login_solicitante,
						String login_proprietario,
						String nome_estacao,
						Backup backup) {

		Validador.validar(backup);

		Usuario solicitante = null;
		if (login_solicitante != null) {
			solicitante = usuarioDao.obter(login_solicitante);
			if (solicitante == null) {
				throw new DataRetrievalFailureException("Usuário solicitante '" + login_solicitante + "' não encontrado");
			}
		}

		Usuario proprietario = usuarioDao.obter(login_proprietario);
		if (proprietario == null) {
			throw new DataRetrievalFailureException("Usuário proprietário '" + login_proprietario + "' não encontrado");
		}

		Estacao estacao = estacaoDao.obter(nome_estacao);
		if (estacao == null) {
			throw new DataRetrievalFailureException("Estação '" + nome_estacao + "' não encontrada");
		}

		Backup existente = this.obter(login_proprietario, nome_estacao, backup.getNome());
		if (existente != null) {
			existente	.getRegistros()
						.addAll(Registrador.modificar(solicitante, existente, backup));
			backup = existente;
		} else {
			backup	.getRegistros()
					.addAll(Registrador.inserir(solicitante, backup));
		}

		Operacao operacao = new Operacao();
		operacao.setBackup(backup);
		operacao.setData(Date.from(LocalDateTime.now()
												.atZone(ZoneId.systemDefault())
												.toInstant()));
		operacao.setStatus(Operacao.Status.AGENDADO);
		operacao.setTamanho(null);
		operacao.setRegistros(Registrador.inserir(solicitante, operacao));

		backup	.getOperacoes()
				.add(operacao);

		backup	.getStatusOperacoes()
				.put(operacao.getStatus(), backup	.getStatusOperacoes()
													.get(operacao.getStatus())
						+ 1);

		proprietario.getBackups()
					.add(backup);
		proprietario.getStatusBackups()
					.put(backup	.getUltimaOperacao()
								.getStatus(),
							proprietario.getStatusBackups()
										.get(backup	.getUltimaOperacao()
													.getStatus())
									+ 1);
		backup.setProprietario(proprietario);

		estacao	.getBackups()
				.add(backup);
		estacao	.getStatusBackups()
				.put(backup	.getUltimaOperacao()
							.getStatus(),
						estacao	.getStatusBackups()
								.get(backup	.getUltimaOperacao()
											.getStatus())
								+ 1);
		backup.setEstacao(estacao);

		manager.persist(backup);
	}

	@Transactional
	public void modificar(	String login_solicitante,
							String login_proprietario,
							String nome_estacao,
							String nome_backup,
							Backup valores) {

		Usuario solicitante = null;
		if (login_solicitante != null) {
			solicitante = usuarioDao.obter(login_solicitante);
			if (solicitante == null) {
				throw new DataRetrievalFailureException("Usuário solicitante '" + login_solicitante + "' não encontrado");
			}
		}

		Usuario proprietário = usuarioDao.obter(login_proprietario);
		if (proprietário == null) {
			throw new DataRetrievalFailureException("Usuário proprietário '" + login_proprietario + "' não encontrado");
		}

		Estacao estacao = estacaoDao.obter(nome_estacao);
		if (estacao == null) {
			throw new DataRetrievalFailureException("Estação '" + nome_estacao + "' não encontrada");
		}

		Backup backup = this.obter(login_proprietario, nome_estacao, nome_backup);
		if (backup == null) {
			throw new DataRetrievalFailureException("Backup '" + nome_backup + "' não encontrado");
		}

		if (valores.getNome() != null && !valores	.getNome()
													.equals(backup.getNome())) {

			Backup existente = this.obter(login_proprietario, nome_estacao, valores.getNome());
			if (existente != null) {
				throw new DataIntegrityViolationException("Backup '" + valores.getNome() + "' já existente");
			}

		}

		if (solicitante != null) {
			List<Registro> registros = Registrador.modificar(solicitante, backup, valores);

			Validador.validar(backup);

			if (backup.getRegistros() == null) {
				backup.setRegistros(registros);
			} else {
				backup	.getRegistros()
						.addAll(registros);
			}
		} else {
			Validador.validar(backup);
		}

		manager.persist(backup);

	}

}
