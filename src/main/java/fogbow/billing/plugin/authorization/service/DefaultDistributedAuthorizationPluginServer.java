package fogbow.billing.plugin.authorization.service;
import org.springframework.stereotype.Service;

import fogbow.billing.datastore.AuthDataStore;

@Service
public class DefaultDistributedAuthorizationPluginServer implements DistributedAuthorizationPluginServerInterface {
	
	private AuthDataStore authDataStore;
	
	private static DefaultDistributedAuthorizationPluginServer instance;
	
	private DefaultDistributedAuthorizationPluginServer() {
		this.authDataStore = new AuthDataStore();
	}

    public static DefaultDistributedAuthorizationPluginServer getInstance() {
        synchronized (DefaultDistributedAuthorizationPluginServer.class) {
            if (instance == null) {
                instance = new DefaultDistributedAuthorizationPluginServer();
            }
            return instance;
        }
    }
    
    @Override
    public boolean getUserAuthorization(String tokenProvider, String userId, String resourceType, String operation) {
    	int response = this.getAuthDataStore().getUserAuthorization(userId, resourceType, operation);
    	
    	if (response == 1) {
    		return true;
    	}
    	return false;
    }
    
    @Override
    public boolean unauthorizeUser(String tokenProvider, String userId, String resourceType, String operation) {
    	if (!getUserAuthorization(tokenProvider, userId, resourceType, operation)) {
    		return false;
    	}
    	return getAuthDataStore().unauthorizeUser(userId);
    }
    
    @Override
    public boolean authorizeUser(String tokenProvider, String userId, String resourceType, String operation) {
    	if (getUserAuthorization(tokenProvider, userId, resourceType, operation)) {
    		return false;
    	}
    	
    	return getAuthDataStore().authorizeUser(userId);
    }
    
    public boolean addUser(String userId) {
    	return getAuthDataStore().addUser(userId);
    }
    
    public AuthDataStore getAuthDataStore() {
    	return this.authDataStore;
    }
}
