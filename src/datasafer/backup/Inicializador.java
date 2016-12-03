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

import datasafer.backup.dao.PermissaoDao;
import datasafer.backup.dao.utility.Carregador;
import datasafer.backup.dao.utility.Modificador;
import datasafer.backup.model.Backup;
import datasafer.backup.model.Estacao;
import datasafer.backup.model.Operacao;
import datasafer.backup.model.Permissao;
import datasafer.backup.model.Usuario;

@Service
public class Inicializador {

	@Autowired
	private Modificador modificador;
	@Autowired
	private Carregador carregador;

	@Autowired
	private PermissaoDao permissaoDao;

	@PostConstruct
	private void popula() throws NoSuchFieldException {

		System.out.println("==== Inicializando ====");
		Usuario usuario_admin = carregador.obtemEntidade(Usuario.class, "login", "admin");
		if (usuario_admin == null) {
			usuario_admin = new Usuario();

			usuario_admin.setArmazenamento(0L);
			usuario_admin.setLogin("admin");
			usuario_admin.setNome("Administrador");
			usuario_admin.setEmail("admin@admin.com");
			usuario_admin.setSenha("admin");
			usuario_admin.setAtivo(true);

			try {
				modificador.insere(null, null, null, "usuarios", usuario_admin);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		populaUsuarios(null, usuario_admin);
	}

	public void populaUsuarios(	Usuario solicitante,
								Usuario superior) throws NoSuchFieldException {

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

			Usuario usuario = carregador.obtemEntidade(Usuario.class, "login", login);
			if (usuario == null) {
				usuario = new Usuario();
				usuario.setNome(nome);
				usuario.setEmail(login + "@gmail.com");
				usuario.setArmazenamento(10000000L);
				usuario.setLogin(login);
				usuario.setSenha(login);
				usuario.setAtivo(true);

				usuario.setProprietario(superior);

				try {
					modificador.insere(solicitante, superior, superior, "usuarios", usuario);

					permissaoDao.inserirPermissao(usuario, new Permissao(solicitante, superior, null, Permissao.Tipo.VISUALIZAR, true));
					permissaoDao.inserirPermissao(usuario, new Permissao(solicitante, superior, null, Permissao.Tipo.EDITAR, true));
					permissaoDao.inserirPermissao(usuario, new Permissao(solicitante, superior, null, Permissao.Tipo.INSERIR, true));
					permissaoDao.inserirPermissao(usuario, new Permissao(solicitante, superior, null, Permissao.Tipo.REMOVER, true));
				} catch (Exception e) {
					e.printStackTrace();
				}
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
								Usuario gerenciador) throws NoSuchFieldException {

		List<String> tiposDispositivos = Arrays.asList("PC", "Desktop", "Computador", "Notebook", "Netbook", "Laptop");
		List<String> nomesDispositivos = Arrays.asList("Trabalho", "Escola", "Casa", "Escritório", "Banheiro", "Quarto", "Sala");
		List<String> separadores = Arrays.asList("-", "_", ".", " ");

		Random gerador = new Random();

		for (int n = gerador.nextInt(5) + 5; n > 0; n--) {
			int tipo_index = gerador.nextInt(tiposDispositivos.size());
			int nome_index = gerador.nextInt(nomesDispositivos.size());
			int separador_index = gerador.nextInt(separadores.size());

			String nome_estacao = tiposDispositivos.get(tipo_index) + separadores.get(separador_index) + nomesDispositivos.get(nome_index);

			Estacao estacao = carregador.obtemEntidade(Estacao.class, "nome", nome_estacao);
			if (estacao == null) {
				estacao = new Estacao();
				estacao.setNome(nome_estacao);

				try {
					modificador.insere(solicitante, gerenciador, gerenciador, "estacoes", estacao);
				} catch (Exception e) {
					e.printStackTrace();
				}

				populaBackups(solicitante, gerenciador, estacao);
			}
		}
	}

	public void populaBackups(	Usuario solicitante,
								Usuario proprietario,
								Estacao estacao) throws NoSuchFieldException {

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

			Backup backup = carregador.obtemEntidade(Backup.class, "proprietario", proprietario, "estacao", estacao, "nome", nomeBackup);
			if (backup == null) {
				backup = new Backup();
				backup.setNome(nomeBackup);

				backup.setIntervalo(new Long(gerador.nextInt(1000000)));

				backup.setInicio(Timestamp.from(LocalDateTime	.now().plusDays(gerador.nextInt(60))
																.toInstant(ZoneOffset.UTC)));
				backup.setPasta("C:\\" + nomeBackup	.toLowerCase()
													.replace(' ', '_'));

				try {
					modificador.insere(solicitante, proprietario, estacao, "backups", backup);
				} catch (Exception e) {
					e.printStackTrace();
				}

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

			try {
				modificador.insere(solicitante, null, backup, "operacoes", operacao);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
