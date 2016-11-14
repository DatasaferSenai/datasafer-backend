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

import datasafer.backup.dao.BackupDao;
import datasafer.backup.dao.EstacaoDao;
import datasafer.backup.dao.OperacaoDao;
import datasafer.backup.dao.UsuarioDao;
import datasafer.backup.model.Backup;
import datasafer.backup.model.Estacao;
import datasafer.backup.model.Operacao;
import datasafer.backup.model.Usuario;
import datasafer.backup.model.Usuario.Permissao;
import datasafer.backup.model.Usuario.Status;

@Service
public class Inicializador {

	@Autowired
	private UsuarioDao usuarioDao;
	@Autowired
	private EstacaoDao estacaoDao;
	@Autowired
	private BackupDao backupDao;
	@Autowired
	private OperacaoDao operacaoDao;

	@PostConstruct
	private void Inicializa() {
		System.out.println("==== Inicializando ====");
		verificaAdmin();
		populaUsuarios();
	}

	private void verificaAdmin() {

		Usuario usuario_sistema = usuarioDao.obter("system");
		if (usuario_sistema == null) {
			usuario_sistema = new Usuario();

			usuario_sistema.setArmazenamento(0L);
			usuario_sistema.setNome("Sistema");
			usuario_sistema.setEmail("");
			usuario_sistema.setLogin("system");
			usuario_sistema.setSenha("system");
			usuario_sistema.setStatus(Status.ATIVO);

			Set<Permissao> permissoes = new HashSet<Permissao>();
			for (Permissao p : Permissao.values())
				permissoes.add(p);
			usuario_sistema.setPermissoes(permissoes);

			usuarioDao.inserir(null, null, usuario_sistema);

		}

		Usuario usuario_admin = usuarioDao.obter("admin");
		if (usuario_admin == null) {
			usuario_admin = new Usuario();

			usuario_admin.setArmazenamento(0L);
			usuario_admin.setLogin("admin");
			usuario_admin.setNome("Administrador");
			usuario_admin.setEmail("admin@admin.com");
			usuario_admin.setSenha("admin");
			usuario_admin.setStatus(Status.ATIVO);

			Set<Permissao> permissoes = new HashSet<Permissao>();
			for (Permissao p : Permissao.values())
				permissoes.add(p);
			usuario_admin.setPermissoes(permissoes);

			usuarioDao.inserir("system", "system", usuario_admin);

		}
	}

	public void populaUsuarios() {

		System.out.println(" ==== POPULA ==== ");

		for (String nome : Arrays.asList("Giovanni Campaner", "Henrique Francisco da Silva", "Sheila Barreto", "Fellipe Thufik Costa", "Felipe Lemes Discher",
				"Hugo Henrique")) {
			List<String> nomes = Arrays.asList(nome	.toLowerCase()
													.split(" "));
			String login = nomes.get(0);
			for (int i = 1; i < nomes.size(); i++) {
				login += nomes	.get(i)
								.charAt(0);
			}

			Usuario usuario = usuarioDao.obter(login);
			if (usuario == null) {
				usuario = new Usuario();
				usuario.setNome(nome);
				usuario.setEmail(login + "@gmail.com");
				usuario.setArmazenamento(10000000L);
				usuario.setLogin(login);
				usuario.setSenha(login);
				usuario.setStatus(Status.ATIVO);

				Set<Permissao> permissoes = new HashSet<Permissao>();
				permissoes.addAll(Arrays.asList(Permissao.VISUALIZAR_USUARIOS, Permissao.VISUALIZAR_ESTACOES, Permissao.VISUALIZAR_OPERACOES,
						Permissao.INSERIR_USUARIOS, Permissao.INSERIR_ESTACOES, Permissao.INSERIR_BACKUPS, Permissao.INSERIR_OPERACOES,
						Permissao.MODIFICAR_USUARIOS, Permissao.MODIFICAR_BACKUPS, Permissao.EXCLUIR_USUARIOS, Permissao.EXCLUIR_BACKUPS));

				usuario.setPermissoes(permissoes);

				usuarioDao.inserir("system", "admin", usuario);

				populaEstacoes(login);
			}
		}
	}

