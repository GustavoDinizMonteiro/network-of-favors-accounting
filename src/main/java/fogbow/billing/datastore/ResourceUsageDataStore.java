package fogbow.billing.datastore;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Properties;

public class ResourceUsageDataStore extends DataStore {
	
	//TODO Remove it and use properties
	public static final String FAKE_DATABASE_URL = "/local/mafra/fake_database.sqlite3";
	
	public static final String DATABASE_URL_PROP = "usage_database_url";
	
	private static final String RESOURCE_USAGE_TABLE_NAME = "resource_usage";
	
	private static final String INSERT_USER_RESOURCE_USAGE_SQL = "INSERT INTO " + RESOURCE_USAGE_TABLE_NAME
			+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
							+ "userId VARCHAR(2) NOT NULL, "
							+ "cpu REAL, "
							+ "ram REAL, "
							+ "disk REAL, "
							+ "PRIMARY KEY (userId)"
							+ ")");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(statement, connection);
		}
	}

}
