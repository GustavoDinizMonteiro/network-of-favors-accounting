package fogbow.billing.services;

import java.sql.SQLException;

import org.fogbowcloud.ras.core.ApplicationFacade;

import fogbow.billing.datastore.ResourceUsageDataStore;

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

}
