package fogbow.billing.services;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.fogbowcloud.ras.core.ApplicationFacade;
import org.fogbowcloud.ras.core.exceptions.UnexpectedException;
import org.fogbowcloud.ras.core.models.ResourceType;

import fogbow.billing.datastore.ResourceUsageDataStore;
import fogbow.billing.model.ComputeUsage;
import fogbow.billing.model.OrderRecord;
import fogbow.billing.model.Usage;
import fogbow.billing.model.VolumeUsage;

public class ResourceUsageService {
	
	private ResourceUsageDataStore resourceUsageDataStore;
	
	private static final String pattern = "yyyy-MM-dd";
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
	
	private static ResourceUsageService instance;
	
	private ResourceUsageService() throws SQLException {
		this.resourceUsageDataStore = new ResourceUsageDataStore();
	}

    public static ResourceUsageService getInstance() throws SQLException {
        synchronized (ApplicationFacade.class) {
            if (instance == null) {
                instance = new ResourceUsageService();
            }
            return instance;
        }
    }
    
    public List<ComputeUsage> getUserComputeUsage(String userId, String beginPeriod, String endPeriod) throws ParseException{
    	
    	Date initalDate = simpleDateFormat.parse(beginPeriod);
    	Timestamp begin = new Timestamp(initalDate.getTime());
    	
    	Date finalDate = simpleDateFormat.parse(endPeriod);
    	Timestamp end = new Timestamp(finalDate.getTime());
    	
    	List<ComputeUsage> computeUsageList = new ArrayList<>();
    	
    	List<OrderRecord> listOfComputeOrders = this.resourceUsageDataStore.getOrdersByUserIdAndResourceType(userId, ResourceType.COMPUTE);
    	
    	for (OrderRecord entry: listOfComputeOrders) {
    		
    		String orderId = entry.getOrderId();
    		String fedUserId = entry.getUserId();
    		String usage = entry.getUsage();
    		
    		String[] resourceUsage = usage.split("/");
    		int cpu = Integer.valueOf(resourceUsage[0]);
    		int ram = Integer.valueOf(resourceUsage[1]);
    		long realDuration = getRealDuration(entry, begin, end);
    		
    		ComputeUsage computeUsage = new ComputeUsage(orderId, fedUserId, begin, end, realDuration, cpu, ram);
    		
    		computeUsageList.add(computeUsage);
    		
    	}
    	
    	return computeUsageList;
    }
    
    public List<VolumeUsage> getUserVolumeUsage(String userId, String beginPeriod, String endPeriod) throws ParseException{
    	
    	Date initalDate = simpleDateFormat.parse(beginPeriod);
    	Timestamp begin = new Timestamp(initalDate.getTime());
    	
    	Date finalDate = simpleDateFormat.parse(endPeriod);
    	Timestamp end = new Timestamp(finalDate.getTime());
    	
    	List<VolumeUsage> volumeUsageList = new ArrayList<>();
    	
    	List<OrderRecord> listOfVolumeOrders = this.resourceUsageDataStore.getOrdersByUserIdAndResourceType(userId, ResourceType.VOLUME);
    	
    	for (OrderRecord entry: listOfVolumeOrders) {
    		
    		String orderId = entry.getOrderId();
    		String fedUserId = entry.getUserId();
    		String usage = entry.getUsage();
    	
    		int volumeSize = Integer.valueOf(usage);
    		long realDuration = getRealDuration(entry, begin, end);
    		
    		VolumeUsage volumeUsage = new VolumeUsage(orderId, fedUserId, begin, end, realDuration, volumeSize);
    		
    		volumeUsageList.add(volumeUsage);
    		
    	}
    	
    	return volumeUsageList;
    }
    
    public List<Usage> getUserUsage(String userId, String beginPeriod, String endPeriod) throws UnexpectedException, ParseException{
    	
    	Date initalDate = simpleDateFormat.parse(beginPeriod);
    	Timestamp begin = new Timestamp(initalDate.getTime());
    	
    	Date finalDate = simpleDateFormat.parse(endPeriod);
    	Timestamp end = new Timestamp(finalDate.getTime());
    	
    	List<Usage> usageList = new ArrayList<>();
    	
    	List<OrderRecord> listOfRecords = this.resourceUsageDataStore.getOrdersByUserId(userId);
    	
    	for (OrderRecord record: listOfRecords) {	
    		Usage usage = processOrderRecord(record, begin, end);
    		usageList.add(usage);
    		
    	} 	
    	return usageList;
    }
    
    private Usage processOrderRecord(OrderRecord record, Timestamp begin, Timestamp end) throws UnexpectedException {
    	
    	Usage returnUsage;
    	String orderId = record.getOrderId();
		String fedUserId = record.getUserId();
		String usage = record.getUsage();

		long realDuration = getRealDuration(record, begin, end);
		System.out.println(record.getResourceType());
		if (record.getResourceType().equals(String.valueOf(ResourceType.COMPUTE))) {
			String[] resourceUsage = usage.split("/");
    		int cpu = Integer.valueOf(resourceUsage[0]);
    		int ram = Integer.valueOf(resourceUsage[1]);
    		returnUsage = new ComputeUsage(orderId, fedUserId, begin, end, realDuration, cpu, ram);
		
		} else if (record.getResourceType().equals(String.valueOf(ResourceType.VOLUME))) {
			int volumeSize = Integer.valueOf(usage);
			returnUsage = new VolumeUsage(orderId, fedUserId, begin, end, realDuration, volumeSize);
		} else {
			throw new UnexpectedException("This type of resource is not ready to be accounted");
		}
		
		return returnUsage;
		
	}

	protected long getRealDuration(OrderRecord entry, Timestamp begin, Timestamp end) {
    	
    	long duration = entry.getDuration();
		
		if (entry.getStart_time().before(begin)) {
			duration -= begin.getTime() - entry.getStart_time().getTime();
		}
		
		if (end.getTime() < entry.getStart_time().getTime() + entry.getDuration()) {
			duration -= (entry.getStart_time().getTime() + entry.getDuration()) - end.getTime();		
		}
		
		return Math.max(0,duration);
    }
    
    protected void setResourceUsageDataStore(ResourceUsageDataStore dataStore) {
    	this.resourceUsageDataStore = dataStore;
    }

}
