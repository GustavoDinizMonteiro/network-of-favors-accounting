package fogbow.billing.services;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.fogbowcloud.ras.core.exceptions.UnexpectedException;
import org.fogbowcloud.ras.core.models.ResourceType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import fogbow.billing.datastore.ResourceUsageDataStore;
import fogbow.billing.model.ComputeUsage;
import fogbow.billing.model.OrderRecord;
import fogbow.billing.model.Usage;
import fogbow.billing.model.VolumeUsage;
import fogbow.billing.services.ResourceUsageService;


public class ResourceUsageServiceTest {
	
	private static final String FAKE_ORDER_ID = "fake-order-id";
	private static final String FAKE_USER_ID = "fake-user-id";
	private static final String FAKE_USER_NAME = "fake-user-name";
	private static final String FAKE_REQUESTING_MEMBER = "fake-requesting-member";
	private static final String FAKE_PROVIDING_MEMBER = "fake-providing-member";
	private static final String COMPUTE_RESOURCE = "compute";
	private static final String VOLUME_RESOURCE = "volume";
	private static final String NOT_ACCOUNTED_RESOURCE = "unknown resource";
	
	// It represents 2 vCPU and 4 GB RAM
	private static final String COMPUTE_USAGE = "2/4";
    
	// It represents 100 GB
	private static final String VOLUME_USAGE = "100";
	
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
				COMPUTE_USAGE, FAKE_USER_ID, FAKE_USER_NAME, FAKE_REQUESTING_MEMBER, 
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
				COMPUTE_USAGE, FAKE_USER_ID, FAKE_USER_NAME, FAKE_REQUESTING_MEMBER, 
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
				COMPUTE_USAGE, FAKE_USER_ID, FAKE_USER_NAME, FAKE_REQUESTING_MEMBER, 
				FAKE_PROVIDING_MEMBER, startTime, duration);
		
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
		
		OrderRecord entry = new OrderRecord(FAKE_ORDER_ID, COMPUTE_RESOURCE, 
				COMPUTE_USAGE, FAKE_USER_ID, FAKE_USER_NAME, FAKE_REQUESTING_MEMBER, 
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
				COMPUTE_USAGE, FAKE_USER_ID, FAKE_USER_NAME, FAKE_REQUESTING_MEMBER, 
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
				COMPUTE_USAGE, FAKE_USER_ID, FAKE_USER_NAME, FAKE_REQUESTING_MEMBER, 
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
				COMPUTE_USAGE, FAKE_USER_ID, FAKE_USER_NAME, FAKE_REQUESTING_MEMBER, 
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
	public void testVolumeUsage() throws ParseException {
		
		// Start time of order: 01/08/2018 00:00:00
		Timestamp startTime = new Timestamp(new Long("1533092400000"));
		
		// duration: 2 days. So, the end time of order is: 03/08/2018 00:00:00
		int duration = 172800000;
				
		// begin interested time: 01/08/2018 00:00:00 
		//Timestamp begin = new Timestamp(new Long("1533092400000"));
		String begin = "2018-08-01";
				
		// end interested time: 04/08/2018 00:00:00 
		//Timestamp end = new Timestamp(new Long("1533095400000"));
		String end = "2018-08-04";
		
		// set up
		OrderRecord volumeOrderRecord1 = new OrderRecord(FAKE_ORDER_ID, VOLUME_RESOURCE, VOLUME_USAGE,
				FAKE_USER_ID, FAKE_USER_NAME, FAKE_REQUESTING_MEMBER,
				FAKE_PROVIDING_MEMBER, startTime, duration);
		
		List<OrderRecord> volumeOrderRecords = new ArrayList<>();
		volumeOrderRecords.add(volumeOrderRecord1);
		

		Mockito.when(resourceUsageDataStore.getOrdersByUserIdAndResourceType(FAKE_USER_ID, ResourceType.VOLUME)).thenReturn(volumeOrderRecords);
		
		
		// exercise
		List<VolumeUsage> usageResult = resourceUsageService.getUserVolumeUsage(FAKE_USER_ID, begin, end);
		
		
		// verify
		Assert.assertEquals(1, usageResult.size());
		
		//2 days
		long expectedDuration = 172800000;
		int expectedVolumeSize = 100;
		
		VolumeUsage usage = usageResult.get(0);
		Assert.assertEquals(FAKE_ORDER_ID, usage.getOrderId());
		Assert.assertEquals(FAKE_USER_ID, usage.getUserId());
		Assert.assertEquals(simpleDateFormat.parse(begin).getTime(), usage.getBegin().getTime());
		Assert.assertEquals(simpleDateFormat.parse(end).getTime(), usage.getEnd().getTime());
		Assert.assertEquals(expectedDuration, usage.getDuration());
		Assert.assertEquals(expectedVolumeSize, usage.getVolumeSize());
		
	}
	
	@Test
	public void testComputeUsage() throws ParseException {
		
		// Start time of order: 01/08/2018 00:00:00
		Timestamp startTime = new Timestamp(new Long("1533092400000"));
		
		// duration: 2 days. So, the end time of order is: 03/08/2018 00:00:00
		int duration = 172800000;
				
		// begin interested time: 01/08/2018 00:00:00 
		//Timestamp begin = new Timestamp(new Long("1533092400000"));
		String begin = "2018-08-01";
		
		// end interested time: 04/08/2018 00:00:00 
		//Timestamp end = new Timestamp(new Long("1533095400000"));
		String end = "2018-08-04";
		
		// set up
		OrderRecord computeOrderRecord1 = new OrderRecord(FAKE_ORDER_ID, COMPUTE_RESOURCE, COMPUTE_USAGE,
				FAKE_USER_ID, FAKE_USER_NAME, FAKE_REQUESTING_MEMBER,
				FAKE_PROVIDING_MEMBER, startTime, duration);
		
		List<OrderRecord> computeOrderRecords = new ArrayList<>();
		computeOrderRecords.add(computeOrderRecord1);
		

		Mockito.when(resourceUsageDataStore.getOrdersByUserIdAndResourceType(FAKE_USER_ID, ResourceType.COMPUTE)).thenReturn(computeOrderRecords);
		
		
		// execise
		List<ComputeUsage> usageResult = resourceUsageService.getUserComputeUsage(FAKE_USER_ID, begin, end);
		
		
		// verify
		Assert.assertEquals(1, usageResult.size());
		
		//30 min
		long expectedDuration = 172800000;
		int expectedCPU = 2;
		int expectedRAM = 4;
		
		ComputeUsage usage = usageResult.get(0);
		Assert.assertEquals(FAKE_ORDER_ID, usage.getOrderId());
		Assert.assertEquals(FAKE_USER_ID, usage.getUserId());
		Assert.assertEquals(simpleDateFormat.parse(begin).getTime(), usage.getBegin().getTime());
		Assert.assertEquals(simpleDateFormat.parse(end).getTime(), usage.getEnd().getTime());
		Assert.assertEquals(expectedDuration, usage.getDuration());
		Assert.assertEquals(expectedCPU, usage.getCpu());
		Assert.assertEquals(expectedRAM, usage.getRam());
		
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
		//Timestamp begin = new Timestamp(new Long("1533092400000"));
		String begin = "2018-08-01";
		
		// end interested time: 04/08/2018 00:00:00 
		//Timestamp end = new Timestamp(new Long("1533095400000"));
		String end = "2018-08-04";
		
		// set up
		OrderRecord computeOrderRecord1 = new OrderRecord(FAKE_ORDER_ID, COMPUTE_RESOURCE, COMPUTE_USAGE,
						FAKE_USER_ID, FAKE_USER_NAME, FAKE_REQUESTING_MEMBER,
						FAKE_PROVIDING_MEMBER, startTime, duration);
				

				
		OrderRecord volumeOrderRecord1 = new OrderRecord(FAKE_ORDER_ID, VOLUME_RESOURCE, VOLUME_USAGE,
						FAKE_USER_ID, FAKE_USER_NAME, FAKE_REQUESTING_MEMBER,
						FAKE_PROVIDING_MEMBER, startTime, duration);
		
		List<OrderRecord> orderRecords = new ArrayList<>();
		orderRecords.add(computeOrderRecord1);
		orderRecords.add(volumeOrderRecord1);
		
		Mockito.when(resourceUsageDataStore.getOrdersByUserId(FAKE_USER_ID)).thenReturn(orderRecords);
		
		// exercise
		List<Usage> usageResult = resourceUsageService.getUserUsage(FAKE_USER_ID, begin, end);
		
		// verify compute usage
		ComputeUsage computeUsage = (ComputeUsage) usageResult.get(0);
		long expectedDuration = 172800000;
		int expectedCPU = 2;
		int expectedRAM = 4;
		
		Assert.assertEquals(FAKE_ORDER_ID, computeUsage.getOrderId());
		Assert.assertEquals(FAKE_USER_ID, computeUsage.getUserId());
		Assert.assertEquals(simpleDateFormat.parse(begin).getTime(), computeUsage.getBegin().getTime());
		Assert.assertEquals(simpleDateFormat.parse(end).getTime(), computeUsage.getEnd().getTime());
		Assert.assertEquals(expectedDuration, computeUsage.getDuration());
		Assert.assertEquals(expectedCPU, computeUsage.getCpu());
		Assert.assertEquals(expectedRAM, computeUsage.getRam());
		
		
		// verify volume usage
		VolumeUsage volumeUsage = (VolumeUsage) usageResult.get(1);
		int expectedVolumeSize = 100;
		
		Assert.assertEquals(FAKE_ORDER_ID, volumeUsage.getOrderId());
		Assert.assertEquals(FAKE_USER_ID, volumeUsage.getUserId());
		Assert.assertEquals(simpleDateFormat.parse(begin).getTime(), volumeUsage.getBegin().getTime());
		Assert.assertEquals(simpleDateFormat.parse(end).getTime(), volumeUsage.getEnd().getTime());
		Assert.assertEquals(expectedDuration, volumeUsage.getDuration());
		Assert.assertEquals(expectedVolumeSize, volumeUsage.getVolumeSize());
	}
	
	// test case: Test case: 
	@Test(expected = UnexpectedException.class)
	public void testGetUsageException() throws UnexpectedException, ParseException {
		
		// set up
		// Start time of order: 01/08/2018 00:00:00
		Timestamp startTime = new Timestamp(new Long("1533092400000"));
			
		// duration: 2 days. So, the end time of order is: 03/08/2018 00:00:00
		int duration = 172800000;
				
		// begin interested time: 01/08/2018 00:00:00 
		//Timestamp begin = new Timestamp(new Long("1533092400000"));
		String begin = "2018-08-01";
		
		// end interested time: 04/08/2018 00:00:00 
		//Timestamp end = new Timestamp(new Long("1533095400000"));
		String end = "2018-08-04";
			
		// set up
		OrderRecord orderRecord = new OrderRecord(FAKE_ORDER_ID, NOT_ACCOUNTED_RESOURCE, COMPUTE_USAGE,
				FAKE_USER_ID, FAKE_USER_NAME, FAKE_REQUESTING_MEMBER,
				FAKE_PROVIDING_MEMBER, startTime, duration);
				
		List<OrderRecord> orderRecords = new ArrayList<>();
		orderRecords.add(orderRecord);
		
		// set up
		Mockito.when(resourceUsageDataStore.getOrdersByUserId(FAKE_USER_ID)).thenReturn(orderRecords);
		
		// exercise
		resourceUsageService.getUserUsage(FAKE_USER_ID, begin, end);
	}
	
	
	
	
	

}
