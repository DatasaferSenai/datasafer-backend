package datasafer.backup.bo;

import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import datasafer.backup.dao.HostDao;
import datasafer.backup.model.Host;
import datasafer.backup.model.Usuario;

@Service
public class HostBo {

	@Autowired
	private HostDao hostDao;

	public Host obter(String login_usuario, String nome_host) {
		return hostDao.obter(login_usuario, nome_host);
	}

}
