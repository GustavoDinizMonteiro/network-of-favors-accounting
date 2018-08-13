package fogbow.billing.plugins;

import java.util.List;
import org.fogbowcloud.manager.core.constants.Operation;
import org.fogbowcloud.manager.core.models.instances.InstanceType;
import org.fogbowcloud.manager.core.models.orders.Order;
import org.fogbowcloud.manager.core.models.tokens.FederationUser;
import org.fogbowcloud.manager.core.plugins.behavior.authorization.AuthorizationPlugin;

public class AuthorizationComposedPlugin implements AuthorizationPlugin {
	
	List<AuthorizationPlugin> authorizationPlugins;
	
	public AuthorizationComposedPlugin(CloudAuthorizationPluginsHolder authorizationPluginsHolder) {
		this.authorizationPlugins = authorizationPluginsHolder.getAllAuthorizationPlugins();
	}

	@Override
	public boolean isAuthorized(FederationUser federationUser, Operation operation, Order order) {
		return true;
	}

	@Override
	public boolean isAuthorized(FederationUser federationUser, Operation operation, InstanceType type) {
		
		for (AuthorizationPlugin plugin : this.authorizationPlugins) {
			if (!plugin.isAuthorized(federationUser, operation, type)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isAuthorized(FederationUser federationUser, Operation operation) {
		return true;
	}

}
