package datasafer.backup.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import datasafer.backup.dao.helper.Registrador;
import datasafer.backup.dao.helper.Validador;
import datasafer.backup.model.Backup;
import datasafer.backup.model.Operacao;
import datasafer.backup.model.Registro;
import datasafer.backup.model.Usuario;

@Repository
public class OperacaoDao {

	@PersistenceContext
	private EntityManager manager;

	// @Transactional
	public Operacao obtemOperacao(	Backup backup,
							Date data_operacao) {
		List<Operacao> resultadosOperacao = manager	.createQuery(
				"SELECT operacao FROM Operacao operacao "
						+ "JOIN FETCH operacao.backup backup "
						+ "WHERE backup.id = :id_backup "
						+ "AND operacao.data = :data_operacao ",
				Operacao.class)
													.setParameter("id_backup", backup.getId())
													.setParameter("data_operacao", data_operacao)
													.getResultList();

		return resultadosOperacao.isEmpty() ? null : resultadosOperacao.get(0);
	}

	@Transactional
	public void insereOperacao(Usuario solicitante,
						Backup backup,
						Operacao operacao) {

		solicitante = (solicitante == null ? null : manager.find(Usuario.class, solicitante.getId()));
		backup = manager.find(Backup.class, backup.getId());

		Validador.validar(operacao);

		operacao.getRegistros()
				.addAll(Registrador.insere(solicitante, operacao));

		backup	.getOperacoes()
				.add(operacao);
		operacao.setBackup(backup);

		manager.persist(operacao);
	}

	@Transactional
	public void modificaOperacao(	Usuario solicitante,
							Operacao operacao,
							Operacao valores) {

		solicitante = (solicitante == null ? null : manager.find(Usuario.class, solicitante.getId()));
		operacao = manager.find(Operacao.class, operacao.getId());

		if (valores.getData() != null && !valores	.getData()
													.equals(operacao.getData())) {

			Operacao existente = this.obtemOperacao(operacao.getBackup(), valores.getData());
			if (existente != null) {
				throw new DataIntegrityViolationException(
						"Operação '" + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(valores.getData()) + "' já existente");
			}
		}

		List<Registro> registros = Registrador.modifica(solicitante, operacao, valores);

		Validador.validar(operacao);

		if (operacao.getRegistros() == null) {
			operacao.setRegistros(registros);
		} else {
			operacao.getRegistros()
					.addAll(registros);
		}

		manager.persist(operacao);
	}

}
