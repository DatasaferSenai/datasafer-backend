package datasafer.backup.controller.utlility;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import datasafer.backup.model.Notificacao;

public class NotificadorOneSignal {

	private static final RestTemplate restTemplate = new RestTemplate();

	private static final String APP_URL = "https://onesignal.com/api/v1/notifications";
	private static final String APP_ID = "52930b86-23de-444d-932e-08a58573327c";
	private static final String APP_KEY = "OTdhMDQyYjMtOTY3Ny00ZDI5LTlmNDctNWQ0MmQ2MzkxNzMx";

	public static void envia(	List<Notificacao> notificacoes,
								String mensagem) {

		try {
			if (!notificacoes.isEmpty()) {

				/* ResponseEntity<String> response = */restTemplate.postForEntity(	new URI(APP_URL),
																					RequestEntity	.method(HttpMethod.POST, new URI(APP_URL))
																									.header("Authorization", "Basic " + APP_KEY)
																									.contentType(MediaType.APPLICATION_JSON_UTF8)
																									.body(new JSONObject()	.put(					"app_id",
																																					APP_ID)
																															.put(	"contents",
																																	new Object() {
																																		String en = mensagem;
																																	})
																															.put(	"include_ios_tokens",
																																	notificacoes.stream()
																																				.filter(n -> n	.getTipo()
																																								.equals(Notificacao.Tipo.DISPOSITIVO_IOS))
																																				.collect(Collectors.toList()))
																															.toString(1)),
																					String.class);

				/* ResponseEntity<String> response = */restTemplate.postForEntity(	new URI(APP_URL),
																					RequestEntity	.method(HttpMethod.POST, new URI(APP_URL))
																									.header("Authorization", "Basic " + APP_KEY)
																									.contentType(MediaType.APPLICATION_JSON_UTF8)
																									.body(new JSONObject()	.put(					"app_id",
																																					APP_ID)
																															.put(	"contents",
																																	new Object() {
																																		String en = mensagem;
																																	})
																															.put(	"include_player_ids",
																																	notificacoes.stream()
																																				.filter(n -> !n	.getTipo()
																																								.equals(Notificacao.Tipo.DISPOSITIVO_IOS))
																																				.collect(Collectors.toList()))
																															.toString(1)),
																					String.class);

			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

	}

}
