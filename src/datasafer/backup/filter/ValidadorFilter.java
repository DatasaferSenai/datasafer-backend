package datasafer.backup.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import datasafer.backup.dao.BackupDao;
import datasafer.backup.dao.EstacaoDao;
import datasafer.backup.model.Backup;
import datasafer.backup.model.Estacao;
import datasafer.backup.model.Usuario;

@WebFilter(filterName = "ValidadorFilter")
public class ValidadorFilter implements Filter {

	@Autowired
	EstacaoDao estacaoDao;
	@Autowired
	BackupDao backupDao;

	@Override
	public void doFilter(	ServletRequest req,
							ServletResponse resp,
							FilterChain chain)
												throws IOException, ServletException, DataIntegrityViolationException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		if (request	.getRequestURI()
					.contains("login")) {

			chain.doFilter(req, resp);
			return;
		}

		Usuario usuario = (Usuario) request.getAttribute("usuario");

		Estacao estacao = null;
		if ((request.getRequestURI()
					.contains("operacao")
				&& !request	.getRequestURI()
							.contains("operacoes"))
				|| (request	.getRequestURI()
							.contains("backup")
						&& !request	.getRequestURI()
									.contains("backups"))
				|| (request	.getRequestURI()
							.contains("estacao")
						&& !request	.getRequestURI()
									.contains("estacoes"))) {

			if (usuario == null) {
				response.sendError(HttpStatus.BAD_REQUEST.value(), "Usuário não espeficado");
				return;
			}

			if (request.getHeader("estacao") != null) {
				estacao = estacaoDao.obtemEstacao((String) request.getHeader("estacao"));
				estacao.setGerenciador(usuario);
			}
			if (estacao == null) {
				response.sendError(HttpStatus.NOT_FOUND.value(), "Estação inválida ou não encontrada");
				return;
			}

			request.setAttribute("estacao", estacao);
		}

		Backup backup = null;
		if ((request.getRequestURI()
					.contains("operacao")
				&& !request	.getRequestURI()
							.contains("operacoes"))
				|| (request	.getRequestURI()
							.contains("backup")
						&& !request	.getRequestURI()
									.contains("backups"))) {

			if (estacao == null) {
				response.sendError(HttpStatus.BAD_REQUEST.value(), "Estação não espeficada");
				return;
			}

			if (request.getHeader("backup") != null) {
				backup = backupDao.obtemBackup(usuario, estacao, (String) request.getHeader("backup"));
				backup.setProprietario(usuario);
			}
			if (backup == null) {
				response.sendError(HttpStatus.NOT_FOUND.value(), "Backup inválido ou não encontrado");
				return;
			}

			request.setAttribute("backup", backup);

		}

		if ((request.getRequestURI()
					.contains("operacao")
				&& !request	.getRequestURI()
							.contains("operacoes"))) {

			if (backup == null) {
				response.sendError(HttpStatus.BAD_REQUEST.value(), "Backup não espeficado");
				return;
			}

		}

		chain.doFilter(req, resp);

	}

	@Override
	public void destroy() {}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, filterConfig.getServletContext());
	}

}
