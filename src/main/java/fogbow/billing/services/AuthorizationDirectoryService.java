package fogbow.billing.services;

public interface AuthorizationDirectoryService {
	
	public boolean getUserAuthorization(String userId, String resourceType, String operation);
	
	public boolean authorizeUser(String userId, String resourceType, String operation);
	
	public boolean unauthorizeUser(String userId, String resourceType, String operation);

}
