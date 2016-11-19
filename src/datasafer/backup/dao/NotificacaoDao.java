package datasafer.backup.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import datasafer.backup.model.Notificacao;
import datasafer.backup.model.Usuario;

@Repository
public class NotificacaoDao {

	@PersistenceContext
	private EntityManager manager;

	@Transactional
	public void insereNotificacao(	Usuario usuario,
									Notificacao notificacao) {

		usuario = manager.find(Usuario.class, usuario.getId());

		usuario	.getNotificacoes()
				.add(notificacao);
		notificacao.setUsuario(usuario);

		manager.persist(notificacao);
	}

	@Transactional
	public void excluiNotificacao(Notificacao notificacao) {
		manager.remove(notificacao);
	}

}
