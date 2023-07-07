package com.mfsys.DatabaseMonitoring.app;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.mfsys.DatabaseMonitoring.Entity.Charges;
import com.mfsys.DatabaseMonitoring.Entity.EventEntity;
import com.mfsys.DatabaseMonitoring.Entity.TransactionType;

import io.micrometer.observation.Observation.Event;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

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
			System.out.println(jdbcTemplate.queryForList("SELECT * FROM " + tableName));
			return jdbcTemplate.queryForList("SELECT * FROM " + tableName);
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
				tableNames.add("deposit.pr_gn_de_depositevents");
			} else if (dbName.equals("loan")) {
				tableNames.add("loan.pr_gn_le_loanevents");
			} else if (dbName.equals("generalledger")) {
				tableNames.add("generalledger.pr_gn_ge_glevents");
			}
			if (tableNames.isEmpty()) {
				return "Error";
			}
		} else if (type.equals("transaction_type")) {
			if (dbName.equals("deposit")) {
				tableNames.add("deposit.pr_gn_dt_deposittransactiontype");
			} else if (dbName.equals("loan")) {
				tableNames.add("loan.pr_gn_lt_loantransactiontype");
			} else if (dbName.equals("generalledger")) {
				tableNames.add("generalledger.pr_gn_gt_gltransactiontype");
			}
			if (tableNames.isEmpty()) {
				return "Error";
			}
		} else if (type.equals("charges")) {
			if (dbName.equals("deposit")) {
				tableNames.add("deposit.pr_gn_ch_charges");
			} else if (dbName.equals("loan")) {
				tableNames.add("loan.pr_gn_ch_charges");
			}
			if (tableNames.isEmpty()) {
				return "table doesnt exist";
			}
		}
		if (!tableNames.isEmpty()) {
			return (tableNames.get(0));
		}
		return "empty tables";
	}

//	---------------------------------GENERATE-SCRIPT--------------------------------

	public String generateScript(String type, String dbName, Object body) throws SQLException {
		String tableName = determineTableName(dbName, type);
		if (type.equals("event")) {
			EventEntity event = (EventEntity) body;

			StringBuilder sql = new StringBuilder(
					"INSERT INTO " + tableName + " (pet_eventcode , pet_eventdesc, system_generated)")
					.append(" VALUES ('%s', '%s', '%s')");

			String formattedSql = String.format(sql.toString(), event.getPet_eventcode(), event.getPet_eventdesc(),
					event.getSystem_gen());

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
		String url = "jdbc:mysql://localhost:3306/";
		String username = "root";
		String password = "root";
		Connection connection = DriverManager.getConnection(url, username, password);
		String tableName = determineTableName(dbName, type);
		if (type.equals("event")) {
			EventEntity event = (EventEntity) body;
			Statement disableConstraintsStmt = connection.createStatement();
			disableConstraintsStmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");
			StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ").append("pet_eventcode = '%s', ")
					.append("pet_eventdesc = '%s', ").append("system_generated = '%s' ")
					.append("WHERE pet_eventcode = '" + event.getPet_eventcode() + "'");
			String formattedSql = String.format(sql.toString(), event.getPet_eventcode(), event.getPet_eventdesc(),
					event.getSystem_gen());

			System.out.println(formattedSql);
			try {

				int rowsAffected = jdbcTemplate.update(formattedSql);
				return "Query executed successfully. Rows affected: " + rowsAffected;
			} catch (Exception e) {
//				Throwable rootCause = e.getRootCause();
				e.printStackTrace();
				System.out.println(e.getMessage());
				return e.getMessage();
			}
		} else if (type.equals("transaction_type")) {
			TransactionType tran_type = (TransactionType) body;
			StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ").append("por_orgacode = '%s', ")
					.append("ptr_trancode = '%s', ").append("pet_eventcode = '%s', ").append("ptr_trandesc = '%s', ")
					.append("system_generated = '%s' ")
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
				StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ").append("pch_chrgcode = '%s', ")
						.append("por_orgacode = '%s', ").append("pch_chrgdesc = '%s', ")
						.append("pch_chrgshort = '%s', ").append("pel_elmtcode = '%s', ")
						.append("ptr_trancode = '%s', ").append("pch_chrgprofit = '%s', ").append("soc_charges = '%s' ")
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
				StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ").append("pch_chrgcode = '%s', ")
						.append("por_orgacode = '%s', ").append("pch_chrgdesc = '%s', ")
						.append("pch_chrgshort = '%s', ").append("pel_elmtcode = '%s', ")
						.append("ptr_trancode = '%s', ").append("pch_chrginterest = '%s', ")
						.append("pch_chrgpenalty = '%s', ").append("pch_chrgprincipal = '%s', ")
						.append("soc_charges = '%s' ").append("WHERE por_orgacode = '" + charges.getPor_orgacode()
								+ "' AND pch_chrgcode = '" + charges.getPch_chrgcode() + "'");

				String formattedSql = String.format(sql.toString(), charges.getPch_chrgcode(),
						charges.getPor_orgacode(), charges.getPch_chrgdesc(), charges.getPch_chrgshort(),
						charges.getPel_elmtcode(), charges.getPtr_trancode(), charges.getPch_chrginterest(),
						charges.getPch_chrgpenalty(), charges.getPch_chrgprincipal(), charges.getSoc_charges());

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
				return "invalid type";

		} else
			return "error";
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

			String sql = "INSERT INTO " + tableName + "(pet_eventcode,pet_eventdesc,system_generated) VALUES (?,?,?)";
			PreparedStatement statement = connection.prepareStatement(sql);
			Statement disableConstraintsStmt = connection.createStatement();
			disableConstraintsStmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");
			statement.setInt(1, event.getPet_eventcode());
			statement.setString(2, event.getPet_eventdesc());
			statement.setInt(3, event.getSystem_gen());
			int rowsAffected = statement.executeUpdate();
			System.out.println(rowsAffected + " :rows effected");

			statement.close();
			connection.close();
			return rowsAffected + " :rows effected";

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
				return rowsAffected + " :rows effected";
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
				return rowsAffected + " :rows effected";
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
			statement.setString(3, transEntity.getPet_eventcode());
			statement.setString(4, transEntity.getPtr_trandesc());
			statement.setString(5, transEntity.getSystem_generated());
			int rowsAffected = statement.executeUpdate();
			System.out.println(rowsAffected + " :rows effected in Database: " + databaseName);
			statement.close();
			connection.close();
			return rowsAffected + " :rows effected";
		}
		return "Error";
	}

