package com.mfsys.DatabaseMonitoring.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.mfsys.DatabaseMonitoring.Entity.Charges;
import com.mfsys.DatabaseMonitoring.Entity.EventEntity;
import com.mfsys.DatabaseMonitoring.Entity.TransactionType;

@Service
public class AppService {
	@Autowired
	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public AppService(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
//---------------------------------RETRIEVE DATA-----------------------------
	public List<Map<String, Object>> getTableData(String dbName, String type) throws SQLException {
		Map<String, Object> response = new HashMap<>();
		List<Map<String, Object>> result = new ArrayList<>();
		String tableName = determineTableName(dbName, type);

		try {
			if (tableName.isEmpty()) {
				response.put("message", "Invalid table name");
				result.add(response);
				return result;
			}
		} catch (Exception e) {
			response.put("message", e.getMessage());
			result.add(response);
			return result;
		}

		try {
			System.out.println(jdbcTemplate.queryForList("SELECT * FROM " + dbName + "." + tableName));
			return jdbcTemplate.queryForList("SELECT * FROM " + dbName + "." + tableName);
		} catch (DataAccessException e) {
			response.put("message", e.getMessage());
			result.add(response);
			return result;
		}
	}

	
//---------------------------------CHECK-TABLE-------------------------------
	private String determineTableName(String dbName, String type) throws SQLException {
		List<String> tableNames = new ArrayList<>();
		if (type.equals("event")) {
			if (dbName.equals("deposit")) {
				tableNames.add("bn_pd_dt_transactionevent");
			} else if (dbName.equals("loan")) {
				tableNames.add("bn_pd_lt_transactionevent");
			} else if (dbName.equals("generalledger")) {
				tableNames.add("bn_pd_gt_transactionevent");
			}
			if (tableNames.isEmpty()) {
				return "Error";
			}
		} else if (type.equals("transaction_type")) {
			if (dbName.equals("deposit")) {
				tableNames.add("pr_gn_dt_deposittransactiontype");
			} else if (dbName.equals("loan")) {
				tableNames.add("pr_gn_lt_loantransactiontype");
			} else if (dbName.equals("generalledger")) {
				tableNames.add("pr_gn_gt_gltransactiontype");
			}
			if (tableNames.isEmpty()) {
				return "Error";
			}
		} else if (type.equals("charges")) {
			if (dbName.equals("deposit")) {
				tableNames.add("pr_gn_ch_charges");
			} else if (dbName.equals("loan")) {
				tableNames.add("pr_gn_ch_charges");
			}
			if (tableNames.isEmpty()) {
				return "table doesnt exist";
			}
		}
		if (!tableNames.get(0).isEmpty()) {
			return (tableNames.get(0));
		}
		return "empty tables";
	}

//	---------------------------------GENERATE-SCRIPT--------------------------------
	public String generateScript(String type, String dbName, Object body) throws SQLException {
		String tableName = determineTableName(dbName, type);
		if (type.equals("event")) {
			EventEntity event = (EventEntity) body;
			String column = "dmp_prodcode";
			String value = event.getDmp_prodcode();
			if (dbName.equals("generalledger")) {
				column = "pcr_currcode";
				value = event.getPcr_currcode();
			}
			StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " (" + column
					+ ", pet_eventcode, por_orgacode, ptr_trancode, pet_eventseqnum, pca_glaccredit, pca_glacdebit)")
					.append(" VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s')");

			String formattedSql = String.format(sql.toString(), value, event.getPet_eventcode(),
					event.getPor_orgacode(), event.getPtr_trancode(), event.getPet_eventseqnum(),
					event.getPca_glaccredit(), event.getPca_glacdebit());

			System.out.println(formattedSql);
			return formattedSql;

		} else if (type.equals("transaction_type")) {
			TransactionType tran_type = (TransactionType) body;
			StringBuilder sql = new StringBuilder("INSERT INTO " + tableName
					+ " (por_orgacode, ptr_trancode, pet_eventcode, ptr_trandesc, system_generated)")
					.append(" VALUES ('%s', '%s', '%s', '%s', '%s')");

			String formattedSql = String.format(sql.toString(), tran_type.getPor_orgacode(),
					tran_type.getPtr_trancode(), tran_type.getPet_eventcode(), tran_type.getPtr_trandesc(),
					tran_type.getSystem_generated());

			System.out.println(formattedSql);
			return formattedSql;

		} else if (type.equals("charges")) {
			Charges charges = (Charges) body;
			if (dbName.equals("deposit")) {
				System.out.println("deposit");
				StringBuilder sql = new StringBuilder("INSERT INTO " + tableName
						+ " (pch_chrgcode, por_orgacode, pch_chrgdesc, pch_chrgshort, pel_elmtcode,ptr_trancode,pch_chrgporfit,soc_charges )")
						.append(" VALUES ('%s', '%s', '%s', '%s', '%s',%s,%s,%s)");

				String formattedSql = String.format(sql.toString(), charges.getPch_chrgcode(),
						charges.getPor_orgacode(), charges.getPch_chrgdesc(), charges.getPch_chrgshort(),
						charges.getPel_elmtcode(), charges.getPtr_trancode(), charges.getPch_chrgprofit(),
						charges.getSoc_charges());

				System.out.println(formattedSql);
				return formattedSql;
			} else if (dbName.equals("loan")) {
				System.out.println("loan");
				StringBuilder sql = new StringBuilder("INSERT INTO " + tableName
						+ " (pch_chrgcode, por_orgacode, pch_chrgdesc, pch_chrgshort, pel_elmtcode, ptr_trancode, pch_chrginterest, pch_chrgpenalty, pch_chrgprincipal, soc_charges)")
						.append(" VALUES ('%s', '%s', '%s', '%s', '%s','%s', '%s', '%s', '%s', '%s')");

				String formattedSql = String.format(sql.toString(), charges.getPch_chrgcode(),
						charges.getPor_orgacode(), charges.getPch_chrgdesc(), charges.getPch_chrgshort(),
						charges.getPel_elmtcode(), charges.getPtr_trancode(), charges.getPch_chrginterest(),
						charges.getPch_chrgpenalty(), charges.getPch_chrgprincipal(), charges.getSoc_charges());

				System.out.println(formattedSql);
				return formattedSql;
			} else {
				return "cant format";
			}
		}

		else
			return "error";

	}

//	---------------------------------UPDATE RECORD_--------------------------------
	public String updateRecord(String type, String dbName, Object body) throws SQLException {
		String tableName = determineTableName(dbName, type);
		if (type.equals("event")) {
			EventEntity event = (EventEntity) body;
			String column = "dmp_prodcode";
			String value = event.getDmp_prodcode();
			String x = event.getPor_orgacode();
			if (dbName.equals("generalledger")) {
				column = "pcr_currcode";
				value = event.getPcr_currcode();
			}
			StringBuilder sql = new StringBuilder("UPDATE " + dbName + "." + tableName + " SET ")
					.append(column + " = '%s', ").append("pet_eventcode = '%s', ").append("por_orgacode = '%s', ")
					.append("ptr_trancode = '%s', ").append("pet_eventseqnum = '%s', ")
					.append("pca_glaccredit = '%s', ").append("pca_glacdebit = '%s' ")
					.append("WHERE por_orgacode = '" + x + "' AND " + column + "= '" + event.getPcr_currcode()
							+ "' AND ptr_trancode='" + event.getPtr_trancode() + "' AND pet_eventcode='"
							+ event.getPet_eventcode() + "'");
			String formattedSql = String.format(sql.toString(), value, event.getPet_eventcode(),
					event.getPor_orgacode(), event.getPtr_trancode(), event.getPet_eventseqnum(),
					event.getPca_glaccredit(), event.getPca_glacdebit());
			System.out.println(formattedSql);
			try {
				int rowsAffected = jdbcTemplate.update(formattedSql);
				return "Query executed successfully. Rows affected: " + rowsAffected;
			} catch (DataAccessException e) {
				Throwable rootCause = e.getRootCause();
				System.out.println(e.getMessage());
				return rootCause.getMessage();
			}
		} else if (type.equals("transaction_type")) {
			TransactionType tran_type = (TransactionType) body;
			StringBuilder sql = new StringBuilder("UPDATE " + dbName + "." + tableName + " SET ")
					.append("por_orgacode = '%s', ").append("ptr_trancode = '%s', ").append("pet_eventcode = '%s', ")
					.append("ptr_trandesc = '%s', ").append("system_generated = '%s' ")
					.append("WHERE por_orgacode = '" + tran_type.getPor_orgacode() + "' AND ptr_trancode = '"
							+ tran_type.getPtr_trancode() + "' AND pet_eventcode='" + tran_type.getPet_eventcode()
							+ "'");
			String formattedSql = String.format(sql.toString(), tran_type.getPor_orgacode(),
					tran_type.getPtr_trancode(), tran_type.getPet_eventcode(), tran_type.getPtr_trandesc(),
					tran_type.getSystem_generated());

			System.out.println(formattedSql);
			try {
				int rowsAffected = jdbcTemplate.update(formattedSql);
				return "Query executed successfully. Rows affected: " + rowsAffected;
			} catch (DataAccessException e) {
				Throwable rootCause = e.getRootCause();
				System.out.println(e.getMessage());
				return rootCause.getMessage();
			}

		} else if (type.equals("charges")) {
			Charges charges = (Charges) body;
			if (dbName.equals("deposit")) {
				StringBuilder sql = new StringBuilder("UPDATE " + dbName + "." + tableName + " SET ")
						.append("pch_chrgcode = '%s', ").append("por_orgacode = '%s', ").append("pch_chrgdesc = '%s', ")
						.append("pch_chrgshort = '%s', ").append("pel_elmtcode = '%s', ").append("ptr_trancode = '%s', ")
						.append("pch_chrgprofit = '%s', ").append("soc_charges = '%s' ")
						.append("WHERE por_orgacode = '" + charges.getPor_orgacode() + "' AND pch_chrgcode = '"
								+ charges.getPch_chrgcode() + "'");

				String formattedSql = String.format(sql.toString(), charges.getPch_chrgcode(),
						charges.getPor_orgacode(), charges.getPch_chrgdesc(), charges.getPch_chrgshort(),
						charges.getPel_elmtcode(), charges.getPtr_trancode(), charges.getPch_chrgprofit(),
						charges.getSoc_charges());

				System.out.println(formattedSql);
				try {
					int rowsAffected = jdbcTemplate.update(formattedSql);
					return "Query executed successfully. Rows affected: " + rowsAffected;
				} catch (DataAccessException e) {
					Throwable rootCause = e.getRootCause();
					System.out.println(e.getMessage());
					return rootCause.getMessage();
				}
				
			} else if (dbName.equals("loan")) {
				StringBuilder sql = new StringBuilder("UPDATE " + dbName + "." + tableName + " SET ")
						.append("pch_chrgcode = '%s', ").append("por_orgacode = '%s', ").append("pch_chrgdesc = '%s', ")
						.append("pch_chrgshort = '%s', ").append("pel_elmtcode = '%s', ").append("ptr_trancode = '%s', ")
						.append("pch_chrginterest = '%s', ").append("pch_chrgpenalty = '%s', ").append("pch_chrgprincipal = '%s', ").append("soc_charges = '%s' ")
						.append("WHERE por_orgacode = '" + charges.getPor_orgacode() + "' AND pch_chrgcode = '"
								+ charges.getPch_chrgcode() + "'");

				String formattedSql = String.format(sql.toString(), charges.getPch_chrgcode(),
						charges.getPor_orgacode(), charges.getPch_chrgdesc(), charges.getPch_chrgshort(),
						charges.getPel_elmtcode(), charges.getPtr_trancode(),charges.getPch_chrginterest(),charges.getPch_chrgpenalty(), charges.getPch_chrgprincipal(),
						charges.getSoc_charges());

				System.out.println(formattedSql);
				try {
					int rowsAffected = jdbcTemplate.update(formattedSql);
					return "Query executed successfully. Rows affected: " + rowsAffected;
				} catch (DataAccessException e) {
					Throwable rootCause = e.getRootCause();
					System.out.println(e.getMessage());
					return rootCause.getMessage();
				}
			} else 
				return "4321";
			
		} else
			return "12345";
	}

//	--------------------------------ADD_COlumns------------------------------------
	public String savecolumns(Object body, String databaseName, String type) throws SQLException {

		String url = "jdbc:mysql://localhost:3306/";
		String username = "root";
		String password = "root";
		Connection connection = DriverManager.getConnection(url, username, password);
		String tableName = determineTableName(databaseName, type);

		if (type.equals("event")) {
			EventEntity event = (EventEntity) body;
			String column = "dmp_prodcode";
			String value = event.getDmp_prodcode();

			if (databaseName.equals("generalledger")) {
				column = "pcr_currcode";
				value = event.getPcr_currcode();

			}
			String sql = "INSERT INTO " + databaseName+"."+ tableName + "(" + column
					+ ",pet_eventcode,ptr_trancode,por_orgacode,pet_eventseqnum,pca_glaccredit,pca_glacdebit) VALUES (?,?,?,?,?,?,?)";
			PreparedStatement statement = connection.prepareStatement(sql);
			Statement disableConstraintsStmt = connection.createStatement();
			disableConstraintsStmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");
			statement.setString(1, value);
			statement.setString(2, event.getPet_eventcode());
			statement.setString(3, event.getPtr_trancode());
			statement.setString(4, event.getPor_orgacode());
			statement.setString(5, event.getPet_eventseqnum());
			statement.setString(6, event.getPca_glaccredit());
			statement.setString(7, event.getPca_glacdebit());
			int rowsAffected = statement.executeUpdate();
			System.out.println(rowsAffected + " :rows effected");
			
			statement.close();
			connection.close();
			return rowsAffected+ " :rows effected";

		} else if (type.equals("charge")) {
			Charges charges = (Charges) body;

			if (databaseName.equals("loan")) {
				String sql = "INSERT INTO " + tableName
						+ "(pch_chrgcode,por_orgacode,pch_chrgdesc,pch_chrgshort,pel_elmtcode,ptr_trancode,pch_chrginterest,pch_chrgpenalty,pch_chrgprincipal,soc_charges) VALUES (?,?,?,?,?,?,?,?,?,?)";
				PreparedStatement statement = connection.prepareStatement(sql);
				Statement disableConstraintsStmt = connection.createStatement();
				disableConstraintsStmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");
				statement.setString(1, charges.getPch_chrgcode());
				statement.setString(2, charges.getPor_orgacode());
				;
				statement.setString(3, charges.getPch_chrgdesc());
				statement.setString(4, charges.getPch_chrgshort());
				statement.setString(5, charges.getPel_elmtcode());
				statement.setString(6, charges.getPtr_trancode());
				statement.setString(7, charges.getPch_chrginterest());
				statement.setString(8, charges.getPch_chrgpenalty());
				statement.setString(9, charges.getPch_chrgprincipal());
				statement.setString(10, charges.getSoc_charges());
				int rowsAffected = statement.executeUpdate();
				System.out.println(rowsAffected + " :rows effected");
				statement.close();
				connection.close();
				return rowsAffected+ " :rows effected";
			} else if (databaseName.equals("deposit")) {
				String sql = "INSERT INTO " + tableName
						+ "(pch_chrgcode,por_orgacode,pch_chrgdesc,pch_chrgshort,pel_elmtcode,ptr_trancode,pch_chrgprofit,soc_charges) VALUES (?,?,?,?,?,?,?,?)";
				PreparedStatement statement = connection.prepareStatement(sql);
				Statement disableConstraintsStmt = connection.createStatement();
				disableConstraintsStmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");
				statement.setString(1, charges.getPch_chrgcode());
				statement.setString(2, charges.getPor_orgacode());
				;
				statement.setString(3, charges.getPch_chrgdesc());
				statement.setString(4, charges.getPch_chrgshort());
				statement.setString(5, charges.getPel_elmtcode());
				statement.setString(6, charges.getPtr_trancode());
				statement.setString(7, charges.getPch_chrgprofit());
				statement.setString(8, charges.getSoc_charges());
				int rowsAffected = statement.executeUpdate();
				System.out.println(rowsAffected + " :rows effected");
				statement.close();
				connection.close();
				return rowsAffected+ " :rows effected";
			} else {
				System.out.println("There is not charges table for generalladger");
				return "There is not charges table for generalladger";
			}
		} else if (type.equals("transaction")) {
			TransactionType transEntity = (TransactionType) body;
			System.out.println(transEntity);
			String sql = "INSERT INTO " + determineTableName(databaseName, type)
					+ "(por_orgacode,ptr_trancode,pet_eventcode,ptr_trandesc,system_generated) VALUES (?,?,?,?,?)";
			PreparedStatement statement = connection.prepareStatement(sql);
			Statement disableConstraintsStmt = connection.createStatement();
			disableConstraintsStmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");
			statement.setString(1, transEntity.getPor_orgacode());
			statement.setString(2, transEntity.getPtr_trancode());
//			statement.setInt(3, transEntity.getPet_eventcode());
			statement.setString(3, transEntity.getPet_eventcode());
//			----------------------------------------------
			statement.setString(4, transEntity.getPtr_trandesc());
//			statement.setInt(5, transEntity.getSystem_generated());
			statement.setString(5, transEntity.getSystem_generated());
// 			-----------------------------------------
			int rowsAffected = statement.executeUpdate();
			System.out.println(rowsAffected + " :rows effected in Database: " + databaseName);
			statement.close();
			connection.close();
			return rowsAffected+ " :rows effected";
		}
		return "Error";
	}

