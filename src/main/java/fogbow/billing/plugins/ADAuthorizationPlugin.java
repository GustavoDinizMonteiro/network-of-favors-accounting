package fogbow.billing.plugins;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.fogbowcloud.manager.core.constants.Operation;
import org.fogbowcloud.manager.core.models.instances.InstanceType;
import org.fogbowcloud.manager.core.models.orders.Order;
import org.fogbowcloud.manager.core.models.tokens.FederationUser;
import org.fogbowcloud.manager.core.plugins.behavior.authorization.AuthorizationPlugin;

public class ADAuthorizationPlugin implements AuthorizationPlugin {
	
	public static final String API_URL = "http://localhost:8080";
	public static final String AUTH_ENDPOINT = "/auth";
	
	public ADAuthorizationPlugin() {
	}

	@Override
	public boolean isAuthorized(FederationUser federationUser, Operation operation, Order order) {
		return true;
	}

	@Override
	public boolean isAuthorized(FederationUser federationUser, Operation operation, InstanceType type) {
		
		String endpoint = API_URL + AUTH_ENDPOINT + "/" + federationUser.getId() + "/" + type + "/" + operation;
		StringBuffer content = null;
		
		try {
			URL url = new URL(endpoint);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			
			BufferedReader in = new BufferedReader(
					  new InputStreamReader(con.getInputStream()));
					String inputLine;
					content = new StringBuffer();
					while ((inputLine = in.readLine()) != null) {
					    content.append(inputLine);
					}
					con.disconnect();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return Boolean.valueOf(content.toString());
	}

	@Override
	public boolean isAuthorized(FederationUser federationUser, Operation operation) {
		return true;
	}
	

}
