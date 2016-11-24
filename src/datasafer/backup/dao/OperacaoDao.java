package datasafer.backup.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import datasafer.backup.dao.utility.Modificador;
import datasafer.backup.dao.utility.Validador;
import datasafer.backup.model.Backup;
import datasafer.backup.model.Operacao;
import datasafer.backup.model.Registro;
import datasafer.backup.model.Usuario;

@Repository
public class OperacaoDao {

	@PersistenceContext
	private EntityManager manager;

	@Autowired
	private Modificador modificador;
	
	@Transactional
	public void insereOperacao(	Usuario solicitante,
								Backup backup,
								Operacao operacao) {

		solicitante = (solicitante == null	? null
											: manager.find(Usuario.class, solicitante.getId()));
		backup = manager.find(Backup.class, backup.getId());

		Validador.validar(operacao);

		operacao.getRegistros()
				.addAll(modificador.modifica(solicitante, operacao, null));

		backup	.getOperacoes()
				.add(operacao);
		operacao.setBackup(backup);

		manager.persist(operacao);
	}

	@Transactional
	public void modificaOperacao(	Usuario solicitante,
									Operacao operacao,
									Operacao valores) {

		solicitante = (solicitante == null	? null
											: manager.find(Usuario.class, solicitante.getId()));
		operacao = manager.find(Operacao.class, operacao.getId());

		List<Registro> registros = modificador.modifica(solicitante, operacao, valores);

		Validador.validar(operacao);

		operacao.getRegistros()
				.addAll(registros);

		manager.persist(operacao);
	}

}
