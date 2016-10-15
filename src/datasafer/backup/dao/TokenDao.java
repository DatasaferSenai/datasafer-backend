package datasafer.backup.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import datasafer.backup.model.Host;
import datasafer.backup.model.Token;
import datasafer.backup.model.Usuario;

@Repository
public class TokenDao {

	@PersistenceContext
	private EntityManager manager;
	
	public Token obter(String chave_token) {
		return manager.find(Token.class, chave_token);
	}
	
	public void inserir(Token token){
		manager.persist(token);
	}
	
	public void inserir(Usuario usuario, String chave_token){
		Token token = this.obter(chave_token);
		token.setUsuario(usuario);
		
	}
	
}
