package fogbow.billing.services;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.sql.Timestamp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import fogbow.billing.datastore.ResourceUsageDataStore;
import fogbow.billing.model.TimestampTableEntry;
import fogbow.billing.services.ResourceUsageService;
public class ResourceUsageServiceTest {
    
	private ResourceUsageService resourceUsageService;
	private ResourceUsageDataStore resourceUsageDataStore;
	
	@Before
	public void setUp() throws SQLException {
		
		resourceUsageDataStore = Mockito.mock(ResourceUsageDataStore.class);
		
		this.resourceUsageService = ResourceUsageService.getInstance();
		this.resourceUsageService.setResourceUsageDataStore(resourceUsageDataStore);	
		
	}
	
	// test case: The period of interest contains all time of order.
	@Test
	public void testDurationInterestedPeriodGreaterThanOrderDuration() {
		
		// set up
		
		// Start time of order: 01/08/2018 00:00:00
		Timestamp startTime = new Timestamp(new Long("1533092400000"));
		
		// duration: 30 min. So, the end time of order is: 01/08/2018 00:30:00
		int duration = 1800000;
		
		TimestampTableEntry entry = new TimestampTableEntry("fake-order-id", "COMPUTE", 
				"2/4", "fake-user-id", "fake-user-name", "requesting-member", 
				"providing-member", startTime, duration);
		
		// begin interested time: 30/07/2018 00:00:00 
		Timestamp begin = new Timestamp(new Long("1532919600000"));
		
		// end interested time: 02/08/2018 00:00:00 
		Timestamp end = new Timestamp(new Long("1533178800000"));
		
		// exercise
		long realDuration = resourceUsageService.getRealDuration(entry, begin, end);
		
		// verify. The interested period contains all the duration of order
		Assert.assertEquals(duration, realDuration);
			
	}
	
	// test case: The period of interest is exactly equals to order duration
	@Test
	public void testDurationInterestedPeriodEqualsOrderDuration() {
		
		// set up
		
		// Start time of order: 01/08/2018 00:00:00
		Timestamp startTime = new Timestamp(new Long("1533092400000"));
		
		// duration: 30 min. So, the end time of order is: 01/08/2018 00:30:00
		int duration = 1800000;
		
		TimestampTableEntry entry = new TimestampTableEntry("fake-order-id", "COMPUTE", 
				"2/4", "fake-user-id", "fake-user-name", "requesting-member", 
				"providing-member", startTime, duration);
		
		// begin interested time: 01/08/2018 00:00:00
		Timestamp begin = new Timestamp(new Long("1533092400000"));
		
		// end interested time: 01/08/2018 00:30:00 
		Timestamp end = new Timestamp(new Long("1533094200000"));
		
		// exercise
		long realDuration = resourceUsageService.getRealDuration(entry, begin, end);
		
		// verify. The interested period is exactly equals to order duration (30 min)
		Assert.assertEquals(duration, realDuration);
			
	}
	
	// test case: begin of interest period if greater than start time and the end of interest period
	// is lower than finish time of order.
	@Test
	public void testDurationInterestedPeriodBetweenOrderDuration() {
		
		// set up
		
		// Start time of order: 01/08/2018 00:00:00
		Timestamp startTime = new Timestamp(new Long("1533092400000"));
		
		// duration: 30 min. So, the end time of order is: 01/08/2018 00:30:00
		int duration = 1800000;
		
		TimestampTableEntry entry = new TimestampTableEntry("fake-order-id", "COMPUTE", 
				"2/4", "fake-user-id", "fake-user-name", "requesting-member", 
				"providing-member", startTime, duration);
		
		// begin interested time: 01/08/2018 00:15:00
		Timestamp begin = new Timestamp(new Long("1533093300000"));
		
		// end interested time: 01/08/2018 00:20:00 
		Timestamp end = new Timestamp(new Long("1533093600000"));
		
		// interest period is 5 min
		long expectedDuration = 300000;
		
		// exercise	
		long realDuration = resourceUsageService.getRealDuration(entry, begin, end);

		// verify. The interested period is 5 min between the start and the finish of order, to 5 min is the duration
		Assert.assertEquals(expectedDuration, realDuration);
			
	}
	
