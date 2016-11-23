package datasafer.backup.filter;

import java.io.IOException;
import java.util.List;

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

import datasafer.backup.dao.PermissaoDao;
import datasafer.backup.model.Permissao;
import datasafer.backup.model.Usuario;

@WebFilter(filterName = "PermissaoFilter")
public class PermissaoFilter implements Filter {

	@Autowired
	PermissaoDao permissaoDao;

	@Override
	public void doFilter(	ServletRequest req,
							ServletResponse resp,
							FilterChain chain)
												throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		if (request	.getRequestURI()
					.contains("login")) {

			chain.doFilter(req, resp);
			return;
		}

		try {

			final Usuario solicitante = (Usuario) request.getAttribute("solicitante");
			final Usuario usuario = (Usuario) request.getAttribute("usuario");
			
			final List<Permissao> permissoes = permissaoDao.obtemPermissoesRecebidas(solicitante);
			
			System.out.println("+++ INICIO PERMISSOES +++ ");
			for(Permissao p : permissoes){
				System.out.println(p.getAtributo());
			}
			System.out.println("+++ FIM PERMISSOES +++ ");
			
			chain.doFilter(request, response);

		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}

	}

	@Override
	public void destroy() {}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, filterConfig.getServletContext());
	}

}
