package datasafer.backup.bo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import datasafer.backup.dao.BackupDao;
import datasafer.backup.model.Backup;

@Service
public class BackupBo {

	@Autowired
	private BackupDao backupDao;

	public Backup inserirBackup(String login_usuario, String nome_host, Backup backup) {
		return backupDao.inserirBackup(login_usuario, nome_host, backup);
	}
	
	public void modificarBackup(Backup backup) {
		backupDao.modificarBackup(backup);
	}
	
	public Backup obterBackup(String login_usuario, String nome_host, String nome_backup){
		return backupDao.obterBackup(login_usuario, nome_host, nome_backup);
	}
	
}
