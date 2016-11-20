package datasafer.backup.dao.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import datasafer.backup.dao.AutorizacaoDao;
import datasafer.backup.dao.NotificacaoDao;

public class Limpador {

	@Autowired
	private static NotificacaoDao notificacaoDao;
	@Autowired
	private static AutorizacaoDao autorizacaoDao;

	@Scheduled(cron = "*/60 * * * * *") /* Todos os dias as 12:00:00 (24HR) */
	public static void excecutaLimpeza() {
		notificacaoDao.limpaNotificacoes();
		autorizacaoDao.limpaAutorizacoes();
	}

}
