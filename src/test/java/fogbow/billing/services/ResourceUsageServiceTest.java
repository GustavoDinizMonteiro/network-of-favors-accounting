package fogbow.billing.services;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.fogbowcloud.ras.core.exceptions.UnexpectedException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import fogbow.billing.datastore.ResourceUsageDataStore;
import fogbow.billing.model.OrderRecord;
import fogbow.billing.model.Usage;
import fogbow.billing.services.ResourceUsageService;


public class ResourceUsageServiceTest {
	
	private static final String FAKE_ORDER_ID = "fake-order-id";
	private static final String FAKE_USER_ID = "fake-user-id";
	private static final String FAKE_USER_NAME = "fake-user-name";
	private static final String FAKE_REQUESTING_MEMBER = "fake-requesting-member";
	private static final String FAKE_PROVIDING_MEMBER = "fake-providing-member";
	private static final String COMPUTE_RESOURCE = "COMPUTE";
	
	// It represents 2 vCPU and 4 GB RAM
	private static final String COMPUTE_SPEC = "2/4";
	
	private static final String pattern = "yyyy-MM-dd";
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
	
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
		
		OrderRecord entry = new OrderRecord(FAKE_ORDER_ID, COMPUTE_RESOURCE, 
				COMPUTE_SPEC, FAKE_USER_ID, FAKE_USER_NAME, FAKE_REQUESTING_MEMBER, 
				FAKE_PROVIDING_MEMBER, startTime, duration);
		
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
		
		OrderRecord entry = new OrderRecord(FAKE_ORDER_ID, COMPUTE_RESOURCE, 
				COMPUTE_SPEC, FAKE_USER_ID, FAKE_USER_NAME, FAKE_REQUESTING_MEMBER, 
				FAKE_PROVIDING_MEMBER, startTime, duration);
		
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
		
		OrderRecord entry = new OrderRecord(FAKE_ORDER_ID, COMPUTE_RESOURCE, 
				COMPUTE_SPEC, FAKE_USER_ID, FAKE_USER_NAME, FAKE_REQUESTING_MEMBER, 
				FAKE_PROVIDING_MEMBER, startTime, duration);
		
		// begin interested time: 01/08/2018 00:15:00
		Timestamp begin = new Timestamp(new Long("1533093300000"));
		
		// end interested time: 01/08/2018 00:20:00 
		Timestamp end = new Timestamp(new Long("1533093600000"));
		
		// interest period is 5 min
		long expectedDuration = 300000;
		
		// exercise	
		long realDuration = resourceUsageService.getRealDuration(entry, begin, end);

		// verify. The interested period is 5 min between the start and the finish of order, so 5 min is the duration
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
		
		OrderRecord entry = new OrderRecord(FAKE_ORDER_ID, COMPUTE_RESOURCE, 
				COMPUTE_SPEC, FAKE_USER_ID, FAKE_USER_NAME, FAKE_REQUESTING_MEMBER, 
				FAKE_PROVIDING_MEMBER, startTime, duration);
		
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
		
		OrderRecord entry = new OrderRecord(FAKE_ORDER_ID, COMPUTE_RESOURCE, 
				COMPUTE_SPEC, FAKE_USER_ID, FAKE_USER_NAME, FAKE_REQUESTING_MEMBER, 
				FAKE_PROVIDING_MEMBER, startTime, duration);
		
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
		
		OrderRecord entry = new OrderRecord(FAKE_ORDER_ID, COMPUTE_RESOURCE, 
				COMPUTE_SPEC, FAKE_USER_ID, FAKE_USER_NAME, FAKE_REQUESTING_MEMBER, 
				FAKE_PROVIDING_MEMBER, startTime, duration);
		
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
		
		OrderRecord entry = new OrderRecord(FAKE_ORDER_ID, COMPUTE_RESOURCE, 
				COMPUTE_SPEC, FAKE_USER_ID, FAKE_USER_NAME, FAKE_REQUESTING_MEMBER, 
				FAKE_PROVIDING_MEMBER, startTime, duration);
		
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
	
	@Test
	public void testRealDurationWhenOrderIsNotClosed() {
		
		// set up
		
		// Start time of order: 01/08/2018 00:00:00
		Timestamp startTime = new Timestamp(new Long("1533092400000"));
		
		// duration: order not closed
		int duration = ResourceUsageDataStore.ORDER_NOT_CLOSED_FLAG;
		
		OrderRecord entry = new OrderRecord(FAKE_ORDER_ID, COMPUTE_RESOURCE, 
				COMPUTE_SPEC, FAKE_USER_ID, FAKE_USER_NAME, FAKE_REQUESTING_MEMBER, 
				FAKE_PROVIDING_MEMBER, startTime, duration);
		
		// begin interested time: 01/08/2018 00:00:00
		Timestamp begin = new Timestamp(new Long("1533092400000"));
		
		// end interested time: 01/08/2018 00:50:00
		Timestamp end = new Timestamp(new Long("1533095400000"));
	
		
		// interest period is 50 min (00:00 to 00:50).
		long expectedDuration = 3000000;
		
		// exercise	
		long realDuration = resourceUsageService.getRealDuration(entry, begin, end);

		// verify
		Assert.assertEquals(expectedDuration, realDuration);
			
	}
	
