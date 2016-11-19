package datasafer.backup.controller.helper;

import java.net.URI;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import datasafer.backup.model.Notificacao;

public class Notificador {

	private static final RestTemplate restTemplate = new RestTemplate();

	private static final String APP_URL = "https://onesignal.com/api/v1/notifications";
	private static final String APP_ID = "52930b86-23de-444d-932e-08a58573327c";
	private static final String APP_KEY = "OTdhMDQyYjMtOTY3Ny00ZDI5LTlmNDctNWQ0MmQ2MzkxNzMx";

	public static void envia(	List<Notificacao> notificacoes,
								String mensagem) {

		try {
			if (!notificacoes.isEmpty()) {
				JSONObject job = new JSONObject();
				job.put("app_id", APP_ID);

				JSONObject content = new JSONObject().put("en", mensagem);
				job.put("contents", content);

				JSONArray tokens = new JSONArray();
				for (Notificacao n : notificacoes) {
					tokens.put(n.getToken());
				}
				job.put("include_ios_tokens", tokens);

				RequestEntity<String> request = RequestEntity	.method(HttpMethod.POST, new URI(APP_URL))
																.header("Authorization", "Basic " + APP_KEY)
																.contentType(MediaType.APPLICATION_JSON_UTF8)
																.body(job.toString(1));

				ResponseEntity<String> response = restTemplate.postForEntity(request.getUrl(), request, String.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

	}

}
