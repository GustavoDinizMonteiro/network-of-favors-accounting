package fogbow.billing.plugin.authorizarization.client;

import org.fogbowcloud.ras.core.HomeDir;
import org.fogbowcloud.ras.util.PropertiesUtil;
import java.util.Properties;
import org.fogbowcloud.ras.core.plugins.aaa.authorization.DistributedAuthorizationPluginClient;

public class PostPaidAuthorizationPluginClient extends DistributedAuthorizationPluginClient {
    private static final String CONF_FILE = "postpaid-authorization-plugin-client.conf";
    private static final String PROP_SERVER_URL = "server_url";

    public PostPaidAuthorizationPluginClient() {
        Properties properties = PropertiesUtil.readProperties(HomeDir.getPath() + CONF_FILE);
        String serverUrl = properties.getProperty(PROP_SERVER_URL);
        super.setServerUrl(serverUrl);
    }
}