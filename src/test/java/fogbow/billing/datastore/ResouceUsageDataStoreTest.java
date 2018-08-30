package fogbow.billing.datastore;

import java.io.File;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.fogbowcloud.ras.core.models.ResourceType;
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
    	OrderRecord computeOrderRecord1 = new OrderRecord("fake-id-1", "COMPUTE", "2/4", "user-id-1",
    			"fake-user-name", "fake-requesting-member", "fake-providing-member", currentTimestamp,
    			duration);
    	
    	OrderRecord computeOrderRecord2 = new OrderRecord("fake-id-2", "COMPUTE", "4/6", "user-id-1",
    			"fake-user-name", "fake-requesting-member", "fake-providing-member", currentTimestamp,
    			duration);
    	
    	// different userId
    	OrderRecord computeOrderRecord3 = new OrderRecord("fake-id-3", "COMPUTE", "4/6", "user-id-2",
    			"fake-user-name", "fake-requesting-member", "fake-providing-member", currentTimestamp,
    			duration);
    	
    	OrderRecord volumeOrderRecord1 = new OrderRecord("fake-id-4", "VOLUME", "100", "user-id-1",
    			"fake-user-name", "fake-requesting-member", "fake-providing-member", currentTimestamp,
    			duration);
    	
    	// different userId
    	OrderRecord volumeOrderRecord2 = new OrderRecord("fake-id-5", "VOLUME", "200", "user-id-2",
    			"fake-user-name", "fake-requesting-member", "fake-providing-member", currentTimestamp,
    			duration);
    	
    	// exercise
    	resourceUsageDataStore.insertOrderRecord(computeOrderRecord1);
    	resourceUsageDataStore.insertOrderRecord(computeOrderRecord2);
    	resourceUsageDataStore.insertOrderRecord(computeOrderRecord3);
    	resourceUsageDataStore.insertOrderRecord(volumeOrderRecord1);
    	resourceUsageDataStore.insertOrderRecord(volumeOrderRecord2);
    	
    	
    	// verify user-id-1    	
    	List<OrderRecord> listResultUser1 = resourceUsageDataStore.getOrdersByUserId("user-id-1");
    	Assert.assertEquals(3, listResultUser1.size());
    	Assert.assertEquals(computeOrderRecord1, listResultUser1.get(0));
    	Assert.assertEquals(computeOrderRecord2, listResultUser1.get(1));
    	Assert.assertEquals(volumeOrderRecord1, listResultUser1.get(2));	
    	
    	List<OrderRecord> listResultComputeOrderUser1 = resourceUsageDataStore.getOrdersByUserIdAndResourceType("user-id-1", ResourceType.COMPUTE);   	
    	Assert.assertEquals(2, listResultComputeOrderUser1.size());
    	Assert.assertEquals(computeOrderRecord1, listResultComputeOrderUser1.get(0));
    	Assert.assertEquals(computeOrderRecord2, listResultComputeOrderUser1.get(1));
    	
    	List<OrderRecord> listResultVolumeOrderUser1 = resourceUsageDataStore.getOrdersByUserIdAndResourceType("user-id-1", ResourceType.VOLUME);   	
    	Assert.assertEquals(1, listResultVolumeOrderUser1.size());
    	Assert.assertEquals(volumeOrderRecord1, listResultVolumeOrderUser1.get(0));
    	
    	
    	// verify user-id-2
    	List<OrderRecord> listResultUser2 = resourceUsageDataStore.getOrdersByUserId("user-id-2");
    	Assert.assertEquals(2, listResultUser2.size());
    	Assert.assertEquals(computeOrderRecord3, listResultUser2.get(0));
    	Assert.assertEquals(volumeOrderRecord2, listResultUser2.get(1));
    	
    	List<OrderRecord> listResultComputeOrderUser2 = resourceUsageDataStore.getOrdersByUserIdAndResourceType("user-id-2", ResourceType.COMPUTE);   	
    	Assert.assertEquals(1, listResultComputeOrderUser2.size());
    	Assert.assertEquals(computeOrderRecord3, listResultComputeOrderUser2.get(0));

    	List<OrderRecord> listResultVolumeOrderUser2 = resourceUsageDataStore.getOrdersByUserIdAndResourceType("user-id-2", ResourceType.VOLUME);   	
    	Assert.assertEquals(1, listResultVolumeOrderUser2.size());
    	Assert.assertEquals(volumeOrderRecord2, listResultVolumeOrderUser2.get(0));
    	
    }
    
    

}
