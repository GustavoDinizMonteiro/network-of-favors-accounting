package fogbow.billing.plugins;

import java.util.HashMap;

import org.fogbowcloud.manager.core.constants.Operation;
import org.fogbowcloud.manager.core.exceptions.UnexpectedException;
import org.fogbowcloud.manager.core.models.instances.InstanceType;
import org.fogbowcloud.manager.core.models.tokens.FederationUser;
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
		System.out.println(authCompPlugin.isAuthorized(new FederationUser("fake-id-1", new HashMap<>()), Operation.CREATE, InstanceType.COMPUTE));
	}

}
