package fogbow.billing.services;
import org.springframework.stereotype.Service;

import fogbow.billing.datastore.AuthDataStore;

@Service
public class AuthorizationDirectoryServiceImpl implements AuthorizationDirectoryService {
	
	private AuthDataStore authDataStore;
	
	private static AuthorizationDirectoryServiceImpl instance;
	
	private AuthorizationDirectoryServiceImpl() {
		this.authDataStore = new AuthDataStore();
	}

    public static AuthorizationDirectoryServiceImpl getInstance() {
        synchronized (AuthorizationDirectoryServiceImpl.class) {
            if (instance == null) {
                instance = new AuthorizationDirectoryServiceImpl();
            }
            return instance;
        }
    }
    
    @Override
    public boolean getUserAuthorization(String userId, String resourceType, String operation) {
    	int response = this.getAuthDataStore().getUserAuthorization(userId, resourceType, operation);
    	
    	if (response == 1) {
    		return true;
    	}
    	return false;
    }
    
    @Override
    public boolean unauthorizeUser(String userId, String resourceType, String operation) {
    	if (!getUserAuthorization(userId, resourceType, operation)) {
    		return false;
    	}
    	return getAuthDataStore().unauthorizeUser(userId);
    }
    
    @Override
    public boolean authorizeUser(String userId, String resourceType, String operation) {
    	if (getUserAuthorization(userId, resourceType, operation)) {
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
