package fogbow.billing.plugins;

import org.fogbowcloud.ras.core.constants.Operation;
import org.fogbowcloud.ras.core.models.ResourceType;
import org.fogbowcloud.ras.core.models.tokens.FederationUserToken;
import org.fogbowcloud.ras.core.plugins.aaa.authorization.AuthorizationPlugin;

public class PosPaidAuthorizationPlugin implements AuthorizationPlugin{

	@Override
	public boolean isAuthorized(FederationUserToken federationUserToken, Operation operation, ResourceType type) {
		// TODO Auto-generated method stub
		return false;
	}


}
