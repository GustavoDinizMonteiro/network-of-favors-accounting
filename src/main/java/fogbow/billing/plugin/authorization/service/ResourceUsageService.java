package fogbow.billing.plugin.authorization.service;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.fogbowcloud.ras.core.ApplicationFacade;

import fogbow.billing.datastore.ResourceUsageDataStore;
import fogbow.billing.model.OrderRecord;
import fogbow.billing.model.Usage;

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
    
    public List<Usage> getUsage(String userId, String requestingMember, String providingMember,
			String resourceType, String beginPeriod, String endPeriod) throws ParseException{
    	
    	Date initalDate = simpleDateFormat.parse(beginPeriod);
    	Timestamp begin = new Timestamp(initalDate.getTime());
    	
    	Date finalDate = simpleDateFormat.parse(endPeriod);
    	Timestamp end = new Timestamp(finalDate.getTime());
    	
    	List<Usage> usageList = new ArrayList<>();
    	
    	List<OrderRecord> listOfRecords = this.resourceUsageDataStore.getOrders(userId, requestingMember,
    			providingMember, resourceType);
    	
    	for (OrderRecord record: listOfRecords) {	
    		Usage usage = processOrderRecord(record, begin, end);
    		
    		if (usage.getDuration() > 0) {
        		usageList.add(usage);
    		}		
    	} 	
    	return usageList;
    }
    
    private Usage processOrderRecord(OrderRecord record, Timestamp begin, Timestamp end) {

		long realDuration = getRealDuration(record, begin, end);
		
		return new Usage(record.getOrderId(), record.getUserId(), record.getUserName(), record.getRequestingMember(),
				record.getProvidingMember(), record.getResourceType(), record.getSpec(), begin, end, realDuration);
		
	}

	protected long getRealDuration(OrderRecord record, Timestamp begin, Timestamp end) {
    	
    	long currentDuration = record.getDuration();
    	
    	// Order not closed, so the current duration of order is now - start_time
    	if (currentDuration == ResourceUsageDataStore.ORDER_NOT_CLOSED_FLAG) {
    		long now = new Date().getTime();
    		currentDuration = now - record.getStart_time().getTime();
    	}
    	
    	long newDuration = currentDuration;
		
    	// Calculates the real duration, i.e when the order was fulfilled in interest interval.
		if (record.getStart_time().before(begin)) {
			newDuration -= begin.getTime() - record.getStart_time().getTime();
		}
		
		if (end.getTime() < record.getStart_time().getTime() + currentDuration) {
			newDuration -= (record.getStart_time().getTime() + currentDuration) - end.getTime();		
		}
		
		return Math.max(0,newDuration);
    }
    
    protected void setResourceUsageDataStore(ResourceUsageDataStore dataStore) {
    	this.resourceUsageDataStore = dataStore;
    }

}
