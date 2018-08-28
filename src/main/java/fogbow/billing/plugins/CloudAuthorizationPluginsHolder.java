package fogbow.billing.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.fogbowcloud.ras.core.PluginFactory;
import org.fogbowcloud.ras.core.plugins.aaa.authorization.AuthorizationPlugin;

public class CloudAuthorizationPluginsHolder {
	
	private List<AuthorizationPlugin> authorizationPluginsList;
	private PluginFactory pluginFactory;
	private Properties properties;
	
	// TODO Read plugins from configuration file by using properties and creating the objects by using
	// pluginFactory
	public CloudAuthorizationPluginsHolder() {
		this.authorizationPluginsList = new ArrayList<>();
		this.authorizationPluginsList.add(new ADAuthorizationPlugin());
		//this.authorizationPluginsList.add(new PosPaidAuthorizationPlugin());
	}

	public List<AuthorizationPlugin> getAllAuthorizationPlugins() {
		return this.authorizationPluginsList;
	}

}
