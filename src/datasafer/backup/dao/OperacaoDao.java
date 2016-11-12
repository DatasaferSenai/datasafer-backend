package datasafer.backup.dao;

import java.text.SimpleDateFormat;
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
public class OperacaoDao {

	@PersistenceContext
	private EntityManager manager;
	@Autowired
	UsuarioDao usuarioDao;
	@Autowired
	EstacaoDao estacaoDao;
	@Autowired
	BackupDao backupDao;

	// @Transactional
	public Operacao obter(	String login_proprietario,
							String nome_estacao,
							String nome_backup,
							Date data_operacao) {
		List<Operacao> resultadosOperacao = manager	.createQuery(
				"SELECT o FROM Operacao o WHERE o.backup.proprietario.login = :login_proprietario AND o.backup.estacao.nome = :nome_estacao AND o.backup.nome = :nome_backup AND o.data = :data_operacao",
				Operacao.class)
													.setParameter("login_proprietario", login_proprietario)
													.setParameter("nome_estacao", nome_estacao)
													.setParameter("nome_backup", nome_backup)
													.setParameter("data_operacao", data_operacao)
													.getResultList();

		return resultadosOperacao.isEmpty() ? null : resultadosOperacao.get(0);
	}

	@Transactional
	public void inserir(String login_solicitante,
						String login_proprietario,
						String nome_estacao,
						String nome_backup,
						Operacao operacao)
			throws DataRetrievalFailureException, DataIntegrityViolationException {

		Validador.validar(operacao);

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

		Backup backup = backupDao.obter(login_proprietario, nome_estacao, nome_backup);
		if (backup == null) {
			throw new DataRetrievalFailureException("Backup '" + nome_backup + "' não encontrado");
		}

		List<Registro> registros = Registrador.inserir(solicitante, operacao);

		if (operacao.getRegistros() == null) {
			operacao.setRegistros(registros);
		} else {
			operacao.getRegistros()
					.addAll(registros);
		}

		operacao.setBackup(backup);

		manager.persist(operacao);
	}

	@Transactional
	public void modificar(	String login_solicitante,
							String login_proprietario,
							String nome_estacao,
							String nome_backup,
							Date data_operacao,
							Operacao valores)
			throws DataRetrievalFailureException, DataIntegrityViolationException {

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

		Backup backup = backupDao.obter(login_proprietario, nome_estacao, nome_backup);
		if (backup == null) {
			throw new DataRetrievalFailureException("Backup '" + nome_backup + "' não encontrado");
		}

		Operacao operacao = this.obter(login_proprietario, nome_estacao, nome_backup, data_operacao);
		if (operacao == null) {
			throw new DataRetrievalFailureException("Operação '" + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(data_operacao) + "' não encontrada");
		}

		if (valores.getData() != null && !valores	.getData()
													.equals(operacao.getData())) {

			Operacao existente = this.obter(login_proprietario, nome_estacao, nome_backup, valores.getData());
			if (existente != null) {
				throw new DataIntegrityViolationException(
						"Operação '" + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(valores.getData()) + "' já existente");
			}
		}

		List<Registro> registros = Registrador.modificar(solicitante, operacao, valores);

		Validador.validar(operacao);

		if (operacao.getRegistros() == null) {
			operacao.setRegistros(registros);
		} else {
			operacao.getRegistros()
					.addAll(registros);
		}

		manager.persist(operacao);
	}

}
