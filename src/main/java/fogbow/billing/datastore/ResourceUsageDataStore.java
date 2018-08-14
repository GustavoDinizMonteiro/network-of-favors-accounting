package fogbow.billing.datastore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import fogbow.billing.model.UserUsage;

public class ResourceUsageDataStore extends DataStore {
	
	//TODO Remove it and use properties
	public static final String FAKE_DATABASE_URL = "/local/mafra/fake_usage_database.sqlite3";
	
	public static final String DATABASE_URL_PROP = "usage_database_url";
	
	private static final String RESOURCE_USAGE_TABLE_NAME = "resource_usage";
	
	private static final String INSERT_USER_RESOURCE_USAGE_SQL = "INSERT INTO " + RESOURCE_USAGE_TABLE_NAME
			+ " VALUES(?, ?, ?, ?, ?, ?, ?)";
	
	private static final String SELECT_CPU_USER_USAGE_SQL_CLOSED_ORDERS = "SELECT userId, SUM(vcpu * (closed - fulfilled)/86400000) AS cpuHours, SUM(vcpu) "
		+ "as cpuUsed FROM " + RESOURCE_USAGE_TABLE_NAME + " WHERE closed IS NOT NULL AND userId = ?";
	
	private static final String SELECT_CPU_USER_USAGE_SQL_OPEN_ORDERS = "SELECT userId, SUM(vcpu * (julianday(date('now')) - julianday(fulfilled)) * 24) AS cpuHours, SUM(vcpu) "
			+ "as cpuUsed FROM " + RESOURCE_USAGE_TABLE_NAME + " WHERE closed IS NULL AND userId = ?";
	
	private static final String SELECT_CPU_ALL_USERS_RESOURCE_USAGE_SQL_CLOSED_ORDERS = "SELECT userId, SUM(vcpu * ((closed - fulfilled)/3600000)) AS cpuHours, SUM(vcpu) "
			+ "as cpuUsed FROM " + RESOURCE_USAGE_TABLE_NAME + " WHERE closed IS NULL GROUP BY userId";
	
	private static final String SELECT_CPU_ALL_USERS_RESOURCE_USAGE_SQL_OPEN_ORDERS = "SELECT userId, SUM(vcpu * (julianday(date('now')) - julianday(fulfilled)) * 24) AS cpuHours, SUM(vcpu) "
			+ "as cpuUsed FROM " + RESOURCE_USAGE_TABLE_NAME + " WHERE closed IS NULL GROUP BY userId";
	
	
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-mm-dd");

	public ResourceUsageDataStore() {
		super(FAKE_DATABASE_URL);

		Statement statement = null;
		Connection connection = null;
		try {

			Class.forName(DATASTORE_SQLITE_DRIVER);

			connection = getConnection();
			statement = connection.createStatement();
			statement
					.execute("CREATE TABLE IF NOT EXISTS resource_usage("
							+ "userId VARCHAR(255) NOT NULL, "
							+ "orderId VARCHAR(255) NOT NULL"
							+ "vcpu REAL, "
							+ "ram REAL, "
							+ "disk REAL, "
							+ "fulfilled DATE, "
							+ "closed DATE, "
							+ "PRIMARY KEY (userId, orderId)"
							+ ")");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(statement, connection);
		}
	}
	
	public ResourceUsageDataStore(Properties properties) {
		super(properties.getProperty(DATABASE_URL_PROP));

		Statement statement = null;
		Connection connection = null;
		try {
			connection = getConnection();
			statement = connection.createStatement();
			statement
					.execute("CREATE TABLE IF NOT EXISTS resource_usage("
							+ "userId VARCHAR(255) NOT NULL, "
							+ "orderId VARCHAR(255) NOT NULL, "
							+ "vcpu REAL, "
							+ "ram REAL, "
							+ "disk REAL, "
							+ "fulfilled DATE, "
							+ "closed DATE, "
							+ "PRIMARY KEY (userId, orderId)"
							+ ")");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(statement, connection);
		}
	
	}
	
	public boolean addUsageEntry(ComputeUsageEntry computeEntry) {
		PreparedStatement insertMemberStatement = null;
		
		Connection connection = null;
		
		try {
			connection = getConnection();
			connection.setAutoCommit(false);
			
			insertMemberStatement = connection.prepareStatement(INSERT_USER_RESOURCE_USAGE_SQL);
			insertMemberStatement = connection
					.prepareStatement(INSERT_USER_RESOURCE_USAGE_SQL);
			
			insertMemberStatement.setString(1, computeEntry.getUserId());
			insertMemberStatement.setString(2, computeEntry.getOrderId());
			insertMemberStatement.setDouble(3, computeEntry.getVcpu());
			insertMemberStatement.setDouble(4, computeEntry.getRam());
			insertMemberStatement.setDouble(5, computeEntry.getDisk());
			insertMemberStatement.setDate(6, java.sql.Date.valueOf(computeEntry.getFulfilledDate()));
			insertMemberStatement.setDate(7, java.sql.Date.valueOf(computeEntry.getClosedDate()));
			
			insertMemberStatement.addBatch();

			
			int[] executeBatch = insertMemberStatement.executeBatch();
			
			if (executionFailed(connection, executeBatch)){
				System.out.println("Rollback will be executed.");
				connection.rollback();
				return false;
			}
			
			connection.commit();
			return true;
			
		} catch (SQLException e) {
			try {
				if (connection != null) {
					connection.rollback();
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
				System.out.println("Couldn't rollback transaction.");
			}
			return false;
		} finally {
			close(insertMemberStatement, connection);
		}
	}
	
	public UserUsage getCPUUsageByUser(String userId) {
		
		PreparedStatement selectMemberStatement = null;
		
		Connection connection = null;
		
		int vCPUHoursClosedOrders = 0;
		int vCPUHoursOpenOrders = 0;
		
		double vCPUUsedClosedOrders = 0;
		double vCPUUsedOpenOrders = 0;
		
		try {
			connection = getConnection();
			connection.setAutoCommit(false);
			
			
			// first query
			selectMemberStatement = connection
					.prepareStatement(SELECT_CPU_USER_USAGE_SQL_CLOSED_ORDERS);
			
			selectMemberStatement.setString(1, userId);
			
			ResultSet rs = null;
			
			rs = selectMemberStatement.executeQuery();
			System.out.println(rs.getDouble("cpuHours"));
			while (rs.next()) {
				vCPUHoursClosedOrders += rs.getDouble("cpuHours");
				vCPUUsedClosedOrders += rs.getDouble("cpuUsed");
			}
			selectMemberStatement.close();
			
			
			// second query
			selectMemberStatement = connection
					.prepareStatement(SELECT_CPU_USER_USAGE_SQL_OPEN_ORDERS);
			
			
			selectMemberStatement.setString(1, userId);
			
			rs = selectMemberStatement.executeQuery();
			while (rs.next()) {
				vCPUHoursOpenOrders += rs.getDouble("cpuHours");
				vCPUUsedOpenOrders += rs.getDouble("cpuUsed");
			}
			
			connection.commit();
			
		} catch (SQLException e) {
			try {
				if (connection != null) {
					connection.rollback();
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
				System.out.println("Couldn't rollback transaction.");
			}
			
		} finally {
			close(selectMemberStatement, connection);
		}
		
		
		
		return new UserUsage(userId, "vCPU", 
				vCPUUsedClosedOrders + vCPUUsedOpenOrders, 
				vCPUHoursClosedOrders + vCPUHoursOpenOrders);
		
		
	}

}
