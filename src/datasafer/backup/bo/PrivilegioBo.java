package datasafer.backup.bo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import datasafer.backup.dao.PrivilegioDao;
import datasafer.backup.model.Privilegio;
import datasafer.backup.model.Usuario;

@Service
public class PrivilegioBo {

	@Autowired
	private PrivilegioDao privilegioDao;

	public void inserirPrivilegio(String login_usuario, Privilegio privilegio) {
		privilegioDao.inserirPrivilegio(login_usuario, privilegio);
	}

	public void modificarPrivilegio(Privilegio privilegio) {
		privilegioDao.modificarPrivilegio(privilegio);
	}

	public Privilegio obterPrivilegio(String nome_privilegio) {
		return privilegioDao.obterPrivilegio(nome_privilegio);
	}
}
