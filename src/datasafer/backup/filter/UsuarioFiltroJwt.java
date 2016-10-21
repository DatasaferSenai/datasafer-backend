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
import datasafer.backup.model.Privilegio.Permissao;
import datasafer.backup.model.Usuario;

@WebFilter("/Datasafer/gerenciamento/usuario*")
public class UsuarioFiltroJwt implements Filter {

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
		Set<Permissao> permissoes = usuario.getPrivilegio().getPermissoes();

		if (permissoes != null) {
			if (usuario.getPrivilegio().getPermissoes().contains(Permissao.ADMINISTRADOR)
					| (request.getMethod() == "GET" && permissoes.contains(Permissao.VISUALIZAR_USUARIOS))
					| (request.getMethod() == "POST" && permissoes.contains(Permissao.INSERIR_USUARIOS))
					| (request.getMethod() == "PUT" && permissoes.contains(Permissao.MODIFICAR_USUARIOS))
					| (request.getMethod() == "DELETE" && permissoes.contains(Permissao.EXCLUIR_USUARIOS))

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

}
