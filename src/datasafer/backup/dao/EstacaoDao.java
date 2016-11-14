package datasafer.backup.dao;

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
import datasafer.backup.model.Estacao;
import datasafer.backup.model.Registro;
import datasafer.backup.model.Usuario;

@Repository
public class EstacaoDao {

	@PersistenceContext
	private EntityManager manager;
	@Autowired
	UsuarioDao usuarioDao;

	// @Transactional
	public Estacao obter(String nome_estacao) {
		List<Estacao> resultadosEstacao = manager	.createQuery("SELECT e FROM Estacao e WHERE e.nome = :nome_estacao", Estacao.class)
													.setParameter("nome_estacao", nome_estacao)
													.getResultList();

		return resultadosEstacao.isEmpty() ? null : resultadosEstacao.get(0);
	}

	@Transactional
	public void modificar(	String login_solicitante,
							String nome_estacao,
							Estacao valores) {

		Usuario solicitante = null;
		if (login_solicitante != null) {
			solicitante = usuarioDao.obter(login_solicitante);
			if (solicitante == null) {
				throw new DataRetrievalFailureException("Usuário solicitante '" + login_solicitante + "' não encontrado");
			}
		}

		Estacao estacao = this.obter(nome_estacao);
		if (estacao == null) {
			throw new DataRetrievalFailureException("Estação '" + nome_estacao + "' não encontrada");
		}

		if (valores.getNome() != null && !valores	.getNome()
													.equals(estacao.getNome())) {

			Estacao existente = this.obter(valores.getNome());
			if (existente != null) {
				throw new DataIntegrityViolationException("Usuário '" + valores.getNome() + "' já existente");
			}
		}

		if (solicitante != null) {
			List<Registro> registros = Registrador.modificar(solicitante, estacao, valores);

			Validador.validar(estacao);

			if (estacao.getRegistros() == null) {
				estacao.setRegistros(registros);
			} else {
				estacao	.getRegistros()
						.addAll(registros);
			}
		} else {
			Validador.validar(estacao);
		}

		manager.persist(estacao);
	}

	@Transactional
	public void inserir(String login_solicitante,
						String login_gerenciador,
						Estacao estacao) {

		Validador.validar(estacao);

		Usuario solicitante = null;
		if (login_solicitante != null) {
			solicitante = usuarioDao.obter(login_solicitante);
			if (solicitante == null) {
				throw new DataRetrievalFailureException("Usuário solicitante '" + login_solicitante + "' não encontrado");
			}
		}

		Usuario gerenciador = usuarioDao.obter(login_gerenciador);
		if (gerenciador == null) {
			throw new DataRetrievalFailureException("Usuário gerenciador '" + login_gerenciador + "' não encontrado");
		}

		Estacao existente = this.obter(estacao.getNome());
		if (existente != null) {
			throw new DataIntegrityViolationException("Estação '" + estacao.getNome() + "' já existente");
		}

		if (solicitante != null) {
			List<Registro> registros = Registrador.inserir(solicitante, estacao);

			if (estacao.getRegistros() == null) {
				estacao.setRegistros(registros);
			} else {
				estacao	.getRegistros()
						.addAll(registros);
			}
		}

		gerenciador.getEstacoes().add(estacao);
		estacao.setGerenciador(gerenciador);

		manager.persist(estacao);
	}

}
