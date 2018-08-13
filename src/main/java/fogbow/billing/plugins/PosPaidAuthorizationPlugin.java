package fogbow.billing.plugins;

import org.fogbowcloud.manager.core.constants.Operation;
import org.fogbowcloud.manager.core.models.instances.InstanceType;
import org.fogbowcloud.manager.core.models.orders.Order;
import org.fogbowcloud.manager.core.models.tokens.FederationUser;
import org.fogbowcloud.manager.core.plugins.behavior.authorization.AuthorizationPlugin;

public class PosPaidAuthorizationPlugin implements AuthorizationPlugin{

	@Override
	public boolean isAuthorized(FederationUser federationUser, Operation operation, Order order) {
		return true;
	}

	@Override
	public boolean isAuthorized(FederationUser federationUser, Operation operation, InstanceType type) {
		return true;
	}

	@Override
	public boolean isAuthorized(FederationUser federationUser, Operation operation) {
		return true;
	}

}
