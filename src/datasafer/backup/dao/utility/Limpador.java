package datasafer.backup.dao.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import datasafer.backup.dao.AutorizacaoDao;
import datasafer.backup.dao.NotificacaoDao;

@Component
public class Limpador {

	@Autowired
	private NotificacaoDao notificacaoDao;
	@Autowired
	private AutorizacaoDao autorizacaoDao;

	@Scheduled(cron = "0 0 12 * * *")
	public void excecutaLimpeza() {
		try {
			notificacaoDao.limpaNotificacoes(15);
			autorizacaoDao.limpaAutorizacoes(15);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
