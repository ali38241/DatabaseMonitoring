package com.mfsys.DatabaseMonitoring.app;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AppRepository {
	private final JdbcTemplate temp;
	@Autowired
    public AppRepository(JdbcTemplate temp) {
        this.temp = temp;
    }
	
	
	public List<Map<String, Object>> getTableData(String tableName){
		String sql = "Select * FROM " + tableName;
		return temp.queryForList(sql);
		
	}
}
