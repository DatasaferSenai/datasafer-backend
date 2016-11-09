package datasafer.backup.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import datasafer.backup.model.Estacao;
import datasafer.backup.model.Registro;
import datasafer.backup.model.Registro.Tipo;
import datasafer.backup.model.Usuario;

@Repository
public class EstacaoDao {

	@PersistenceContext
	private EntityManager manager;

	// @Transactional
	public Estacao obter(String nome_estacao) {
		List<Estacao> results = manager	.createQuery("SELECT e FROM Estacao e WHERE e.nome = :nome_estacao", Estacao.class)
										.setParameter("nome_estacao", nome_estacao)
										.getResultList();
		if (!results.isEmpty()) {
			return results.get(0);
		} else {
			return null;
		}
	}

	@Transactional
	public void modificar(	String login_solicitante,
							Estacao estacao) {

		estacao = manager.merge(estacao);

		Registro registro = new Registro();
		registro.setSolicitante(
				login_solicitante == null ? null : manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_solicitante", Usuario.class)
															.setParameter("login_solicitante", login_solicitante)
															.getSingleResult());
		registro.setData(Calendar	.getInstance(TimeZone.getDefault())
									.getTime());
		registro.setTipo(Tipo.MODIFICADO);

		estacao	.getRegistros()
				.add(registro);
	}

	@Transactional
	public void inserir(String login_solicitante,
						String login_gerenciador,
						Estacao estacao) {

		Registro registro = new Registro();
		registro.setSolicitante(
				login_solicitante == null ? null : manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_solicitante", Usuario.class)
															.setParameter("login_solicitante", login_solicitante)
															.getSingleResult());
		registro.setData(Calendar	.getInstance(TimeZone.getDefault())
									.getTime());
		registro.setTipo(Tipo.INSERIDO);

		estacao.setRegistros(new ArrayList<Registro>(Arrays.asList(registro)));

		estacao.setGerenciador(
				login_gerenciador == null ? null : manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_proprietario", Usuario.class)
															.setParameter("login_proprietario", login_gerenciador)
															.getSingleResult());

		manager.persist(estacao);
	}

}
