package fogbow.billing.plugin.authorization.service;

public interface DistributedAuthorizationPluginServerInterface {
	
	public boolean getUserAuthorization(String tokenProvider, String userId, String resourceType, String operation);
	
	public boolean authorizeUser(String tokenProvider, String userId, String resourceType, String operation);
	
	public boolean unauthorizeUser(String tokenProvider, String userId, String resourceType, String operation);

}
