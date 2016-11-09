package datasafer.backup.bo;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;

import datasafer.backup.bo.helper.Modificador;
import datasafer.backup.bo.helper.Validador;
import datasafer.backup.dao.BackupDao;
import datasafer.backup.dao.EstacaoDao;
import datasafer.backup.dao.OperacaoDao;
import datasafer.backup.dao.UsuarioDao;
import datasafer.backup.model.Operacao;

@Service
public class OperacaoBo {

	@Autowired
	private UsuarioDao usuarioDao;
	@Autowired
	private EstacaoDao estacaoDao;
	@Autowired
	private BackupDao backupDao;
	@Autowired
	private OperacaoDao operacaoDao;

	public void inserir(String login_solicitante,
						String login_proprietario,
						String nome_estacao,
						String nome_backup,
						Operacao operacao)
			throws DataRetrievalFailureException, DataIntegrityViolationException {

		if (usuarioDao.obter(login_solicitante) == null) {
			throw new DataRetrievalFailureException("Usuário solicitante não encontrado");
		}

		if (usuarioDao.obter(login_proprietario) == null) {
			throw new DataRetrievalFailureException("Usuário proprietário não encontrado");
		}

		if (estacaoDao.obter(nome_estacao) == null) {
			throw new DataRetrievalFailureException("Estação não encontrada");
		}

		if (backupDao.obter(login_proprietario, nome_estacao, nome_backup) == null) {
			throw new DataRetrievalFailureException("Backup não encontrado");
		}

		Validador.validar(operacao);

		if (operacaoDao.obter(login_proprietario, nome_estacao, nome_backup, operacao.getData()) != null) {
			throw new DataIntegrityViolationException("Operação já existente");
		}

		operacaoDao.inserir(login_solicitante, login_proprietario, nome_estacao, nome_backup, operacao);
	}

	public void modificar(	String login_solicitante,
							String login_proprietario,
							String nome_estacao,
							String nome_backup,
							Date data_operacao,
							Map<String, Object> valores)
			throws DataRetrievalFailureException, DataIntegrityViolationException {

		if (usuarioDao.obter(login_solicitante) == null) {
			throw new DataRetrievalFailureException("Usuário solicitante não encontrado");
		}

		if (usuarioDao.obter(login_proprietario) == null) {
			throw new DataRetrievalFailureException("Usuário proprietário não encontrado");
		}

		if (estacaoDao.obter(nome_estacao) == null) {
			throw new DataRetrievalFailureException("Estação não encontrada");
		}

		if (backupDao.obter(login_proprietario, nome_estacao, nome_backup) == null) {
			throw new DataRetrievalFailureException("Backup não encontrado");
		}

		Operacao operacao = operacaoDao.obter(login_proprietario, nome_estacao, nome_backup, data_operacao);
		if (operacao == null) {
			throw new DataRetrievalFailureException("Operação não encontrada");
		}

		Modificador.modificar(operacao, valores);
		Validador.validar(operacao);

		operacaoDao.modificar(login_solicitante, operacao);
	}

	public Operacao obter(	String login_proprietario,
							String nome_estacao,
							String nome_backup,
							Date data_operacao)
			throws DataRetrievalFailureException, DataIntegrityViolationException {

		if (usuarioDao.obter(login_proprietario) == null) {
			throw new DataRetrievalFailureException("Usuário proprietário não encontrado");
		}

		if (estacaoDao.obter(nome_estacao) == null) {
			throw new DataRetrievalFailureException("Estação não encontrada");
		}

		if (backupDao.obter(login_proprietario, nome_estacao, nome_backup) == null) {
			throw new DataRetrievalFailureException("Backup não encontrado");
		}

		Operacao operacao = operacaoDao.obter(login_proprietario, nome_estacao, nome_backup, data_operacao);
		if (operacao == null) {
			throw new DataRetrievalFailureException("Operação não encontrada");
		}

		return operacao;
	}

}
