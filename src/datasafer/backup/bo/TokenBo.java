package datasafer.backup.bo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import datasafer.backup.dao.TokenDao;
import datasafer.backup.model.Token;

@Service
public class TokenBo {
	
	@Autowired
	private TokenDao tokenDao;
	
	public void validar(String login_usuario, String chave_token) throws Exception{
		Token token = tokenDao.obter(chave_token);
	}
	
}
