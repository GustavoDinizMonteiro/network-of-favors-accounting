package fogbow.billing.datastore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class AuthDataStore extends DataStore{
	
	//TODO Remove it and use properties
	public static final String FAKE_DATABASE_URL = "jdbc:sqlite:/local/mafra/fake_auth_database.sqlite3";

	public static final String DATABASE_URL_PROP = "auth_database_url";
	
	private static final String AUTHORIZATION_TABLE_NAME = "auth";
	
	private static final String INSERT_USER_AUTH_SQL = "INSERT INTO " + AUTHORIZATION_TABLE_NAME
			+ " VALUES(?, ?)";
	
	private static final String UPDATE_USER_AUTH_SQL = "UPDATE " + AUTHORIZATION_TABLE_NAME + " SET isAuthorized = ? "
            + "WHERE userId = ?";
	
	private static final String SELECT_USER_AUTH_SQL = "SELECT isAuthorized FROM " + AUTHORIZATION_TABLE_NAME
            + " WHERE userId = ?";
	
	private static final int UNAUTHORIZED_USER = 0;
	private static final int AUTHORIZED_USER = 1;;

	public AuthDataStore() {
		super(FAKE_DATABASE_URL);

		Statement statement = null;
		Connection connection = null;
		try {
			connection = getConnection();
			statement = connection.createStatement();
			statement
					.execute("CREATE TABLE IF NOT EXISTS auth("
							+ "userId VARCHAR(255) PRIMARY KEY, "
							+ "isAuthorized INTEGER"
							+ ")");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(statement, connection);
		}
	
	}
	
	public AuthDataStore(Properties properties) {
		super(properties.getProperty(DATABASE_URL_PROP));

		Statement statement = null;
		Connection connection = null;
		try {
			connection = getConnection();
			statement = connection.createStatement();
			statement
					.execute("CREATE TABLE IF NOT EXISTS auth("
							+ "userId VARCHAR(255) PRIMARY KEY, "
							+ "isAuthorized INTEGER"
							+ ")");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(statement, connection);
		}
	
	}
	
	public boolean addUser(String userId) {
		
		PreparedStatement insertMemberStatement = null;
		
		Connection connection = null;
		
		try {
			connection = getConnection();
			connection.setAutoCommit(false);
			
			insertMemberStatement = connection.prepareStatement(INSERT_USER_AUTH_SQL);
			insertMemberStatement = connection
					.prepareStatement(INSERT_USER_AUTH_SQL);
			
			insertMemberStatement.setString(1, userId);
			insertMemberStatement.setInt(2, UNAUTHORIZED_USER);
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
	
	public boolean unauthorizeUser(String userId) {
		
		PreparedStatement updateMemberStatement = null;
		
		Connection connection = null;
		
		try {
			connection = getConnection();
			connection.setAutoCommit(false);
			
			updateMemberStatement = connection.prepareStatement(UPDATE_USER_AUTH_SQL);
			
			updateMemberStatement.setInt(1, UNAUTHORIZED_USER);
			updateMemberStatement.setString(2, userId);
			updateMemberStatement.addBatch();

			
			int[] executeBatch = updateMemberStatement.executeBatch();
			
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
			close(updateMemberStatement, connection);
		}
	}
	
	public boolean authorizeUser(String userId) {
		
		PreparedStatement updateMemberStatement = null;
		
		Connection connection = null;
		
		try {
			connection = getConnection();
			connection.setAutoCommit(false);
			
			updateMemberStatement = connection.prepareStatement(UPDATE_USER_AUTH_SQL);
			
			updateMemberStatement.setInt(1, AUTHORIZED_USER);
			updateMemberStatement.setString(2, userId);
			updateMemberStatement.addBatch();

			
			int[] executeBatch = updateMemberStatement.executeBatch();
			
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
			close(updateMemberStatement, connection);
		}
	}
	
	public int getUserAuthorization(String userId, String resourceType, String operation) {
		PreparedStatement selectMemberStatement = null;
		
		Connection connection = null;
		
		int authorization = -1;
		
		try {
			connection = getConnection();
			connection.setAutoCommit(false);
			
			selectMemberStatement = connection
					.prepareStatement(SELECT_USER_AUTH_SQL);
			
			selectMemberStatement.setString(1, userId);
			
			ResultSet rs = selectMemberStatement.executeQuery();
			while (rs.next()) {
				authorization = rs.getInt("isAuthorized");
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
		
		return authorization;
		
	}

}
