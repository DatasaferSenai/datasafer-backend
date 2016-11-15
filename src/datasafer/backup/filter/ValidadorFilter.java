package datasafer.backup.filter;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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
import org.springframework.http.HttpStatus;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import datasafer.backup.dao.BackupDao;
import datasafer.backup.dao.EstacaoDao;
import datasafer.backup.dao.OperacaoDao;
import datasafer.backup.model.Backup;
import datasafer.backup.model.Estacao;
import datasafer.backup.model.Operacao;
import datasafer.backup.model.Usuario;

@WebFilter(filterName = "ValidadorFilter")
public class ValidadorFilter implements Filter {

	@Autowired
	EstacaoDao estacaoDao;
	@Autowired
	BackupDao backupDao;
	@Autowired
	OperacaoDao operacaoDao;

	@Override
	public void doFilter(	ServletRequest req,
							ServletResponse resp,
							FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		Usuario usuario = (Usuario) request.getAttribute("usuario");

		if (request	.getRequestURI()
					.contains("estacao")) {
			Estacao estacao = null;
			if (request.getHeader("estacao") != null) {
				estacao = estacaoDao.obter((String) request.getHeader("estacao"));
			}
			if (estacao == null) {
				response.sendError(HttpStatus.NOT_FOUND.value(), "Estação inválida ou não encontrada");
				return;
			}

			request.setAttribute("estacao", estacao);

			if (request	.getRequestURI()
						.contains("backup")) {
				Backup backup = null;
				if (request.getHeader("backup") != null) {
					backup = backupDao.obter(usuario, estacao, (String) request.getHeader("backup"));
				}
				if (backup == null) {
					response.sendError(HttpStatus.NOT_FOUND.value(), "Backup inválido ou não encontrado");
					return;
				}

				request.setAttribute("backup", backup);

				if (request	.getRequestURI()
							.contains("operacao")) {
					Operacao operacao = null;
					if (request.getHeader("operacao") != null) {
						try {
							operacao = operacaoDao.obter(backup, new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse((String) request.getHeader("operacao")));
						} catch (ParseException e) {
							operacao = null;
						}
					}
					if (operacao == null) {
						response.sendError(HttpStatus.NOT_FOUND.value(), "Operacao inválida ou não encontrada");
						return;
					}

					request.setAttribute("operacao", operacao);
				}
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
