package datasafer.backup;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import datasafer.backup.dao.BackupDao;
import datasafer.backup.dao.EstacaoDao;
import datasafer.backup.dao.PermissaoDao;
import datasafer.backup.dao.UsuarioDao;
import datasafer.backup.model.Backup;
import datasafer.backup.model.Estacao;
import datasafer.backup.model.Operacao;
import datasafer.backup.model.Permissao;
import datasafer.backup.model.Usuario;
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
	private PermissaoDao permissaoDao;

	@PostConstruct
	private void popula() {

		System.out.println("==== Inicializando ====");

		Usuario usuario_admin = usuarioDao.obtemUsuario("admin");
		if (usuario_admin == null) {
			usuario_admin = new Usuario();

			usuario_admin.setArmazenamento(0L);
			usuario_admin.setLogin("admin");
			usuario_admin.setNome("Administrador");
			usuario_admin.setEmail("admin@admin.com");
			usuario_admin.setSenha("admin");
			usuario_admin.setStatus(Status.ATIVO);

			usuarioDao.insereUsuario(null, null, usuario_admin);
		}

		populaUsuarios(null, usuario_admin);
	}

	public void populaUsuarios(	Usuario solicitante,
								Usuario superior) {

		System.out.println(" ==== POPULA ==== ");

		for (String nome : Arrays.asList(	"Giovanni Campaner",
											"Henrique Francisco da Silva",
											"Sheila Barreto",
											"Fellipe Thufik Costa",
											"Felipe Lemes Discher",
											"Hugo Henrique")) {
			List<String> nomes = Arrays.asList(nome	.toLowerCase()
													.split(" "));
			String login = nomes.get(0);
			for (int i = 1; i < nomes.size(); i++) {
				login += nomes	.get(i)
								.charAt(0);
			}

			Usuario usuario = usuarioDao.obtemUsuario(login);
			if (usuario == null) {
				usuario = new Usuario();
				usuario.setNome(nome);
				usuario.setEmail(login + "@gmail.com");
				usuario.setArmazenamento(10000000L);
				usuario.setLogin(login);
				usuario.setSenha(login);
				usuario.setStatus(Status.ATIVO);

				usuarioDao.insereUsuario(solicitante, superior, usuario);

				permissaoDao.inserirPermissao(usuario, new Permissao(solicitante, superior, null, Permissao.Tipo.VISUALIZAR, true));
				permissaoDao.inserirPermissao(usuario, new Permissao(solicitante, superior, null, Permissao.Tipo.EDITAR, true));
				permissaoDao.inserirPermissao(usuario, new Permissao(solicitante, superior, null, Permissao.Tipo.INSERIR, true));
				permissaoDao.inserirPermissao(usuario, new Permissao(solicitante, superior, null, Permissao.Tipo.REMOVER, true));

				// permissaoDao.inserirPermissao(superior, new
				// Permissao(solicitante, usuario, null,
				// Permissao.Tipo.VISUALIZAR, false));
				// permissaoDao.inserirPermissao(superior, new
				// Permissao(solicitante, usuario, null, Permissao.Tipo.EDITAR,
				// false));
				// permissaoDao.inserirPermissao(superior, new
				// Permissao(solicitante, usuario, null, Permissao.Tipo.INSERIR,
				// false));
				// permissaoDao.inserirPermissao(superior, new
				// Permissao(solicitante, usuario, null, Permissao.Tipo.REMOVER,
				// false));

				populaEstacoes(solicitante, usuario);
			}
		}
	}

	public void populaEstacoes(	Usuario solicitante,
								Usuario gerenciador) {

		List<String> tiposDispositivos = Arrays.asList("PC", "Desktop", "Computador", "Notebook", "Netbook", "Laptop");
		List<String> nomesDispositivos = Arrays.asList("Trabalho", "Escola", "Casa", "Escritório", "Banheiro", "Quarto", "Sala");
		List<String> separadores = Arrays.asList("-", "_", ".", " ");

		Random gerador = new Random();

		for (int n = gerador.nextInt(5) + 5; n > 0; n--) {
			int tipo_index = gerador.nextInt(tiposDispositivos.size());
			int nome_index = gerador.nextInt(nomesDispositivos.size());
			int separador_index = gerador.nextInt(separadores.size());

			String nome_estacao = tiposDispositivos.get(tipo_index) + separadores.get(separador_index) + nomesDispositivos.get(nome_index);

			Estacao estacao = estacaoDao.obtemEstacao(nome_estacao);
			if (estacao == null) {
				estacao = new Estacao();
				estacao.setNome(nome_estacao);
				usuarioDao.insereEstacao(solicitante, gerenciador, estacao);
				populaBackups(solicitante, gerenciador, estacao);
			}
		}
	}

	public void populaBackups(	Usuario solicitante,
								Usuario proprietario,
								Estacao estacao) {

		List<String> nomes_backups = Arrays.asList(	"Meus arquivos",
													"Minhas fotos",
													"Arquivos",
													"Fotos",
													"Imagens",
													"Meus videos",
													"Videos",
													"Midia",
													"Programas",
													"Importante",
													"Coisas importantes",
													"Arquivos importantes",
													"Software",
													"Banco de dados",
													"DB",
													"Apresentações",
													"Planilhas",
													"Planilhas importantes");

		Random gerador = new Random();

		for (int n = gerador.nextInt(5) + 5; n > 0; n--) {
			String nomeBackup = nomes_backups.get(gerador.nextInt(nomes_backups.size()));

			Backup backup = backupDao.obtemBackup(proprietario, estacao, nomeBackup);
			if (backup == null) {
				backup = new Backup();
				backup.setNome(nomeBackup);

				backup.setIntervalo(gerador.nextInt(1000000));

				backup.setInicio(Timestamp.from(LocalDateTime	.now().plusDays(gerador.nextInt(60))
																.toInstant(ZoneOffset.UTC)));
				backup.setPasta("C:\\" + nomeBackup	.toLowerCase()
													.replace(' ', '_'));

				estacaoDao.insereBackup(solicitante, proprietario, estacao, backup);
				populaOperacoes(solicitante, proprietario, estacao, backup);
			}
		}
	}

	public void populaOperacoes(Usuario solicitante,
								Usuario proprietario,
								Estacao estacao,
								Backup backup) {

		Random gerador = new Random();

		for (int n = gerador.nextInt(10) + 5; n > 0; n--) {

			Operacao operacao = new Operacao();
			operacao.setData(Timestamp.from(LocalDateTime	.now().plusDays(gerador.nextInt(365))
															.toInstant(ZoneOffset.UTC)));
			operacao.setStatus(Operacao.Status.values()[gerador.nextInt(Operacao.Status.values().length)]);
			operacao.setTamanho((long) gerador.nextInt(10000000));

			backupDao.insereOperacao(solicitante, backup, operacao);
		}

	}

}
