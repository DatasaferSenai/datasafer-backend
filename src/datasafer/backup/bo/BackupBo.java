package datasafer.backup.bo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import datasafer.backup.dao.BackupDao;
import datasafer.backup.model.Backup;

@Service
public class BackupBo {

	@Autowired
	private BackupDao backupDao;
	
	public Backup obter(String login_usuario, String nome_host, String nome_backup){
		return backupDao.obter(login_usuario, nome_host, nome_backup);
	}
	
}
