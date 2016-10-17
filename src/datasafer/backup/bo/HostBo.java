package datasafer.backup.bo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import datasafer.backup.dao.HostDao;
import datasafer.backup.model.Host;

@Service
public class HostBo {

	@Autowired
	private HostDao hostDao;

	public Host inserirHost(String login_usuario, Host host) {
		return hostDao.inserirHost(login_usuario, host);
	}
	
	public void modificarHost(Host host) {
		hostDao.modificarHost(host);
	}
	
	public Host obterHost(String login_usuario, String nome_host) {
		return hostDao.obterHost(login_usuario, nome_host);
	}

}
