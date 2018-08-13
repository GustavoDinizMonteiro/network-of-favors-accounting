package fogbow.billing.datastore;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Properties;

public class CreditDataStore extends DataStore {
	
	public static final String DATABASE_URL_PROP = "credit_database_url";
	
	private static final String CREDIT_TABLE_NAME = "credit";
	
	private static final String INSERT_USER_CREDIT_SQL = "INSERT INTO " + CREDIT_TABLE_NAME
			+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	public CreditDataStore(Properties properties) {
		super(properties.getProperty(DATABASE_URL_PROP));

		Statement statement = null;
		Connection connection = null;
		try {

			Class.forName(DATASTORE_SQLITE_DRIVER);

			connection = getConnection();
			statement = connection.createStatement();
			statement
					.execute("CREATE TABLE IF NOT EXISTS credit("
							+ "userId VARCHAR(2) NOT NULL, "
							+ "credit REAL, "
							+ "PRIMARY KEY (userId)"
							+ ")");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(statement, connection);
		}
	}
	
	

}