//----------------------------------Delete-row
	public String deleteRow(Object body, String databaseName, String type) throws SQLException {
		String url = "jdbc:mysql://localhost:3306/";
		String username = "root";
		String password = "root";
		Connection connection = DriverManager.getConnection(url, username, password);

		if (type.equals("event")) {
			EventEntity event = (EventEntity) body;
			String sql = "DELETE FROM " + determineTableName(databaseName, type) + " WHERE pet_eventcode = ? ";

			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, event.getPet_eventcode());
			int rowsAffected = statement.executeUpdate();
			System.out.println(rowsAffected + " :rows deleted");
			statement.close();
			connection.close();
			return rowsAffected + " :rows deleted";
		} else if (type.equals("charges")) {
			Charges charges = (Charges) body;
			String sql = "DELETE FROM " + determineTableName(databaseName, type) + " WHERE " + "pch_chrgcode = ? AND "
					+ "por_orgacode = ?";
			PreparedStatement statement = connection.prepareStatement(sql);
			Statement disableConstraintsStmt = connection.createStatement();
			disableConstraintsStmt.execute("SET SESSION FOREIGN_KEY_CHECKS = 0");
			statement.setString(1, charges.getPch_chrgcode());
			statement.setString(2, charges.getPor_orgacode());
			int rowsAffected = statement.executeUpdate();
			System.out.println(rowsAffected + " :rows deleted");
			statement.close();
			connection.close();
			return rowsAffected + " :rows deleted";
		} else if (type.equals("transaction_type")) {
			TransactionType transaction = (TransactionType) body;
			String sql = "DELETE FROM " + determineTableName(databaseName, type) + " WHERE " + "ptr_trancode = ? AND "
					+ "por_orgacode = ?";
			PreparedStatement statement = connection.prepareStatement(sql);
			Statement disableConstraintsStmt = connection.createStatement();
			disableConstraintsStmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");
			statement.setString(1, transaction.getPtr_trancode());
			statement.setString(2, transaction.getPor_orgacode());
			int rowsAffected = statement.executeUpdate();
			System.out.println(rowsAffected + " :rows deleted");
			statement.close();
			connection.close();
			return rowsAffected + " :rows deleted";
		}
		return "Error deleting,.";

	}

	public void deleteRows(List<Object> myObjects, String type, String dbName) {
		String sql = "DELETE FROM deposit.pr_gn_de_depositevents WHERE pet_eventcode = ?";
//        EventEntity event = (EventEntity) myObjects;
		EventEntity[] events = (EventEntity[]) myObjects.toArray();
		for (EventEntity event : events) {
			jdbcTemplate.update(sql, event.getPet_eventcode());
		}
	}

