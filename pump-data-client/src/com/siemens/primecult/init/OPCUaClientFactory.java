package com.siemens.primecult.init;

import static com.siemens.primecult.core.DataClientConfiguration.APP_NAME;
import static com.siemens.primecult.core.DataClientConfiguration.URI;

import java.util.Locale;

import org.opcfoundation.ua.builtintypes.LocalizedText;
import org.opcfoundation.ua.core.ApplicationDescription;
import org.opcfoundation.ua.core.ApplicationType;
import org.opcfoundation.ua.core.MessageSecurityMode;
import org.opcfoundation.ua.transport.security.SecurityMode;
import org.opcfoundation.ua.transport.security.SecurityPolicy;

import com.prosysopc.ua.ApplicationIdentity;
import com.prosysopc.ua.UserIdentity;
import com.prosysopc.ua.client.UaClient;

public class OPCUaClientFactory {

	private static UaClient uaClient = initOPCUAClient();

	private OPCUaClientFactory() {
	}

	public static UaClient getOPCUaClient() {
		return uaClient;
	}

	private static UaClient initOPCUAClient() {
		UaClient opcClient = null;
		try {
			opcClient = new UaClient(URI);
			final ApplicationIdentity identity = ApplicationIdentity
					.loadOrCreateCertificate(getApplicationDescription(), "Organisation", null, null, true);
			opcClient.setApplicationIdentity(identity);
			opcClient.setSecurityMode(new SecurityMode(SecurityPolicy.NONE, MessageSecurityMode.None));
			opcClient.setUserIdentity(new UserIdentity("alam", "daac"));
			opcClient.connect();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return opcClient;
	}

	private static ApplicationDescription getApplicationDescription() {
		ApplicationDescription appDescription = new ApplicationDescription();
		appDescription.setApplicationName(new LocalizedText(APP_NAME + "@localhost", Locale.ENGLISH));
		appDescription.setApplicationUri("urn:localhost:OPCUA:" + APP_NAME);
		appDescription.setProductUri("urn:prosysopc.com:OPCUA:" + APP_NAME);
		appDescription.setApplicationType(ApplicationType.Client);
		return appDescription;
	}
}