	public void deleteRow(Object body, String databaseName, String type) throws SQLException {
		String url = "jdbc:mysql://localhost:3306/";
		String username = "root";
		String password = "root";
		Connection connection = DriverManager.getConnection(url, username, password);

		if (type.equals("event")) {
			EventEntity event = (EventEntity) body;
			String column = "dmp_prodcode";
			String value = event.getDmp_prodcode();

			if (databaseName.equals("generalledger")) {
				column = "pcr_currcode";
				value = event.getPcr_currcode();

			}
			String sql = "DELETE FROM " + determineTableName(databaseName, type) + " WHERE " + column + " = ? AND "
					+ "por_orgacode = ? AND " + "pet_eventcode = ? AND " + "ptr_trancode = ?";

			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, value);
			statement.setString(2, event.getPor_orgacode());
//			statement.setInt(3, event.getPet_eventcode());
			statement.setString(3, event.getPet_eventcode());
//			----------------------------------------------
			statement.setString(4, event.getPtr_trancode());
			int rowsAffected = statement.executeUpdate();
			System.out.println(rowsAffected + " :rows deleted");
			statement.close();
			connection.close();
		}else if(type.equals("charge")) {
			Charges charges = (Charges) body;
			String sql = "DELETE FROM " + determineTableName(databaseName, type) + " WHERE "
					+ "pch_chrgcode = ? AND " + "por_orgacode = ?";
			PreparedStatement statement = connection.prepareStatement(sql);
			Statement disableConstraintsStmt = connection.createStatement();
			disableConstraintsStmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");
			statement.setString(1, charges.getPch_chrgcode());
			statement.setString(2, charges.getPor_orgacode());
			int rowsAffected = statement.executeUpdate();
			System.out.println(rowsAffected + " :rows deleted");
			statement.close();
			connection.close();
		}else if(type.equals("transaction")) {
			TransactionType transaction = (TransactionType) body;
			String sql = "DELETE FROM " + determineTableName(databaseName, type) + " WHERE "
					+ "ptr_trancode = ? AND " + "por_orgacode = ?";
			PreparedStatement statement = connection.prepareStatement(sql);
			Statement disableConstraintsStmt = connection.createStatement();
			disableConstraintsStmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");
			statement.setString(1, transaction.getPtr_trancode());
			statement.setString(2, transaction.getPor_orgacode());
			int rowsAffected = statement.executeUpdate();
			System.out.println(rowsAffected + " :rows deleted");
			statement.close();
			connection.close();
		}
		
	}
}
