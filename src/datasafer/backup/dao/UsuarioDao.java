package datasafer.backup.dao;

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.dao.DataIntegrityViolationException;
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
public class UsuarioDao {

	@PersistenceContext
	private EntityManager manager;

	// @Transactional
	public Usuario obtem(String login_usuario) {
		List<Usuario> resultadosUsuario = manager	.createQuery(
				"SELECT usuario FROM Usuario usuario "
						+ "WHERE usuario.login = :login_usuario ",
				Usuario.class)
													.setParameter("login_usuario", login_usuario)
													.getResultList();

		return resultadosUsuario.isEmpty() ? null : resultadosUsuario.get(0);
	}

	// @Transactional
	public Usuario carregaArmazenamentoOcupado(Usuario proprietario) {

		@SuppressWarnings("unchecked")
		List<Long> resultadosArmazenamentoOcupado = manager	.createQuery(
				"SELECT SUM(operacao.tamanho) FROM Operacao operacao "
						+ "WHERE operacao.backup.proprietario.id = :id_proprietario "
						+ "AND operacao.data = (SELECT MAX(ultimaOperacao.data) FROM Operacao ultimaOperacao WHERE operacao.backup = ultimaOperacao.backup AND ultimaOperacao.status = :status_operacao) ")
															.setParameter("id_proprietario", proprietario.getId())
															.setParameter("status_operacao", Operacao.Status.SUCESSO)
															.getResultList();

		proprietario.setArmazenamentoOcupado(
				!resultadosArmazenamentoOcupado.isEmpty() && resultadosArmazenamentoOcupado.get(0) != null ? resultadosArmazenamentoOcupado.get(0) : 0L);

		return proprietario;
	}

	// @Transactional
	public List<Usuario> obtemArmazenamentoOcupado(List<Usuario> usuarios) {
		for (Usuario usuario : usuarios) {
			this.carregaArmazenamentoOcupado(usuario);
		}
		return usuarios;
	}

	// @Transactional
	public Usuario carregaStatusBackups(Usuario proprietario) {

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
	public List<Usuario> obtemStatusBackups(List<Usuario> usuarios) {
		for (Usuario usuario : usuarios) {
			this.carregaStatusBackups(usuario);
		}
		return usuarios;
	}

	@Transactional
	public void inserir(Usuario solicitante,
						Usuario superior,
						Usuario usuario) {

		solicitante = (solicitante == null ? null : manager.find(Usuario.class, solicitante.getId()));
		superior = (superior == null ? null : manager.find(Usuario.class, superior.getId()));

		Validador.validar(usuario);

		Usuario existente = this.obtem(usuario.getLogin());
		if (existente != null) {
			throw new DataIntegrityViolationException("Usuário '" + usuario.getLogin() + "' já existente");
		}

		usuario	.getRegistros()
				.addAll(Registrador.inserir(solicitante, usuario));

		if (superior != null) {
			superior.getColaboradores()
					.add(usuario);
		}
		usuario.setSuperior(superior);

		manager.persist(usuario);
	}

	@Transactional
	public void modificar(	Usuario solicitante,
							Usuario usuario,
							Usuario valores) {

		solicitante = (solicitante == null ? null : manager.find(Usuario.class, solicitante.getId()));
		usuario = manager.find(Usuario.class, usuario.getId());

		if (valores.getLogin() != null && !valores	.getLogin()
													.equals(usuario.getLogin())) {

			Usuario existente = this.obtem(valores.getLogin());
			if (existente != null) {
				throw new DataIntegrityViolationException("Usuário '" + valores.getLogin() + "' já existente");
			}

		}

		List<Registro> registros = Registrador.modificar(solicitante, usuario, valores);

		Validador.validar(usuario);

		usuario	.getRegistros()
				.addAll(registros);

		manager.persist(usuario);
	}

	@Transactional
	public List<Usuario> obtemColaboradores(Usuario superior) {
		return manager	.createQuery(
				"SELECT colaborador FROM Usuario colaborador "
						+ "JOIN FETCH colaborador.superior superior "
						+ "WHERE superior.id = :id_superior ",
				Usuario.class)
						.setParameter("id_superior", superior.getId())
						.getResultList();
	}

	@Transactional
	public List<Backup> obtemBackups(Usuario proprietario) {
		return manager	.createQuery(
				"SELECT backup FROM Backup backup "
						+ "JOIN FETCH backup.proprietario proprietario "
						+ "WHERE proprietario.id = :id_proprietario ",
				Backup.class)
						.setParameter("id_proprietario", proprietario.getId())
						.getResultList();
	}

	@Transactional
	public List<Estacao> obtemEstacoes(Usuario gerenciador) {
		return manager	.createQuery(
				"SELECT estacao FROM Estacao estacao "
						+ "JOIN FETCH estacao.gerenciador gerenciador "
						+ "WHERE gerenciador.id = :id_gerenciador ",
				Estacao.class)
						.setParameter("id_gerenciador", gerenciador.getId())
						.getResultList();
	}

	// @Transactional
	public Usuario logar(Usuario usuario) {
		List<Usuario> results = manager	.createQuery(
				"SELECT usuario FROM Usuario usuario "
						+ "WHERE usuario.login = :login_usuario "
						+ "AND usuario.senha = :senha_usuario ",
				Usuario.class)
										.setParameter("login_usuario", usuario.getLogin())
										.setParameter("senha_usuario", usuario.getSenha())
										.getResultList();

		return results.isEmpty() ? null : results.get(0);
	}
}
