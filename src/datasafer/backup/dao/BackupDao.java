package datasafer.backup.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import datasafer.backup.model.Backup;
import datasafer.backup.model.Operacao;

@Repository
public class BackupDao {

	@PersistenceContext
	private EntityManager manager;
	
	//@Transactional
	public Backup obter(Long idBackup){
		return manager.find(Backup.class, idBackup);
	}
	
	@Transactional
	public void inserir(Long idBackup, Operacao operacao){
		operacao.setBackup(manager.find(Backup.class,idBackup));
		manager.persist(operacao);
	}

}
