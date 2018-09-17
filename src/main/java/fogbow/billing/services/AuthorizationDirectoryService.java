package fogbow.billing.services;

public interface AuthorizationDirectoryService {
	
	public boolean getUserAuthorization(String tokenProvider, String userId, String resourceType, String operation);
	
	public boolean authorizeUser(String tokenProvider, String userId, String resourceType, String operation);
	
	public boolean unauthorizeUser(String tokenProvider, String userId, String resourceType, String operation);

}
