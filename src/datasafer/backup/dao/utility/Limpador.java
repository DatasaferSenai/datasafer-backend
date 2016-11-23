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

	//@Scheduled(cron = "*/10 * * * * *")
	public void excecutaLimpeza() {
		try {
			notificacaoDao.limpaNotificacoes();
			autorizacaoDao.limpaAutorizacoes();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
