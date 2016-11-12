package datasafer.backup.dao.helper;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import datasafer.backup.model.Registro;
import datasafer.backup.model.Usuario;

public class Registrador {

	public static <T> List<Registro> inserir(	Usuario solicitante,
												T objeto) {

		List<Registro> registros = new ArrayList<Registro>();
		for (Field atributo : objeto.getClass()
									.getDeclaredFields()) {
			try {
				Object valor = new PropertyDescriptor(atributo.getName(), objeto.getClass()).getReadMethod()
																							.invoke(objeto);

				if (valor != null) {
					Registro registro = new Registro();
					registro.setSolicitante(solicitante);
					registro.setData(Date.from(LocalDateTime.now()
															.atZone(ZoneId.systemDefault())
															.toInstant()));
					registro.setAtributo(atributo.getName());
					registro.setDe(null);
					registro.setPara(valor.getClass() == Date.class ? new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(valor) : valor.toString());
					registros.add(registro);

				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | IntrospectionException e) {
				e.printStackTrace();
				continue;
			}
		}
		return registros;
	}

	public static <T> List<Registro> modificar(	Usuario solicitante,
												T destino,
												T origem) {

		List<Registro> registros = new ArrayList<Registro>();
		for (Field atributo : destino	.getClass()
										.getDeclaredFields()) {
			try {
				Method lerOrigem = new PropertyDescriptor(atributo.getName(), origem.getClass()).getReadMethod();
				Method lerDestino = new PropertyDescriptor(atributo.getName(), destino.getClass()).getReadMethod();
				Method gravarDestino = new PropertyDescriptor(atributo.getName(), destino.getClass()).getWriteMethod();

				Object valorOrigem = lerOrigem.invoke(origem);
				Object valorDestino = lerDestino.invoke(destino);

				if (valorOrigem != null && !valorOrigem.equals(valorDestino)) {

					Registro registro = new Registro();
					registro.setSolicitante(solicitante);
					registro.setData(Date.from(LocalDateTime.now()
															.atZone(ZoneId.systemDefault())
															.toInstant()));
					registro.setAtributo(atributo.getName());
					if (valorDestino != null) {
						registro.setDe(valorDestino.getClass() == Date.class ? new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(valorDestino)
								: valorDestino.toString());
					} else {
						registro.setDe(null);
					}
					registro.setPara(
							valorOrigem.getClass() == Date.class ? new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(valorOrigem) : valorOrigem.toString());
					registros.add(registro);

					System.out.println("De: " + registro.getDe());
					System.out.println("Para: " + registro.getPara());
					gravarDestino.invoke(destino, valorOrigem);
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | IntrospectionException e) {
				e.printStackTrace();
				continue;
			}
		}

		return registros;
	}

}
