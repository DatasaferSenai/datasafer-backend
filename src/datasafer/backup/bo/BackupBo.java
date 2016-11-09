package datasafer.backup.bo;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;

import datasafer.backup.bo.helper.Modificador;
import datasafer.backup.bo.helper.Validador;
import datasafer.backup.dao.BackupDao;
import datasafer.backup.dao.EstacaoDao;
import datasafer.backup.dao.UsuarioDao;
import datasafer.backup.model.Backup;

@Service
public class BackupBo {

	@Autowired
	private UsuarioDao usuarioDao;
	@Autowired
	private EstacaoDao estacaoDao;
	@Autowired
	private BackupDao backupDao;

	public void inserir(String login_solicitante,
						String login_proprietario,
						String nome_estacao,
						Backup backup)
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

		Validador.validar(backup);

		if (backupDao.obter(login_proprietario, nome_estacao, backup.getNome()) != null) {
			throw new DataIntegrityViolationException("Backup já existente");
		}

		backupDao.inserir(login_solicitante, login_proprietario, nome_estacao, backup);
	}

	public void modificar(	String login_solicitante,
							String login_proprietario,
							String nome_estacao,
							String nome_backup,
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

		Backup backup = backupDao.obter(login_proprietario, nome_estacao, nome_backup);
		if (backup == null) {
			throw new DataRetrievalFailureException("Backup não encontrado");
		}

		Modificador.modificar(backup, valores);
		Validador.validar(backup);

		backupDao.modificar(login_solicitante, backup);
	}

	public Backup obter(String login_proprietario,
						String nome_estacao,
						String nome_backup)
			throws DataRetrievalFailureException {

		if (usuarioDao.obter(login_proprietario) == null) {
			throw new DataRetrievalFailureException("Usuário proprietário não encontrado");
		}

		if (estacaoDao.obter(nome_estacao) == null) {
			throw new DataRetrievalFailureException("Estação não encontrada");
		}

		Backup backup = backupDao.obter(login_proprietario, nome_estacao, nome_backup);
		if (backup == null) {
			throw new DataRetrievalFailureException("Backup não encontrado");
		}

		return backup;
	}

	// public void excluir(String login_solicitante,
	// String login_proprietario,
	// String nome_estacao,
	// String nome_backup)
	// throws DataRetrievalFailureException, DataIntegrityViolationException {
	//
	// if (usuarioDao.obter(login_solicitante) == null) {
	// throw new DataRetrievalFailureException("Usuário solicitante não
	// encontrado");
	// }
	//
	// if (usuarioDao.obter(login_proprietario) == null) {
	// throw new DataRetrievalFailureException("Usuário proprietário não
	// encontrado");
	// }
	//
	// if (estacaoDao.obter(nome_estacao) == null) {
	// throw new DataRetrievalFailureException("Estação não encontrada");
	// }
	//
	// if (backupDao.obter(login_proprietario, nome_estacao, nome_backup) ==
	// null) {
	// throw new DataRetrievalFailureException("Backup não encontrado");
	// }
	//
	// // backupDao.excluir(login_solicitante, login_proprietario,
	// // nome_estacao, nome_backup);
	//
	// }
}
