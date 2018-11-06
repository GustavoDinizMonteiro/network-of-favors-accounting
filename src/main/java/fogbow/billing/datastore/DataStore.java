package fogbow.billing.datastore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DataStore {
	
	protected static final String DATASTORE_SQLITE_DRIVER = "org.sqlite.JDBC";

	private String databaseURL;

	public DataStore(String databaseURL) {
		try {
            Class.forName(DATASTORE_SQLITE_DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println("Invalid driver");
        }
		setDatabaseURL(databaseURL);
		
	}
	
	protected Connection getConnection() throws SQLException {
		try {
			return DriverManager.getConnection(getDatabaseURL());
		} catch (SQLException e) {
			throw e;
		}
	}
	
	protected Connection getConnection(String databaseURL) throws SQLException {
		try {
			return DriverManager.getConnection(databaseURL);
		} catch (SQLException e) {
			throw e;
		}
	}
	
	protected void close(Statement statement, Connection conn) {
		if (statement != null) {
			try {
				if (!statement.isClosed()) {
					statement.close();
				}
			} catch (SQLException e) {
			}
		}

		if (conn != null) {
			try {
				if (!conn.isClosed()) {
					conn.close();
				}
			} catch (SQLException e) {
			}
		}
	}
	
	protected boolean executionFailed(Connection connection, int[] executeBatch)
			throws SQLException {
		for (int i : executeBatch) {
			if (i == PreparedStatement.EXECUTE_FAILED) {
				return true;
			}
		}
		return false;
	}

	protected String getDatabaseURL() {
		return databaseURL;
	}

	protected void setDatabaseURL(String databaseURL) {
		this.databaseURL = databaseURL;
	}


}
