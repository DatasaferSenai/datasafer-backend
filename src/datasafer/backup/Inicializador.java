package datasafer.backup;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import datasafer.backup.bo.BackupBo;
import datasafer.backup.bo.HostBo;
import datasafer.backup.bo.OperacaoBo;
import datasafer.backup.bo.PrivilegioBo;
import datasafer.backup.bo.UsuarioBo;
import datasafer.backup.model.Privilegio;
import datasafer.backup.model.Usuario;
import datasafer.backup.model.Usuario.Status;

@Service
public class Inicializador {

	@Autowired
	private UsuarioBo usuarioBo;
	@Autowired
	private HostBo hostBo;
	@Autowired
	private BackupBo backupBo;
	@Autowired
	private OperacaoBo operacaoBo;
	@Autowired
	private PrivilegioBo privilegioBo;

	@PostConstruct
	public void verificaAdmin() {
		
		Privilegio privilegio_admin = privilegioBo.obterPrivilegio("Administrador");
		if(privilegio_admin == null){
			privilegio_admin = new Privilegio();
			privilegio_admin.setNome("Administrador");
	
			Set<Privilegio.Permissao> permissoes = new HashSet<Privilegio.Permissao>();
			permissoes.add(Privilegio.Permissao.ADMINISTRADOR);
			privilegio_admin.setPermissoes(permissoes);
			
			privilegioBo.inserirPrivilegio(privilegio_admin);
		}
		
		Usuario usuario_admin = usuarioBo.obterUsuario("admin");
		if(usuario_admin == null){
			usuario_admin = new Usuario();
			
			usuario_admin.setArmazenamento(0L);
			usuario_admin.setHosts(null);
			usuario_admin.setLogin("admin");
			usuario_admin.setNome("Administrador");
			usuario_admin.setPrivilegio(privilegio_admin.getNome());
			usuario_admin.setSenha("admin");
			usuario_admin.setStatus(Status.ATIVO);

			usuarioBo.inserirUsuario(usuario_admin);
		}
		
	}

}
