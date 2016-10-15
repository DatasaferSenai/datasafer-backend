package datasafer.backup.bo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import datasafer.backup.dao.UsuarioDao;
import datasafer.backup.model.Token;
import datasafer.backup.model.Usuario;
import datasafer.backup.model.Usuario.Privilegio;
import datasafer.backup.model.Usuario.Status;

@Service
public class UsuarioBo {
	
	@Autowired
	private UsuarioDao usuarioDao;
	
	public void verificaAdmin(){
		Usuario admin = usuarioDao.obter("admin");
		if (admin == null) {
			admin = new Usuario();
			
			admin.setArmazenamento(0L);
			admin.setHosts(null);
			admin.setLogin("admin");
			admin.setNome("Administrador");
			admin.setPrivilegio(Privilegio.SUPER);
			admin.setSenha("admin");
			admin.setStatus(Status.ATIVO);
			
			usuarioDao.inserir(admin);
		}
	}
	
	public void inserir(Usuario usuario) {
		usuarioDao.inserir(usuario);
	} 
	
	public Usuario obter(String login_usuario){
		return usuarioDao.obter(login_usuario);
	}
	
	public Usuario logar(Usuario usuario){
		return usuarioDao.logar(usuario);
	}
}