//----------------------------------------Get All Data-----------------------------------
	public List<Map<String, Object>> getAllData(String type) {
		Map<String, Object> response = new HashMap<>();
		List<Map<String, Object>> result = new ArrayList<>();
		List<String> dbName = new ArrayList<>();
		if (type.equals("event")) {
			dbName.add(0, "loan.pr_gn_le_loanevents");
			dbName.add(1, "deposit.pr_gn_de_depositevents");
			dbName.add(2, "generalledger.pr_gn_ge_glevents");
		} else if (type.equals("transaction_type")) {
			dbName.add(0, "loan.pr_gn_lt_loantransactiontype");
			dbName.add(1, "deposit.pr_gn_dt_deposittransactiontype");
			dbName.add(2, "generalledger.pr_gn_gt_gltransactiontype");
		} else if (type.equals("charges")) {
			dbName.add(0, "loan.pr_gn_ch_charges");
			dbName.add(1, "deposit.pr_gn_ch_charges");
		} else {
			response.put("message", "invalid type: " + type);
			result.add(response);
			return result;
		}
		try {
			for (String item : dbName) {
				response.put("Value for: ", item);
				result.add(response);
				result.addAll(jdbcTemplate.queryForList("SELECT * FROM " + item));
			}
			return result;
		} catch (DataAccessException e) {
			response.clear();
			response.put("message", e.getMessage());
			result.clear();
			result.add(response);
			return result;
		}

	}

	String mongoPath = System.getProperty("user.home") + "\\Downloads\\Backup\\mongo";
	String mysqlPath = System.getProperty("user.home") + "\\Downloads\\Backup\\mysql";

//	public String backup(String type) throws IOException {
//		String result = "";
//
//		List<String> mongoDb = Arrays.asList("crm", "security", "loan", "deposit", "generalledger", "configuration");
//
//		if (type.equals("mongo")) {
//			File backPath = new File(mongoPath);
//			if (!backPath.exists()) {
//				backPath.mkdirs();
//			}
//			for (String db : mongoDb) {
//				ProcessBuilder pb = new ProcessBuilder("mongodump", "--db", db, "--out", mongoPath);
//				try {
//				 pb.start();	
//
//				} catch (Exception e) {
//					System.out.println(e.getMessage());
//				}
//			}
//			
//			zipMongo(mongoPath);
//
//			System.out.println(result);
//			return result;
//		} else if (type.equals("mysql")) {
//			File backPath = new File(mysqlPath);
//			if (!backPath.exists()) {
//				backPath.mkdirs();
//			}
//				ProcessBuilder pb = new ProcessBuilder("mysqldump", "-uroot", "-proot", "--all-databases", "-r",
//						mysqlPath +"\\"+ "mysql" + ".sql");
//				System.out.println(mysqlPath);
//				try {
//					Process p = pb.start();
//					int exitcode = p.waitFor();
//					System.out.println("Exit code: " + exitcode);
//					if (exitcode == 0) {
//						System.out.println("Sql backup created successfully");
//						result = "mysql success";
//					} else {
//						result = "backup fail";
//					}
//					p.destroy();
//				} catch (Exception e) {
//					System.out.println(e.getMessage());
//				}
//				
////			}
//			zipMysql(result);
//		} else {
//			return "invalid type";
//		}
//		System.out.println(result);
//		return result;
//
//	}
//
//	public String zipMongo(String path) throws IOException {
//		byte[] buffer = new byte[1024];
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		ZipOutputStream zos = new ZipOutputStream(baos);
//		File directory = new File(path);
//		if (directory.isDirectory()) {
//			String zipFileName = "backup_" + "mongo" + ".zip";
//			for (File subDirectory : directory.listFiles()) {
//				if (subDirectory.isDirectory()) {
//					for (File file : subDirectory.listFiles()) {
//						FileInputStream fis = new FileInputStream(file);
//						zos.putNextEntry(new ZipEntry(subDirectory.getName() + "\\" + file.getName()));
//						int length;
//						while ((length = fis.read(buffer)) > 0) {
//							zos.write(buffer, 0, length);
//						}
//						zos.closeEntry();
//						fis.close();
//					}
//				}
//			}
//			zos.close();
//			System.out.println("Zip file created successfully: " + zipFileName);
//		} else {
//			throw new IllegalArgumentException("Directory not found: " + path);
//		}
//
//		zos.close();
//		baos.close();
//
//		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
//				.getResponse();
//		response.setContentType("application/zip");
//		response.setHeader("Content-Disposition", "attachment; filename=\"" + "backup_mongo.zip\"");
//
//		ServletOutputStream sos = response.getOutputStream();
//		sos.write(baos.toByteArray());
//		sos.flush();
//		sos.close();
//
//		return ("Created zip file: " + path + ".zip \n");
//	}
//
//	public void zipMysql(String path) throws IOException {
//		byte[] buffer = new byte[1024];
//		File backupFolder = new File(mysqlPath);
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		ZipOutputStream zos = new ZipOutputStream(baos);
//
//		File[] filesToZip = backupFolder.listFiles();
//		for (File file : filesToZip) {
//			FileInputStream fis = new FileInputStream(file);
//			zos.putNextEntry(new ZipEntry(file.getName()));
//			int length;
//			while ((length = fis.read(buffer)) > 0) {
//				zos.write(buffer, 0, length);
//			}
//			zos.closeEntry();
//			fis.close();
//		}
//
//		zos.close();
//		byte[] zipBytes = baos.toByteArray();
//		baos.close();
//
//		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
//				.getResponse();
//		response.setContentType("application/zip");
//		response.setHeader("Content-Disposition", "attachment; filename=\"" + "backup_mysql.zip" + "\"");
//		response.setContentLength(zipBytes.length);
//
//		OutputStream os = response.getOutputStream();
//		os.write(zipBytes);
//		os.flush();
//		os.close();
//	}

