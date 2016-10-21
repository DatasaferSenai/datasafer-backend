package datasafer.backup.bo;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import datasafer.backup.dao.OperacaoDao;
import datasafer.backup.model.Backup;
import datasafer.backup.model.Operacao;

@Service
public class OperacaoBo {

	@Autowired
	private OperacaoDao operacaoDao;

	public void modificarOperacao(Operacao operacao) {
		operacaoDao.modificar(operacao);
	}
	
	public Operacao obterOperacao(String login_usuario, String nome_host, String nome_backup, Date data_operacao){
		return operacaoDao.obter(login_usuario, nome_host, nome_backup, data_operacao);
	}
	
}
