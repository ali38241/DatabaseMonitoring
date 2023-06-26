package com.mfsys.DatabaseMonitoring.app;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mfsys.DatabaseMonitoring.Entity.Charges;
import com.mfsys.DatabaseMonitoring.Entity.EventEntity;
import com.mfsys.DatabaseMonitoring.Entity.TransactionType;



@CrossOrigin(exposedHeaders="Access-Control-Allow-Origin")
@RequestMapping("/")
@RestController
public class AppController {

	@Autowired
	private AppService appService;

	@GetMapping("/getData/{type}/{dbName}")
	public List<Map<String, Object>> getData(@PathVariable String dbName, @PathVariable String type)
			throws SQLException {
		List<Map<String, Object>> data = appService.getTableData(dbName, type);
		return data;
	}

//	----------------------------------------GENERATE-SCRIPT---------------------------------

	@PostMapping("/generateScript/{type}/{dbName}/{requestBody}")
	public String generateScript(@PathVariable String type, @PathVariable String dbName,@RequestBody String requestBody) throws SQLException {
		Object body = deserializeBody(type, requestBody);
		return appService.generateScript(type, dbName, body);

	}
	
//	-----------------------------------------UPDATE-RECORD--------------------------------
	@PutMapping("/update/{type}/{dbName}/{requestBody}")
	public String updateRecord(@PathVariable String type, @PathVariable String dbName,@RequestBody String requestBody) throws SQLException {
		Object body = deserializeBody(type, requestBody);
		return appService.updateRecord(type, dbName, body);
	}
	
//	-----------------------------------------ADD-RECORD---------------------------------
	@PostMapping("/addcolumns/{dbName}/{type}/{requestBody}")
	public String savecolumns(@RequestBody String requestBody, @PathVariable String dbName, @PathVariable String type)throws SQLException{
		Object body = deserializeBody(type, requestBody);
		return appService.savecolumns(body,dbName,type);
	}
//	-----------------------------------------DELETE-RECORD---------------------------------
	@DeleteMapping("/deleteAll/{type}/{dbName}/{requestBodies}")
    public void deleteRow(@RequestBody List<String> requestBodies,
                          @PathVariable String type,
                          @PathVariable String dbName) {
        List<Object> myObjects = deserialize(requestBodies, type);
        appService.deleteRows(myObjects, type, dbName);
    }
	
	
	@DeleteMapping("/delete/{dbName}/{type}/{requestBody}")
	public void deleteRow(@RequestBody String requestBody, @PathVariable String dbName, @PathVariable String type) throws SQLException {
		Object body = deserializeBody(type, requestBody);
		appService.deleteRow(body,dbName,type);
	}	
	
	 
//	------------------------------------------View ALL-------------------------------------
	
	@GetMapping("/getData/{type}/all")
	public List<Map<String, Object>> getAllData(@PathVariable String type){
	
		return appService.getAllData(type);
	}
	
	@GetMapping("/backup/{type}")
	 public String backupDatabase(@PathVariable String type) throws IOException {
		return appService.backup(type);
	}
	
	
	
	
	
	
	
	
//	-------------------------------------Deserialising--------------------------
	private Object deserializeBody(String type, String requestBody) {
		ObjectMapper objectMapper = new ObjectMapper();

		try {
			if (type.equals("event")) {
				return objectMapper.readValue(requestBody, EventEntity.class);
			} else if (type.equals("transaction_type")) {
				return objectMapper.readValue(requestBody, TransactionType.class);
			} else if (type.equals("charges")) {
				return objectMapper.readValue(requestBody, Charges.class);
			}
		} catch (JsonProcessingException e) {
			System.err.println("error deserializing");
			System.out.println(e.getLocalizedMessage());
		}

		return null;
	}

	private List<Object> deserialize(List<String> requestBodies, String type) {
        ObjectMapper objectMapper = new ObjectMapper();

        List<Object> deserializedObjects = new ArrayList<>();

        for (String requestBody : requestBodies) {
            try {
                if (type.equals("event")) {
                    deserializedObjects.add(objectMapper.readValue(requestBody, EventEntity.class));
                } else if (type.equals("transaction_type")) {
                    deserializedObjects.add(objectMapper.readValue(requestBody, TransactionType.class));
                } else if (type.equals("charges")) {
                    deserializedObjects.add(objectMapper.readValue(requestBody, Charges.class));
                }
            } catch (IOException e) {
                // Handle the exception according to your needs
                e.printStackTrace();
            }
        }

        return deserializedObjects;


    }
}
	
	