//	private void zipFile(String fileName) throws IOException {
//		String sourceFile = mysqlPath;
//		FileOutputStream fos = new FileOutputStream("mysql.zip");
//		ZipOutputStream zipOut = new ZipOutputStream(fos);
//
//		File fileToZip = new File(sourceFile);
//		zipOut.close();
//		fos.close();
//		if (fileToZip.isHidden()) {
//			return;
//		}
//		if (fileToZip.isDirectory()) {
//			if (fileName.endsWith("/")) {
//				zipOut.putNextEntry(new ZipEntry(fileName));
//				zipOut.closeEntry();
//			} else {
//				zipOut.putNextEntry(new ZipEntry(fileName + "/"));
//				zipOut.closeEntry();
//			}
//			File[] children = fileToZip.listFiles();
//			for (File childFile : children) {
//				zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
//			}
//			return;
//		}
//		FileInputStream fis = new FileInputStream(fileToZip);
//		ZipEntry zipEntry = new ZipEntry(fileName);
//		zipOut.putNextEntry(zipEntry);
//		byte[] bytes = new byte[1024];
//		int length;
//		while ((length = fis.read(bytes)) >= 0) {
//			zipOut.write(bytes, 0, length);
//		}
//		fis.close();
//	}
	public void backup() {
	    String[] databases = {"onlinebanking", "deposit", "configuration", "loan", "generalledger"};
	    String backupPath = "C:\\Users\\mmghh\\Downloads\\Backup\\mysql\\mysql.sql";
	    
	    List<String> command = new ArrayList<>();
	    command.add("mysqldump");
	    command.add("-uroot");
	    command.add("-proot");
	    command.add("--databases");
	    command.addAll(Arrays.asList(databases));
	    command.add("--result-file=" + backupPath);

	    ProcessBuilder pb = new ProcessBuilder(command);
	    System.out.println(pb.command());
	    System.out.println(mysqlPath);

	    try {
	        Process p = pb.start();
	        int exitcode = p.waitFor();
	        System.out.println("Exit code: " + exitcode);
	        if (exitcode == 0) {
	            System.out.println("SQL backup created successfully");
	        } else {
	            System.out.println("Error creating SQL backup");
	        }
	    } catch (Exception e) {
	        System.out.println(e.getMessage());
	    }
	}
	
	public void zipMysql(String type) throws IOException {
		String sourceFile = mysqlPath+"\\mysql.sql";
		String x = mysqlPath+"\\mysql.zip";
		File outPut = new File(x);
		FileOutputStream fos = new FileOutputStream(x);
		ZipOutputStream zipOut = new ZipOutputStream(fos);

		File fileToZip = new File(sourceFile);
		FileInputStream fis = new FileInputStream(fileToZip);
		ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
		zipOut.putNextEntry(zipEntry);

		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zipOut.write(bytes, 0, length);
		}

		zipOut.close();
		fis.close();
		fos.close();
	}
}
