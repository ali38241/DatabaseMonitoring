package com.mfsys.DatabaseMonitoring.app;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
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
	public List<Map<String, Object>> getAllData(@PathVariable String dbName, @PathVariable String type)
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
	@PostMapping("/addcolumns/{body}/{databaseName}/{type}")
	public String savecolumns(@RequestBody String requstBody, @PathVariable String databaseName, @PathVariable String type)throws SQLException{
		Object body = deserializeBody(type, requstBody);
		return appService.savecolumns(body,databaseName,type);
	}
//	-----------------------------------------DELETE-RECORD---------------------------------
	@GetMapping("/delete/{body}/{databaseName}/{type}")
	public void deleteRow(@RequestBody String requestbody, @PathVariable String databaseName, @PathVariable String type) throws SQLException {
		Object body = deserializeBody(type, requestbody);
		appService.deleteRow(body,databaseName,type);
	}	
	
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
			// Handle exception, e.g., log or throw custom exception
		}

		return null; // Return null or handle invalid type case appropriately
	}

	

}