	public void populaEstacoes(String login_usuario) {

		List<String> tiposDispositivos = Arrays.asList("PC", "Desktop", "Computador", "Notebook", "Netbook", "Laptop");
		List<String> nomesDispositivos = Arrays.asList("Trabalho", "Escola", "Casa", "Escritório", "Banheiro", "Quarto", "Sala");
		List<String> separadores = Arrays.asList("-", "_", ".", " ");

		Random gerador = new Random();

		int quantidade = gerador.nextInt(3) + 2;

		for (int n = 0; n < quantidade; n++) {
			int tipo_index = gerador.nextInt(tiposDispositivos.size());
			int nome_index = gerador.nextInt(nomesDispositivos.size());
			int separador_index = gerador.nextInt(separadores.size());

			String nome_estacao = tiposDispositivos.get(tipo_index) + separadores.get(separador_index) + nomesDispositivos.get(nome_index);

			Estacao estacao = estacaoDao.obter(nome_estacao);
			if (estacao == null) {
				estacao = new Estacao();
				estacao.setNome(nome_estacao);
				estacaoDao.inserir("system", login_usuario, estacao);

				populaBackups(login_usuario, nome_estacao);
			}
		}
	}

	public void populaBackups(	String login_usuario,
								String nome_estacao) {

		List<String> nomes_backups = Arrays.asList("Meus arquivos", "Minhas fotos", "Arquivos", "Fotos", "Imagens", "Meus videos", "Videos", "Midia",
				"Programas", "Importante", "Coisas importantes", "Arquivos importantes", "Software", "Banco de dados", "DB", "Apresentações", "Planilhas",
				"Planilhas importantes");

		Random gerador = new Random();

		int quantidade = gerador.nextInt(3) + 2;

		for (int n = 0; n < quantidade; n++) {
			int nomeIndex = gerador.nextInt(nomes_backups.size());
			long intervalo = gerador.nextInt(1000000);
			int inicio = gerador.nextInt(60);

			String nomeBackup = nomes_backups.get(nomeIndex);

			Backup backup = backupDao.obter(login_usuario, nome_estacao, nomeBackup);
			if (backup == null) {
				backup = new Backup();
				backup.setNome(nomeBackup);

				backup.setIntervalo(intervalo);

				backup.setInicio(new Date(Calendar	.getInstance(TimeZone.getDefault())
													.getTime()
													.getTime()
						+ (1000 * 60 * 60 * 24 * inicio)));
				backup.setPasta("C:\\" + nomeBackup	.toLowerCase()
													.replace(' ', '_'));

				backupDao.inserir("system", login_usuario, nome_estacao, backup);

				populaOperacoes(login_usuario, nome_estacao, backup.getNome());
			}
		}
	}

	public void populaOperacoes(String login_usuario,
								String nome_estacao,
								String nome_backup) {

		Random gerador = new Random();

		int quantidade = gerador.nextInt(7) + 3;

		for (int n = 0; n < quantidade; n++) {

			Date data = new Date(Calendar	.getInstance(TimeZone.getDefault())
											.getTime()
											.getTime()
					+ gerador.nextInt(365));

			Operacao operacao = operacaoDao.obter(login_usuario, nome_estacao, nome_estacao, data);
			if (operacao == null) {
				operacao = new Operacao();
				operacao.setData(new Date(Calendar	.getInstance(TimeZone.getDefault())
													.getTime()
													.getTime()
						+ gerador.nextInt(365)));
				operacao.setStatus(Operacao.Status.values()[gerador.nextInt(Operacao.Status.values().length)]);
				operacao.setTamanho((long) gerador.nextInt(10000000));
				operacaoDao.inserir("system", login_usuario, nome_estacao, nome_backup, operacao);

			}
		}

	}

}
