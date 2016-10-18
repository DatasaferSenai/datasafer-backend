package datasafer.backup;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import datasafer.backup.bo.BackupBo;
import datasafer.backup.bo.HostBo;
import datasafer.backup.bo.OperacaoBo;
import datasafer.backup.bo.PrivilegioBo;
import datasafer.backup.bo.UsuarioBo;
import datasafer.backup.model.Backup;
import datasafer.backup.model.Backup.Frequencia;
import datasafer.backup.model.Host;
import datasafer.backup.model.Privilegio;
import datasafer.backup.model.Privilegio.Permissao;
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
	private void Inicializa() {
		verificaAdmin();
		populaPrivilegios();
		populaUsuarios();
	}

	private void verificaAdmin() {

		Privilegio privilegio_admin = privilegioBo.obterPrivilegio("Administrador");
		if (privilegio_admin == null) {
			privilegio_admin = new Privilegio();
			privilegio_admin.setNome("Administrador");

			Set<Permissao> permissoes = new HashSet<Permissao>();
			for (Permissao p : Permissao.values())
				permissoes.add(p);
			privilegio_admin.setPermissoes(permissoes);

			privilegioBo.inserirPrivilegio(privilegio_admin);
		}

		Usuario usuario_admin = usuarioBo.obterUsuario("admin");
		if (usuario_admin == null) {
			usuario_admin = new Usuario();

			usuario_admin.setArmazenamento(0L);
			usuario_admin.setHosts(null);
			usuario_admin.setLogin("admin");
			usuario_admin.setNome("Administrador");
			usuario_admin.setSenha("admin");
			usuario_admin.setStatus(Status.ATIVO);

			usuarioBo.inserirUsuario(null, usuario_admin);

			usuarioBo.modificarPrivilegio("admin", privilegio_admin);
		}
	}

	private void populaPrivilegios() {

		Date agora = Calendar.getInstance(TimeZone.getDefault()).getTime();

		Privilegio gerenciador = privilegioBo.obterPrivilegio("Gerenciador");
		if (gerenciador == null) {
			gerenciador = new Privilegio();
			gerenciador.setNome("Gerenciador");
			gerenciador.setDataInclusao(agora);
			gerenciador.setPermissoes(new HashSet<Permissao>(Arrays.asList(Permissao.VISUALIZAR_PRIVILEGIOS,
					Permissao.VISUALIZAR_PRIVILEGIOS, Permissao.VISUALIZAR_USUARIOS, Permissao.VISUALIZAR_HOSTS,
					Permissao.VISUALIZAR_BACKUPS, Permissao.VISUALIZAR_OPERACOES, Permissao.INSERIR_USUARIOS,
					Permissao.INSERIR_HOSTS, Permissao.INSERIR_BACKUPS, Permissao.MODIFICAR_USUARIOS,
					Permissao.MODIFICAR_HOSTS, Permissao.MODIFICAR_BACKUPS, Permissao.EXCLUIR_USUARIOS,
					Permissao.EXCLUIR_HOSTS, Permissao.EXCLUIR_BACKUPS)));
			privilegioBo.inserirPrivilegio(gerenciador);
		}

		Privilegio operador = privilegioBo.obterPrivilegio("Operador");
		if (operador == null) {
			operador = new Privilegio();
			operador.setNome("Operador");
			operador.setDataInclusao(agora);
			operador.setPermissoes(new HashSet<Permissao>(Arrays.asList(Permissao.VISUALIZAR_HOSTS,
					Permissao.VISUALIZAR_BACKUPS, Permissao.VISUALIZAR_OPERACOES, Permissao.INSERIR_BACKUPS,
					Permissao.MODIFICAR_BACKUPS, Permissao.EXCLUIR_BACKUPS)));
			privilegioBo.inserirPrivilegio(operador);
		}

		Privilegio visualizacao = privilegioBo.obterPrivilegio("Visualização");
		if (visualizacao == null) {
			visualizacao = new Privilegio();
			visualizacao.setNome("Visualização");
			visualizacao.setDataInclusao(agora);
			visualizacao.setPermissoes(new HashSet<Permissao>(Arrays.asList(Permissao.VISUALIZAR_HOSTS,
					Permissao.VISUALIZAR_BACKUPS, Permissao.VISUALIZAR_OPERACOES)));
			privilegioBo.inserirPrivilegio(visualizacao);
		}
	}

	public void populaUsuarios() {

		Date agora = Calendar.getInstance(TimeZone.getDefault()).getTime();

		for (String nome : Arrays.asList("Giovanni Campaner", "Henrique Francisco da Silva", "Sheila Barreto",
				"Fellipe Thufik Costa", "Felipe Lemes Discher", "Hugo Henrique")) {
			List<String> nomes = Arrays.asList(nome.toLowerCase().split(" "));
			String login = nomes.get(0);
			for (int i = 1; i < nomes.size(); i++) {
				login += nomes.get(i).charAt(0);
			}

			Usuario usuario = usuarioBo.obterUsuario(login);
			if (usuario == null) {
				usuario = new Usuario();
				usuario.setNome(nome);
				usuario.setDataInclusao(agora);
				usuario.setArmazenamento(10000000L);
				usuario.setLogin(login);
				usuario.setSenha(login);
				usuario.setStatus(Status.ATIVO);

				usuarioBo.inserirUsuario("admin", usuario);

				Privilegio privilegio_giovanni = privilegioBo.obterPrivilegio("Gerenciador");
				usuarioBo.modificarPrivilegio(login, privilegio_giovanni);

				populaHosts(login);
			}
		}
	}

	public void populaHosts(String login_usuario) {

		Date agora = Calendar.getInstance(TimeZone.getDefault()).getTime();

		List<String> tiposDispositivos = Arrays.asList("PC", "Desktop", "Computador", "Notebook", "Netbook", "Laptop");
		List<String> nomesDispositivos = Arrays.asList("Trabalho", "Escola", "Casa", "Escritório", "Banheiro", "Quarto",
				"Sala");
		List<String> separadores = Arrays.asList("-", "_", ".", " ");

		Random gerador = new Random();

		int quantidade = gerador.nextInt(10);

		for (int n = 0; n < quantidade; n++) {
			int tipo_index = gerador.nextInt(tiposDispositivos.size());
			int nome_index = gerador.nextInt(nomesDispositivos.size());
			int separador_index = gerador.nextInt(separadores.size());

			String nome_host = tiposDispositivos.get(tipo_index) + separadores.get(separador_index)
					+ nomesDispositivos.get(nome_index);

			Host host = hostBo.obterHost(login_usuario, nome_host);
			if (host == null) {
				host = new Host();
				host.setDataInclusao(agora);
				host.setNome(nome_host);
				hostBo.inserirHost(login_usuario, host);
				
				populaBackups(login_usuario,nome_host);
			}
		}
	}

	public void populaBackups(String login_usuario, String nome_host) {

		Date agora = Calendar.getInstance(TimeZone.getDefault()).getTime();

		List<String> nomes_backups = Arrays.asList("Meus arquivos", "Minhas fotos", "Arquivos", "Fotos", "Imagens",
				"Meus videos", "Videos", "Midia", "Programas", "Importante", "Coisas importantes",
				"Arquivos importantes", "Software", "Banco de dados", "DB", "Apresentações", "Planilhas",
				"Planilhas importantes");

		Random gerador = new Random();

		int quantidade = gerador.nextInt(20);

		for (int n = 0; n < quantidade; n++) {
			int nomeIndex = gerador.nextInt(nomes_backups.size());
			int frequenciaIndex = gerador.nextInt(Frequencia.values().length);
			int intervalo = gerador.nextInt(111) + 10;
			int inicio = gerador.nextInt(60);

			String nomeBackup = nomes_backups.get(nomeIndex);
			Frequencia frequencia = Frequencia.values()[frequenciaIndex];

			Backup backup = backupBo.obterBackup(login_usuario, nome_host, nomeBackup);
			if (backup == null) {
				backup = new Backup();
				backup.setDataInclusao(agora);
				backup.setNome(nomeBackup);
				backup.setFrequencia(frequencia);
				if(frequencia == Frequencia.INTERVALO){
					backup.setIntervalo(intervalo);
				}
				backup.setInicio(new Date(agora.getTime() + (1000 * 60 * 60 * 24 * inicio)));
				backup.setPasta("C:\\" + nomeBackup.toLowerCase().replace(' ', '_'));
				
				backupBo.inserirBackup(login_usuario, nome_host, backup);
			}
		}
	}

}
