package fogbow.billing.services;

import java.util.HashMap;
import java.util.Map;

import org.fogbowcloud.manager.core.ApplicationFacade;
import org.fogbowcloud.manager.core.datastore.DatabaseManager;
import org.fogbowcloud.manager.core.models.linkedlists.SynchronizedDoublyLinkedList;
import org.fogbowcloud.manager.core.models.orders.ComputeOrder;
import org.fogbowcloud.manager.core.models.orders.Order;
import org.fogbowcloud.manager.core.models.orders.OrderState;
import org.springframework.stereotype.Service;

@Service
public class BillingService {
	
	private static BillingService instance;
	
	public static final double RAM_PRICE = 1;
	public static final double CPU_PRICE = 1;
	public static final double DISK_PRICE = 1;
	
	private BillingService() {}

    public static BillingService getInstance() {
        synchronized (ApplicationFacade.class) {
            if (instance == null) {
                instance = new BillingService();
            }
            return instance;
        }
    }
	
	public Map<String, Double> getUserBilling(String federationUserId){
		
		SynchronizedDoublyLinkedList allOrders = DatabaseManager.getInstance().readActiveOrders(OrderState.OPEN);
		Map<String, Double> userReport = new HashMap<>();
		
		double cpuUsed = 0;
		double ramUsed = 0;
		double diskUsed = 0;
		double totalPayable = 0;
		
		Order order = allOrders.getNext();
				
		while (order != null) {
			
			if (order.getFederationUser().getId() == federationUserId) {
				ComputeOrder computeOrder = (ComputeOrder) order;
				
				cpuUsed += computeOrder.getvCPU();
				ramUsed += computeOrder.getMemory();
				diskUsed += computeOrder.getDisk();
			}
		}
		
		totalPayable += (cpuUsed * CPU_PRICE) + (ramUsed * RAM_PRICE) + (diskUsed * DISK_PRICE);
		
		userReport.put("CPU", cpuUsed);
		userReport.put("RAM", ramUsed);
		userReport.put("DISK", diskUsed);
		userReport.put("TOTAL (R$):", totalPayable);
		
		return userReport;
	}

}
