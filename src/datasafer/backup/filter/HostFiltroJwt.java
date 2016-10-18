package datasafer.backup.filter;

import java.io.IOException;
import java.util.Set;

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

import datasafer.backup.bo.PrivilegioBo;
import datasafer.backup.bo.UsuarioBo;
import datasafer.backup.model.Privilegio;
import datasafer.backup.model.Usuario;
import datasafer.backup.model.Privilegio.Permissao;

@WebFilter("/Datasafer/gerenciamento/host*")
public class HostFiltroJwt implements Filter {

	@Autowired
	private UsuarioBo usuarioBo;
	@Autowired
	private PrivilegioBo privilegioBo;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		Usuario usuario = usuarioBo.obterUsuario(request.getHeader("usuario"));
		Set<Permissao> permissoes = usuario.getPrivilegio().getPermissoes();

		if (permissoes != null) {
			if (permissoes.contains(Privilegio.Permissao.ADMINISTRADOR)
					| (request.getMethod() == "GET" && permissoes.contains(Privilegio.Permissao.VISUALIZAR_HOSTS))
					| (request.getMethod() == "POST" && permissoes.contains(Privilegio.Permissao.INSERIR_HOSTS))
					| (request.getMethod() == "UPDATE" && permissoes.contains(Privilegio.Permissao.MODIFICAR_HOSTS))
					| (request.getMethod() == "DELETE" && permissoes.contains(Privilegio.Permissao.EXCLUIR_HOSTS))

			) {
				chain.doFilter(req, resp);
			}
		}

		response.sendError(HttpStatus.FORBIDDEN.value(),
				"O usuário não possui permissão para realizar a operação solicitada");
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

	@Override
	public void destroy() {

	}
}
