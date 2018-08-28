package fogbow.billing.datastore;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ResouceUsageDataStoreTest {
	
	private static String databaseFile = "resourceDataStoreTest.sqlite3";
    private static String databaseURL = "jdbc:sqlite:" + databaseFile;

    private static Properties properties;

    private ResourceUsageDataStore resourceUsageDataStore;
    
    ComputeUsageEntry entry1 = 
    		new ComputeUsageEntry("id-1", "order1", 1.0, 1.0, 1.0, LocalDate.of(2018,8,2), LocalDate.of(2018,8,4));
    
    ComputeUsageEntry entry2 = 
    		new ComputeUsageEntry("id-1", "order2", 2.0, 3.0, 10.0, LocalDate.of(2018,8,1), LocalDate.of(2018,8,5));
    	
    private static final int AUTHORIZED_USER = 1;
    private static final int UNAUTHORIZED_USER = 0;
    private static final int UNEXPECTED_AUTHORIZATION = -1;
    
    @Before
    public void setUp() throws SQLException {
    	
    	properties = new Properties();
    	properties.setProperty(ResourceUsageDataStore.DATABASE_URL_PROP, databaseURL);
    	
    	resourceUsageDataStore = new ResourceUsageDataStore(properties);
    }
    
    @After
    public void tearDown() {
        new File(databaseFile).delete();
    }

}
