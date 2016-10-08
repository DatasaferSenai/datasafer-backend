package datasafer.backup.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import datasafer.backup.model.Operacao;

@Repository
public class OperacaoDao {

	@PersistenceContext
	private EntityManager manager;
	
	//@Transactional
	public Operacao obter(Long idOperacao){
		return manager.find(Operacao.class, idOperacao);
	}
	
}