	// test case: The begin of interested period is before start time
	@Test
	public void testDurationInterestedBeginBeforeStartTime() {
		
		// set up
		
		// Start time of order: 01/08/2018 00:00:00
		Timestamp startTime = new Timestamp(new Long("1533092400000"));
		
		// duration: 30 min. So, the end time of order is: 01/08/2018 00:30:00
		int duration = 1800000;
		
		TimestampTableEntry entry = new TimestampTableEntry("fake-order-id", "COMPUTE", 
				"2/4", "fake-user-id", "fake-user-name", "requesting-member", 
				"providing-member", startTime, duration);
		
		// begin interested time: 31/07/2018 23:50:00
		Timestamp begin = new Timestamp(new Long("1533091800000"));
		
		// end interested time: 01/08/2018 00:20:00 
		Timestamp end = new Timestamp(new Long("1533093600000"));
		
		// interest period is 30 min, but the order was fulfilled just in 01/08/2018, so the the time is 20 min
		// between 00:00 and 00:20
		long expectedDuration = 1200000;
		
		// exercise	
		long realDuration = resourceUsageService.getRealDuration(entry, begin, end);

		// verify
		Assert.assertEquals(expectedDuration, realDuration);
			
	}
	
	// test case: The end of interested period is after finish time
	@Test
	public void testDurationInterestedEndAfterFinishTime() {
		
		// set up
		
		// Start time of order: 01/08/2018 00:00:00
		Timestamp startTime = new Timestamp(new Long("1533092400000"));
		
		// duration: 30 min. So, the end time of order is: 01/08/2018 00:30:00
		int duration = 1800000;
		
		TimestampTableEntry entry = new TimestampTableEntry("fake-order-id", "COMPUTE", 
				"2/4", "fake-user-id", "fake-user-name", "requesting-member", 
				"providing-member", startTime, duration);
		
		// begin interested time: 01/08/2018 00:20:00 
		Timestamp begin = new Timestamp(new Long("1533093600000"));
		
		// end interested time: 01/08/2018 00:50:00
		Timestamp end = new Timestamp(new Long("1533095400000"));
	
		
		// interest period is 30 min, but the order was closed at 00:30, so the the time is 10 min
		// between 00:20 and 00:30
		long expectedDuration = 600000;
		
		// exercise	
		long realDuration = resourceUsageService.getRealDuration(entry, begin, end);

		// verify
		Assert.assertEquals(expectedDuration, realDuration);
			
	}
	
	
	// test case: The end of interested time is before start time of order
	@Test
	public void testDurationInterestedEndBeforeStartTimeOfOrder() {
		
		// set up
		
		// Start time of order: 01/08/2018 00:00:00
		Timestamp startTime = new Timestamp(new Long("1533092400000"));
		
		// duration: 30 min. So, the end time of order is: 01/08/2018 00:30:00
		int duration = 1800000;
		
		TimestampTableEntry entry = new TimestampTableEntry("fake-order-id", "COMPUTE", 
				"2/4", "fake-user-id", "fake-user-name", "requesting-member", 
				"providing-member", startTime, duration);
		
		// begin interested time: 31/07/2018 23:50:00
		Timestamp begin = new Timestamp(new Long("1533091800000"));
		
		// end interested time: 31/07/2018 23:55:00
		Timestamp end = new Timestamp(new Long("1533092100000"));
	
		
		// interest period is 5 min, but the order never was in fulfilled state in this period, so the time is 0.
		long expectedDuration = 0;
		
		// exercise	
		long realDuration = resourceUsageService.getRealDuration(entry, begin, end);

		// verify
		Assert.assertEquals(expectedDuration, realDuration);			
	}
	
	// test case: The begin of interested time is after finish time of order
	@Test
	public void testDurationInterestedBeginAfterFinishTimeOfOrder() {
		
		// set up
		
		// Start time of order: 01/08/2018 00:00:00
		Timestamp startTime = new Timestamp(new Long("1533092400000"));
		
		// duration: 30 min. So, the end time of order is: 01/08/2018 00:30:00
		int duration = 1800000;
		
		TimestampTableEntry entry = new TimestampTableEntry("fake-order-id", "COMPUTE", 
				"2/4", "fake-user-id", "fake-user-name", "requesting-member", 
				"providing-member", startTime, duration);
		
		// begin interested time: 01/08/2018 00:30:00
		Timestamp begin = new Timestamp(new Long("1533094200000"));
		
		// end interested time: 01/08/2018 00:50:00
		Timestamp end = new Timestamp(new Long("1533095400000"));
	
		
		// interest period is 20 min, but the order was in closed state in this period, so the time is 0.
		long expectedDuration = 0;
		
		// exercise	
		long realDuration = resourceUsageService.getRealDuration(entry, begin, end);

		// verify
		Assert.assertEquals(expectedDuration, realDuration);
			
	}
	
	
	
	
	

}
