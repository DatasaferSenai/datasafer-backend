package datasafer.backup.dao;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import datasafer.backup.dao.helper.Modificador;
import datasafer.backup.dao.helper.Validador;
import datasafer.backup.model.Backup;
import datasafer.backup.model.Estacao;
import datasafer.backup.model.Registro;
import datasafer.backup.model.Registro.Tipo;
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

		Usuario solicitante = usuarioDao.obter(login_solicitante);
		if (solicitante == null) {
			throw new DataRetrievalFailureException("Usuário solicitante '" + login_solicitante + "' não encontrado");
		}

		Usuario proprietário = usuarioDao.obter(login_proprietario);
		if (proprietário == null) {
			throw new DataRetrievalFailureException("Usuário proprietário '" + login_proprietario + "' não encontrado");
		}

		Estacao estacao = estacaoDao.obter(nome_estacao);
		if (estacao == null) {
			throw new DataRetrievalFailureException("Estação '" + nome_estacao + "' não encontrada");
		}

		Backup existente = this.obter(login_proprietario, nome_estacao, backup.getNome());
		if (existente != null && existente	.getUltimoRegistro()
											.getTipo() != Tipo.EXCLUIDO) {
			throw new DataIntegrityViolationException("Backup '" + backup.getNome() + "' já existente");
		}

		List<Registro> registros = backup.getRegistros();
		if (registros == null) {
			registros = new ArrayList<Registro>();
			backup.setRegistros(registros);
		}
		registros.add(new Registro(solicitante, Tipo.INSERIDO, Date.from(LocalDateTime	.now()
																						.atZone(ZoneId.systemDefault())
																						.toInstant())));

		backup.setEstacao(estacao);

		manager.persist(backup);
	}

	@Transactional
	public void modificar(	String login_solicitante,
							String login_proprietario,
							String nome_estacao,
							String nome_backup,
							Backup valores) {

		Usuario solicitante = usuarioDao.obter(login_solicitante);
		if (solicitante == null) {
			throw new DataRetrievalFailureException("Usuário solicitante '" + login_solicitante + "' não encontrado");
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

		Modificador.modificar(backup, valores);
		Validador.validar(backup);

		List<Registro> registros = backup.getRegistros();
		if (registros == null) {
			registros = new ArrayList<Registro>();
			backup.setRegistros(registros);
		}
		registros.add(new Registro(solicitante, Tipo.MODIFICADO, Date.from(LocalDateTime.now()
																						.atZone(ZoneId.systemDefault())
																						.toInstant())));

		manager.persist(backup);

	}

}
