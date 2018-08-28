package fogbow.billing.plugins;

import org.fogbowcloud.ras.core.constants.Operation;
import org.fogbowcloud.ras.core.exceptions.UnexpectedException;
import org.fogbowcloud.ras.core.models.ResourceType;
import org.fogbowcloud.ras.core.models.tokens.FederationUserToken;
import org.junit.Before;
import org.junit.Test;

import fogbow.billing.BillingApplicationTests;

public class AuthorizationComposedPluginTest extends BillingApplicationTests {
	
	AuthorizationComposedPlugin authCompPlugin;
	
	@Before
	public void setUp() {
		authCompPlugin = new AuthorizationComposedPlugin(new CloudAuthorizationPluginsHolder());
	}
	
	@Test
	public void testIsAuthorized() throws UnexpectedException {
		System.out.println(authCompPlugin.isAuthorized(new FederationUserToken("token-provider", "token-vale", "fake-id-1", "user-name"), Operation.CREATE, ResourceType.COMPUTE));
	}

}
