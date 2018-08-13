package fogbow.billing.services;

import org.fogbowcloud.manager.core.ApplicationFacade;

import fogbow.billing.datastore.ResourceUsageDataStore;

public class ResourceUsageService {
	
	private ResourceUsageDataStore resourceUsageDataStore;
	
	private static ResourceUsageService instance;
	
	private ResourceUsageService() {
		this.resourceUsageDataStore = new ResourceUsageDataStore();
	}

    public static ResourceUsageService getInstance() {
        synchronized (ApplicationFacade.class) {
            if (instance == null) {
                instance = new ResourceUsageService();
            }
            return instance;
        }
    }

}