	// test case: Test get usage of all type of resources
	@Test
	public void testGetUsage() throws UnexpectedException, ParseException {
		
		// set up
		
		// Start time of order: 01/08/2018 00:00:00
		Timestamp startTime = new Timestamp(new Long("1533092400000"));
				
		// duration: 2 days. So, the end time of order is: 03/08/2018 00:00:00
		int duration = 172800000;
						
		// begin interested time: 01/08/2018 00:00:00 
		String begin = "2018-08-01";
		
		// end interested time: 04/08/2018 00:00:00 
		String end = "2018-08-04";
		
		// set up
		OrderRecord computeOrderRecord1 = new OrderRecord(FAKE_ORDER_ID, COMPUTE_RESOURCE, COMPUTE_SPEC,
						FAKE_USER_ID, FAKE_USER_NAME, FAKE_REQUESTING_MEMBER,
						FAKE_PROVIDING_MEMBER, startTime, duration);
		
		List<OrderRecord> orderRecords = new ArrayList<>();
		orderRecords.add(computeOrderRecord1);
		
		Mockito.when(resourceUsageDataStore.getOrders(FAKE_USER_ID, FAKE_REQUESTING_MEMBER, FAKE_PROVIDING_MEMBER, COMPUTE_RESOURCE)).thenReturn(orderRecords);
		
		// exercise
		List<Usage> usageResult = resourceUsageService.getUsage(FAKE_USER_ID, FAKE_REQUESTING_MEMBER, FAKE_PROVIDING_MEMBER, COMPUTE_RESOURCE, begin, end);
		
		// verify usage
		Usage usage = usageResult.get(0);
		long expectedDuration = 172800000;
		
		Assert.assertEquals(FAKE_ORDER_ID, usage.getOrderId());
		Assert.assertEquals(FAKE_USER_ID, usage.getUserId());
		Assert.assertEquals(simpleDateFormat.parse(begin).getTime(), usage.getBegin().getTime());
		Assert.assertEquals(simpleDateFormat.parse(end).getTime(), usage.getEnd().getTime());
		Assert.assertEquals(expectedDuration, usage.getDuration());
		Assert.assertEquals(FAKE_REQUESTING_MEMBER, usage.getRequestingMember());
		Assert.assertEquals(FAKE_PROVIDING_MEMBER, usage.getProvidingMember());
		Assert.assertEquals(COMPUTE_RESOURCE, usage.getResourceType());
		Assert.assertEquals(COMPUTE_SPEC, usage.getSpec());
		Assert.assertEquals(FAKE_USER_ID, usage.getUserId());
	}
	
	// test case: Test case filtering order with duration equals to 0
	public void testFilteringUsagesWithDurationEqualsTo0() throws UnexpectedException, ParseException {
		// set up
		
		// Start time of order: 01/08/2018 00:00:00
		Timestamp startTime = new Timestamp(new Long("1533092400000"));
				
		// duration: 0. To force the filter
		int duration = 0;
						
		// begin interested time: 01/08/2018 00:00:00 
		String begin = "2018-08-01";
		
		// end interested time: 04/08/2018 00:00:00 
		String end = "2018-08-04";
		
		// set up
		OrderRecord computeOrderRecord1 = new OrderRecord(FAKE_ORDER_ID, COMPUTE_RESOURCE, COMPUTE_SPEC,
						FAKE_USER_ID, FAKE_USER_NAME, FAKE_REQUESTING_MEMBER,
						FAKE_PROVIDING_MEMBER, startTime, duration);
		
		List<OrderRecord> orderRecords = new ArrayList<>();
		orderRecords.add(computeOrderRecord1);
		
		Mockito.when(resourceUsageDataStore.getOrders(FAKE_USER_ID, FAKE_REQUESTING_MEMBER, FAKE_PROVIDING_MEMBER, COMPUTE_RESOURCE)).thenReturn(orderRecords);
		
		// exercise
		List<Usage> usageResult = resourceUsageService.getUsage(FAKE_USER_ID, FAKE_REQUESTING_MEMBER, FAKE_PROVIDING_MEMBER, COMPUTE_RESOURCE, begin, end);
		
		// verify usage
		Assert.assertTrue(usageResult.isEmpty());
	}
}
