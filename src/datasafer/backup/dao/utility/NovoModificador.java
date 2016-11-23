package datasafer.backup.dao.utility;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import datasafer.backup.dao.PermissaoDao;
import datasafer.backup.model.Permissao;
import datasafer.backup.model.Registro;
import datasafer.backup.model.Usuario;

@Service
public class NovoModificador {

	@Autowired
	private PermissaoDao permissaoDao;

	public <T> List<Registro> modifica(	Usuario solicitante,
										T destino,
										T origem) throws AccessDeniedException {

		List<Permissao> permissoes = permissaoDao.obtemPermissoesRecebidas(solicitante);

		List<Registro> registros = new ArrayList<Registro>();
		// try {
		// for (Field f : destino.getClass().getDeclaredFields()) {
		//
		// permissoes.stream().filter(p -> p.getAtributo());
		//
		// Object valorDestino = new PropertyDescriptor(f.getName(),
		// destino.getClass()).getReadMethod().invoke(destino);
		//
		// if (origem != null) {
		//
		// Object valorOrigem = new PropertyDescriptor(f.getName(),
		// origem.getClass()).getReadMethod().invoke(origem);
		// if (valorOrigem != null && !valorOrigem.equals(valorDestino))
		// {
		//
		// Registro registro = new Registro();
		// registro.setSolicitante(solicitante);
		// registro.setData(Timestamp.from(LocalDateTime .now()
		// .atZone(ZoneId.systemDefault())
		// .toInstant()));
		//
		// registros.add(registro);
		//
		// new PropertyDescriptor(f.getName(),
		// destino.getClass()).getWriteMethod().invoke(destino,
		// valorOrigem);
		// }
		// }
		// }
		// } catch (IllegalAccessException | IllegalArgumentException |
		// InvocationTargetException | IntrospectionException e) {
		// e.printStackTrace();
		// }
		return registros;

	}

}
