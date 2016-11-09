package datasafer.backup.bo;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;

import datasafer.backup.bo.helper.Modificador;
import datasafer.backup.bo.helper.Validador;
import datasafer.backup.dao.EstacaoDao;
import datasafer.backup.dao.UsuarioDao;
import datasafer.backup.model.Estacao;

@Service
public class EstacaoBo {

	@Autowired
	private UsuarioDao usuarioDao;
	@Autowired
	private EstacaoDao estacaoDao;

	public Estacao obter(String nome_estacao) throws DataRetrievalFailureException {

		Estacao estacao = estacaoDao.obter(nome_estacao);
		if (estacao == null) {
			throw new DataRetrievalFailureException("Estação não encontrada");
		}

		return estacao;
	}

	public void inserir(String login_solicitante,
						String login_gerenciador,
						Estacao estacao)
			throws DataRetrievalFailureException, DataIntegrityViolationException {

		if (usuarioDao.obter(login_solicitante) == null) {
			throw new DataRetrievalFailureException("Usuário solicitante não encontrado");
		}

		Validador.validar(estacao);

		if (estacaoDao.obter(estacao.getNome()) != null) {
			throw new DataIntegrityViolationException("Estação já existente");
		}

		estacaoDao.inserir(login_solicitante, login_gerenciador, estacao);
	}

	public void modificar(	String login_solicitante,
							String nome_estacao,
							Map<String, Object> valores) {

		if (usuarioDao.obter(login_solicitante) == null) {
			throw new DataRetrievalFailureException("Usuário solicitante não encontrado");
		}

		Estacao estacao = estacaoDao.obter(nome_estacao);
		if (estacao == null) {
			throw new DataRetrievalFailureException("Estação não encontrada");
		}

		Modificador.modificar(estacao, valores);
		Validador.validar(estacao);

		estacaoDao.modificar(login_solicitante, estacao);
	}

}
