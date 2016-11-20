package datasafer.backup.dao;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import datasafer.backup.dao.utility.Modificador;
import datasafer.backup.dao.utility.Validador;
import datasafer.backup.model.Backup;
import datasafer.backup.model.Estacao;
import datasafer.backup.model.Notificacao;
import datasafer.backup.model.Operacao;
import datasafer.backup.model.Registro;
import datasafer.backup.model.Usuario;

@Repository
public class UsuarioDao {

	@PersistenceContext
	private EntityManager manager;

	// @Transactional
	public Usuario obtemUsuario(String login_usuario) {
		List<Usuario> resultadosUsuario = manager	.createQuery(
																"SELECT usuario FROM Usuario usuario "
																		+ "WHERE usuario.login = :login_usuario ",
																Usuario.class)
													.setParameter("login_usuario", login_usuario)
													.getResultList();

		return resultadosUsuario.isEmpty()	? null
											: resultadosUsuario.get(0);
	}

	// @Transactional
	public Usuario obtemSuperior(Usuario usuario) {

		List<Usuario> resultadosSuperior = manager	.createQuery(
																"SELECT usuario.superior "
																		+ "FROM Usuario usuario "
																		+ "WHERE usuario.id = :id_usuario",
																Usuario.class)
													.setParameter("id_usuario", usuario.getId())
													.getResultList();

		return resultadosSuperior.isEmpty()	? null
											: resultadosSuperior.get(0);
	}

	// @Transactional
	public Usuario carregaInfos(Usuario proprietario) {

		List<Long> resultadosArmazenamentoOcupado = manager	.createQuery(
																		"SELECT SUM(operacao.tamanho) FROM Operacao operacao "
																				+ "WHERE operacao.backup.proprietario.id = :id_proprietario "
																				+ "AND operacao.data = (SELECT MAX(ultimaOperacao.data) FROM Operacao ultimaOperacao WHERE operacao.backup = ultimaOperacao.backup AND ultimaOperacao.status = :status_operacao) ",
																		Long.class)
															.setParameter("id_proprietario", proprietario.getId())
															.setParameter("status_operacao", Operacao.Status.SUCESSO)
															.getResultList();

		proprietario.setArmazenamentoOcupado(
												!resultadosArmazenamentoOcupado.isEmpty()
														&& resultadosArmazenamentoOcupado.get(0) != null	? resultadosArmazenamentoOcupado.get(0)
																											: 0L);

		@SuppressWarnings("unchecked")
		List<Object> resultadosStatusBackups = manager	.createQuery(
																	"SELECT operacao.status, COUNT(DISTINCT operacao.backup) FROM Operacao operacao "
																			+ "WHERE operacao.backup.proprietario.id = :id_proprietario "
																			+ "AND operacao.data = (SELECT MAX(ultimaOperacao.data) FROM Operacao ultimaOperacao WHERE operacao.backup = ultimaOperacao.backup) "
																			+ "GROUP BY operacao.status ")
														.setParameter("id_proprietario", proprietario.getId())
														.getResultList();

		for (Iterator<Object> iterator = resultadosStatusBackups.iterator(); iterator.hasNext();) {
			Object obj[] = (Object[]) iterator.next();
			proprietario.getStatusBackups()
						.put((Operacao.Status) obj[0], (Long) obj[1]);
		}

		return proprietario;
	}

	// @Transactional
	public List<Usuario> carregaInfos(List<Usuario> usuarios) {
		for (Usuario usuario : usuarios) {
			this.carregaInfos(usuario);
		}
		return usuarios;
	}

	@Transactional
	public void insereUsuario(	Usuario solicitante,
								Usuario superior,
								Usuario usuario) throws DataIntegrityViolationException {

		solicitante = (solicitante == null	? null
											: manager.find(Usuario.class, solicitante.getId()));
		superior = (superior == null	? null
										: manager.find(Usuario.class, superior.getId()));

		Validador.validar(usuario);

		Usuario existente = this.obtemUsuario(usuario.getLogin());
		if (existente != null) {
			throw new DataIntegrityViolationException("O usuário " + usuario.getLogin() + " já existe");
		}

		usuario	.getRegistros()
				.addAll(Modificador.modifica(solicitante, usuario, null));

		if (superior != null) {
			superior.getColaboradores()
					.add(usuario);
		}
		usuario.setSuperior(superior);

		manager.persist(usuario);
	}

	@Transactional
	public void modificaUsuario(Usuario solicitante,
								Usuario usuario,
								Usuario valores) throws DataIntegrityViolationException {

		solicitante = (solicitante == null	? null
											: manager.find(Usuario.class, solicitante.getId()));
		usuario = manager.find(Usuario.class, usuario.getId());

		if (valores.getLogin() != null && !valores	.getLogin()
													.equals(usuario.getLogin())) {

			Usuario existente = this.obtemUsuario(valores.getLogin());
			if (existente != null) {
				throw new DataIntegrityViolationException("O usuário " + valores.getLogin() + " já existe");
			}

		}

		Set<Registro> registros = Modificador.modifica(solicitante, usuario, valores);

		Validador.validar(usuario);

		usuario	.getRegistros()
				.addAll(registros);

		manager.persist(usuario);
	}

	@Transactional
	public List<Usuario> obtemColaboradores(Usuario superior) {
		return manager	.createQuery(
									"SELECT c FROM Usuario c "
											+ "JOIN FETCH c.superior superior "
											+ "WHERE superior.id = :id_superior ",
									Usuario.class)
						.setParameter("id_superior", superior.getId())
						.getResultList();
	}

	@Transactional
	public List<Backup> obtemBackups(Usuario proprietario) {
		return manager	.createQuery(
									"SELECT b FROM Backup b "
											+ "JOIN FETCH b.proprietario proprietario "
											+ "WHERE proprietario.id = :id_proprietario ",
									Backup.class)
						.setParameter("id_proprietario", proprietario.getId())
						.getResultList();
	}

	@Transactional
	public List<Estacao> obtemEstacoes(Usuario gerenciador) {
		return manager	.createQuery(
									"SELECT e FROM Estacao e "
											+ "JOIN FETCH e.gerenciador gerenciador "
											+ "WHERE gerenciador.id = :id_gerenciador ",
									Estacao.class)
						.setParameter("id_gerenciador", gerenciador.getId())
						.getResultList();
	}

	@Transactional
	public List<Notificacao> obtemNotificacoes(Usuario usuario) {
		return manager	.createQuery(
									"SELECT n FROM Notificacao n "
											+ "JOIN FETCH n.usuario usuario "
											+ "WHERE usuario.id = :id_usuario ",
									Notificacao.class)
						.setParameter("id_usuario", usuario.getId())
						.getResultList();
	}

	// @Transactional
	public Usuario login(Usuario usuario) {
		List<Usuario> results = manager	.createQuery(
													"SELECT usuario FROM Usuario usuario "
															+ "WHERE usuario.login = :login_usuario "
															+ "AND usuario.senha = :senha_usuario ",
													Usuario.class)
										.setParameter("login_usuario", usuario.getLogin())
										.setParameter("senha_usuario", usuario.getSenha())
										.getResultList();

		return results.isEmpty()	? null
									: results.get(0);
	}

}
