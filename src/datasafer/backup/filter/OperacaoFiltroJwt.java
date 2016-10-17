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
import org.springframework.http.HttpStatus;

import datasafer.backup.bo.PrivilegioBo;
import datasafer.backup.bo.UsuarioBo;
import datasafer.backup.model.Privilegio;
import datasafer.backup.model.Usuario;

@WebFilter("/Datasafer/gerenciamento/operacao*")
public class OperacaoFiltroJwt implements Filter {

	@Autowired
	private UsuarioBo usuarioBo;
	@Autowired
	private PrivilegioBo privilegioBo;

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		Usuario usuario = usuarioBo.obterUsuario(request.getHeader("usuario"));
		Privilegio privilegio = privilegioBo.obterPrivilegio(usuario.getPrivilegio());

		if (!privilegio.getPermissoes().contains(Privilegio.Permissao.ADMINISTRADOR)) {
			if ((request.getMethod() == "GET"
					&& !privilegio.getPermissoes().contains(Privilegio.Permissao.VISUALIZAR_OPERACOES))
					| (request.getMethod() == "POST"
							&& !privilegio.getPermissoes().contains(Privilegio.Permissao.INSERIR_OPERACOES))
					| (request.getMethod() == "UPDATE"
							&& !privilegio.getPermissoes().contains(Privilegio.Permissao.MODIFICAR_OPERACOES))
					| (request.getMethod() == "DELETE"
							&& !privilegio.getPermissoes().contains(Privilegio.Permissao.EXCLUIR_OPERACOES))

			) {
				response.sendError(HttpStatus.FORBIDDEN.value());
			}
		}

		chain.doFilter(req, resp);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

}
