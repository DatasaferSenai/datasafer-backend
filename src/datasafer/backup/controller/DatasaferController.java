package datasafer.backup.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import datasafer.backup.dao.DatasaferDao;
import datasafer.backup.modelo.Backup;

@RestController
public class DatasaferController {

	@Autowired
	private DatasaferDao datasaferDao;

	@Transactional
	@RequestMapping(value = "/adicionarBackup", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Backup> inserir(@RequestBody String strLista) {
		try {
			JSONObject job = new JSONObject(strLista);
			Backup backup = new Backup();
			System.out.println(job.getString("titulo"));
			/*backup.setTitulo(job.getString("titulo"));

			List<ItemLista> itens = new ArrayList<>();
			JSONArray arrayItens = job.getJSONArray("itens");
			for (int i = 0; i < arrayItens.length(); i++) {
				ItemLista item = new ItemLista();
				item.setDescricao(arrayItens.getString(i));
				item.setLista(backup);
				itens.add(item);
			}
			backup.setItens(itens);
			listaDao.inserir(backup);
			*/
			URI location = new URI("/ok");
			
			return ResponseEntity.created(location).body(backup);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
