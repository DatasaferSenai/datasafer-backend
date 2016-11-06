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

import datasafer.backup.model.Backup;
import datasafer.backup.model.Estacao;
import datasafer.backup.model.Registro;
import datasafer.backup.model.Registro.Tipo;
import datasafer.backup.model.Usuario;

@Repository
public class BackupDao {

	@PersistenceContext
	private EntityManager manager;

	// @Transactional
	public Backup obter(String login_proprietario, String nome_estacao, String nome_backup) {
		List<Backup> results = manager	.createQuery(
				"SELECT b FROM Backup b WHERE b.estacao.nome = :nome_estacao AND b.proprietario.login = :login_proprietario AND b.nome = :nome_backup",
				Backup.class)
										.setParameter("login_proprietario", login_proprietario)
										.setParameter("nome_estacao", nome_estacao)
										.setParameter("nome_backup", nome_backup)
										.getResultList();
		if (!results.isEmpty()) {
			return results.get(0);
		} else {
			return null;
		}
	}

	@Transactional
	public void inserir(String login_solicitante, String login_proprietario, String nome_estacao, Backup backup) {

		Registro registro = new Registro();
		registro.setSolicitante(
				login_solicitante == null ? null : manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_solicitante", Usuario.class)
															.setParameter("login_solicitante", login_solicitante)
															.getSingleResult());

		registro.setData(Calendar	.getInstance(TimeZone.getDefault())
									.getTime());
		registro.setTipo(Tipo.INSERIDO);

		backup.setRegistros(new ArrayList<Registro>(Arrays.asList(registro)));

		backup.setProprietario(
				login_proprietario == null ? null : manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_proprietario", Usuario.class)
															.setParameter("login_proprietario", login_proprietario)
															.getSingleResult());

		backup.setEstacao(manager	.createQuery("SELECT e FROM Estacao e WHERE e.nome = :nome_estacao", Estacao.class)
									.setParameter("nome_estacao", nome_estacao)
									.getSingleResult());

		manager.persist(backup);
	}

	@Transactional
	public void modificar(String login_solicitante, Backup backup) {

		backup = manager.merge(backup);

		Registro registro = new Registro();
		registro.setSolicitante(
				login_solicitante == null ? null : manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_solicitante", Usuario.class)
															.setParameter("login_solicitante", login_solicitante)
															.getSingleResult());
		registro.setData(Calendar	.getInstance(TimeZone.getDefault())
									.getTime());
		registro.setTipo(Tipo.MODIFICADO);

		backup	.getRegistros()
				.add(registro);

	}

}
