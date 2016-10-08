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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import datasafer.backup.dao.BackupDao;
import datasafer.backup.model.Backup;

@RestController
public class BackupRestController {

	@Autowired
	private BackupDao backupDao;
	
	/*@RequestMapping(value = "/backup", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Backup> inserir(@RequestBody String strBackup) {
		try {
			JSONObject job = new JSONObject(strBackup);
			Backup backup = new Backup();
			backup.setTitulo(job.getString("titulo"));

			List<ItemBackup> itens = new ArrayList<>();
			JSONArray arrayItens = job.getJSONArray("itens");
			for (int i = 0; i < arrayItens.length(); i++) {
				ItemBackup item = new ItemBackup();
				item.setDescricao(arrayItens.getString(i));
				item.setBackup(backup);
				itens.add(item);
			}
			backup.setItens(itens);
			backupDao.inserir(backup);
			URI location = new URI("/backup/" + backup.getId());

			return ResponseEntity.created(location).body(backup);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}*/
	
	/*@RequestMapping(value = "/backup", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Backup> inserir(@RequestBody Backup backup) {
		try {
			backupDao.inserir(backup);
			URI location = new URI("/backup/" + backup.getId());
			
			return ResponseEntity.created(location).body(backup);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}*/
	
	
}
