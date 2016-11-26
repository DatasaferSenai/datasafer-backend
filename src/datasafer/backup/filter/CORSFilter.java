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

import org.springframework.http.HttpMethod;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

@WebFilter(filterName = "CORSFilter")
public class CORSFilter implements Filter {

	@Override
	public void doFilter(	ServletRequest req,
							ServletResponse resp,
							FilterChain chain)
												throws IOException, ServletException {

		HttpServletResponse response = (HttpServletResponse) resp;
		HttpServletRequest request = (HttpServletRequest) req;

		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, Authorization, usuario, estacao, backup, operacao");
		response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
		response.setHeader("Access-Control-Max-Age", "3600");

		if (request.getMethod().equals(HttpMethod.OPTIONS)) {
			response.setHeader("Allow", "GET,POST,PUT,DELETE,OPTIONS");
			return;
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
