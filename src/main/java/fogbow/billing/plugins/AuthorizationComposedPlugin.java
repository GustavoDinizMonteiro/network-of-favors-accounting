package fogbow.billing.plugins;

import java.util.List;

import org.fogbowcloud.ras.core.constants.Operation;
import org.fogbowcloud.ras.core.models.ResourceType;
import org.fogbowcloud.ras.core.models.tokens.FederationUserToken;
import org.fogbowcloud.ras.core.plugins.aaa.authorization.AuthorizationPlugin;

public class AuthorizationComposedPlugin implements AuthorizationPlugin {
	
	List<AuthorizationPlugin> authorizationPlugins;
	
	public AuthorizationComposedPlugin(CloudAuthorizationPluginsHolder authorizationPluginsHolder) {
		this.authorizationPlugins = authorizationPluginsHolder.getAllAuthorizationPlugins();
	}

	@Override
	public boolean isAuthorized(FederationUserToken federationUserToken, Operation operation, ResourceType type) {
		
		for (AuthorizationPlugin plugin : this.authorizationPlugins) {
			if (!plugin.isAuthorized(federationUserToken, operation, type)) {
				return false;
			}
		}
		return true;
	}

}
