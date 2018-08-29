package fogbow.billing.services;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.fogbowcloud.ras.core.ApplicationFacade;
import org.fogbowcloud.ras.core.models.ResourceType;

import fogbow.billing.datastore.ResourceUsageDataStore;
import fogbow.billing.model.ComputeUsage;
import fogbow.billing.model.TimestampTableEntry;
import fogbow.billing.model.VolumeUsage;

public class ResourceUsageService {
	
	private ResourceUsageDataStore resourceUsageDataStore;
	
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
    
    public List<ComputeUsage> getUserComputeUsage(String userId, Timestamp begin, Timestamp end){
    	
    	List<ComputeUsage> computeUsageList = new ArrayList<>();
    	
    	List<TimestampTableEntry> listOfComputeOrders = this.resourceUsageDataStore.getOrdersByUserIdAndResourceType(userId, ResourceType.COMPUTE);
    	
    	for (TimestampTableEntry entry: listOfComputeOrders) {
    		
    		String orderId = entry.getOrderId();
    		String fedUserId = entry.getUserId();
    		String usage = entry.getUsage();
    		
    		String[] resourceUsage = usage.split("/");
    		int cpu = Integer.valueOf(resourceUsage[0]);
    		int ram = Integer.valueOf(resourceUsage[1]);
    		long realDuration = getRealDuration(entry, begin, end);
    		
    		ComputeUsage computeUsage = new ComputeUsage(orderId, fedUserId, realDuration, cpu, ram);
    		
    		computeUsageList.add(computeUsage);
    		
    	}
    	
    	return computeUsageList;
    }
    
    public List<VolumeUsage> getUserVolumeUsage(String userId, Timestamp begin, Timestamp end){
    	
    	List<VolumeUsage> volumeUsageList = new ArrayList<>();
    	
    	List<TimestampTableEntry> listOfVolumeOrders = this.resourceUsageDataStore.getOrdersByUserIdAndResourceType(userId, ResourceType.VOLUME);
    	
    	for (TimestampTableEntry entry: listOfVolumeOrders) {
    		
    		String orderId = entry.getOrderId();
    		String fedUserId = entry.getUserId();
    		String usage = entry.getUsage();
    	
    		int volumeSize = Integer.valueOf(usage);
    		long realDuration = getRealDuration(entry, begin, end);
    		
    		VolumeUsage volumeUsage = new VolumeUsage(orderId, fedUserId, realDuration, volumeSize);
    		
    		volumeUsageList.add(volumeUsage);
    		
    	}
    	
    	return volumeUsageList;
    }
    
    protected long getRealDuration(TimestampTableEntry entry, Timestamp begin, Timestamp end) {
    	
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
