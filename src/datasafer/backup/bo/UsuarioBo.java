package datasafer.backup.bo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import datasafer.backup.dao.UsuarioDao;
import datasafer.backup.model.Privilegio;
import datasafer.backup.model.Usuario;

@Service
public class UsuarioBo {
	
	@Autowired
	private UsuarioDao usuarioDao;
	
	public void inserirUsuario(String login_usuario, Usuario usuario) {
		usuarioDao.inserirUsuario(login_usuario, usuario);
	}

	public void modificarUsario(Usuario usuario) {
		usuarioDao.modificarUsuario(usuario);
	}
	
	public void modificarPrivilegio(String login_usuario, Privilegio privilegio) {
		usuarioDao.modificarPrivilegio(login_usuario,privilegio);
	}
	
	public Usuario obterUsuario(String login_usuario){
		return usuarioDao.obterUsuario(login_usuario);
	}
	
	public Usuario logar(Usuario usuario){
		return usuarioDao.logar(usuario);
	}
	
	public List<Usuario> listaUsuarios() {
		return usuarioDao.listaUsuarios();
	}

}
