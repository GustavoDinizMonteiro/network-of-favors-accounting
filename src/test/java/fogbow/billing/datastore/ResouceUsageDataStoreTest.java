package fogbow.billing.datastore;

import java.io.File;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fogbow.billing.model.OrderRecord;

public class ResouceUsageDataStoreTest {
	
	private static String usage_databaseFile = "usageDataStoreTest.sqlite3";
    private static String usage_databaseURL = "jdbc:sqlite:" + usage_databaseFile;
    
    private static String ras_databaseFile = "rasDataStoreTest.sqlite3";
    private static String ras_databaseURL = "jdbc:sqlite:" + ras_databaseFile;

    private static Properties properties;
    
    private static Timestamp currentTimestamp = new Timestamp(new Date().getTime());
    private static final int duration = 1800;
    
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";
    private static final String USER_NAME = "user-name";
    private static final String REQUESTING_MEMBER = "requesting-member";
    private static final String PROVIDING_MEMBER = "providing-member";
    private static final String COMPUTE = "COMPUTE";
    private static final String VOLUME = "VOLUME";

    private ResourceUsageDataStore resourceUsageDataStore;
    
    @Before
    public void setUp() throws SQLException {
    	
    	properties = new Properties();
    	properties.setProperty(ResourceUsageDataStore.DATABASE_URL_PROP, usage_databaseURL);
    	properties.setProperty(ResourceUsageDataStore.RAS_URL_PROP, ras_databaseURL);
    	
    	resourceUsageDataStore = new ResourceUsageDataStore(properties);
    }
    
    @After
    public void tearDown() {
        new File(usage_databaseFile).delete();
        new File(ras_databaseFile).delete();
    }
    
    @Test
    public void testInsertOrderRecords() throws SQLException {
    	
    	// set up
    	OrderRecord computeOrderRecord1 = new OrderRecord("fake-id-1", COMPUTE, "2/4", USER_ID_1,
    			USER_NAME, REQUESTING_MEMBER, PROVIDING_MEMBER, currentTimestamp,
    			duration);
    	
    	OrderRecord computeOrderRecord2 = new OrderRecord("fake-id-2", COMPUTE, "4/6", USER_ID_1,
    			"fake-user-name", REQUESTING_MEMBER, PROVIDING_MEMBER, currentTimestamp,
    			duration);
    	
    	// different userId
    	OrderRecord computeOrderRecord3 = new OrderRecord("fake-id-3", COMPUTE, "4/6", USER_ID_2,
    			"fake-user-name", REQUESTING_MEMBER, PROVIDING_MEMBER, currentTimestamp,
    			duration);
    	
    	OrderRecord volumeOrderRecord1 = new OrderRecord("fake-id-4", VOLUME, "100", USER_ID_1,
    			"fake-user-name", REQUESTING_MEMBER, PROVIDING_MEMBER, currentTimestamp,
    			duration);
    	
    	// different userId
    	OrderRecord volumeOrderRecord2 = new OrderRecord("fake-id-5", VOLUME, "200", USER_ID_2,
    			"fake-user-name", REQUESTING_MEMBER, PROVIDING_MEMBER, currentTimestamp,
    			duration);
    	
    	// exercise
    	resourceUsageDataStore.insertOrderRecord(computeOrderRecord1);
    	resourceUsageDataStore.insertOrderRecord(computeOrderRecord2);
    	resourceUsageDataStore.insertOrderRecord(computeOrderRecord3);
    	resourceUsageDataStore.insertOrderRecord(volumeOrderRecord1);
    	resourceUsageDataStore.insertOrderRecord(volumeOrderRecord2);  	
    	
    	// verify 
    	
    	// user-id-1 - compute    	
    	List<OrderRecord> listResultUser1 = resourceUsageDataStore.getOrders(USER_ID_1, REQUESTING_MEMBER, PROVIDING_MEMBER, COMPUTE);
    	Assert.assertEquals(2, listResultUser1.size());
    	Assert.assertEquals(computeOrderRecord1, listResultUser1.get(0));
    	Assert.assertEquals(computeOrderRecord2, listResultUser1.get(1));
    	
    	// user-id-1 - volume
    	List<OrderRecord> listResultUser1Volume = resourceUsageDataStore.getOrders(USER_ID_1, REQUESTING_MEMBER, PROVIDING_MEMBER, VOLUME);
    	Assert.assertEquals(1, listResultUser1Volume.size());
    	Assert.assertEquals(volumeOrderRecord1, listResultUser1Volume.get(0));
    	
    	// user-id-2 - compute
    	List<OrderRecord> listResultUser2 = resourceUsageDataStore.getOrders(USER_ID_2, REQUESTING_MEMBER, PROVIDING_MEMBER, COMPUTE);
    	Assert.assertEquals(1, listResultUser2.size());
    	Assert.assertEquals(computeOrderRecord3, listResultUser2.get(0));
    	
    	
    	// user-id-2 - volume
    	List<OrderRecord> listResultUser2Volume = resourceUsageDataStore.getOrders(USER_ID_2, REQUESTING_MEMBER, PROVIDING_MEMBER, VOLUME);
    	Assert.assertEquals(1, listResultUser2Volume.size());
    	Assert.assertEquals(volumeOrderRecord2, listResultUser2Volume.get(0));
    	
    	// checking inexistent order from inexistent requesting member
    	Assert.assertTrue(resourceUsageDataStore.getOrders(USER_ID_2, "wrong-requesting-member", PROVIDING_MEMBER, COMPUTE).isEmpty());
    }
    
    

}
