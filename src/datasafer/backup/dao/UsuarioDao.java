package datasafer.backup.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import datasafer.backup.dao.utility.Modificador;
import datasafer.backup.model.Backup;
import datasafer.backup.model.Estacao;
import datasafer.backup.model.Notificacao;
import datasafer.backup.model.Permissao;
import datasafer.backup.model.Registro;
import datasafer.backup.model.Usuario;

@Repository
public class UsuarioDao {

	@PersistenceContext
	private EntityManager manager;
	@Autowired
	private Modificador modificador;
	@Autowired
	private PermissaoDao permissaoDao;
	@Autowired
	private EstacaoDao estacaoDao;

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
																"SELECT u.superior "
																		+ "FROM Usuario u "
																		+ "WHERE u.id = :id_usuario",
																Usuario.class)
													.setParameter("id_usuario", usuario.getId())
													.getResultList();

		return resultadosSuperior.isEmpty()	? null
											: resultadosSuperior.get(0);
	}

	@Transactional
	public void insereUsuario(	Usuario solicitante,
								Usuario superior,
								Usuario usuario) {

		if (solicitante != null && !permissaoDao.temPermissao(solicitante, superior, "colaboradores", Permissao.Tipo.INSERIR)) {
			throw new AccessDeniedException("O solicitante não tem permissão para inserir colaboradores neste usuário");
		}

		solicitante = (solicitante == null	? null
											: manager.find(Usuario.class, solicitante.getId()));
		superior = (superior == null	? null
										: manager.find(Usuario.class, superior.getId()));

		Usuario existente = this.obtemUsuario(usuario.getLogin());
		if (existente != null) {
			throw new DataIntegrityViolationException("O usuário " + usuario.getLogin() + " já existe");
		}

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
								Usuario valores) {

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

		List<Registro> registros = modificador.modifica(solicitante, usuario, valores);

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

	@Transactional
	public void insereEstacao(	Usuario solicitante,
								Usuario gerenciador,
								Estacao estacao) {

		solicitante = (solicitante == null	? null
											: manager.find(Usuario.class, solicitante.getId()));
		gerenciador = manager.find(Usuario.class, gerenciador.getId());

		Estacao existente = estacaoDao.obtemEstacao(estacao.getNome());
		if (existente != null) {
			throw new DataIntegrityViolationException("Estação '" + estacao.getNome() + "' já existente");
		}

		// estacao .getRegistros()
		// .addAll(modificador.modifica(solicitante, estacao, null));

		gerenciador	.getEstacoes()
					.add(estacao);
		estacao.setGerenciador(gerenciador);

		manager.persist(estacao);
	}
}
