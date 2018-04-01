package com.siemens.primecult.client;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.opcfoundation.ua.builtintypes.DataValue;
import org.opcfoundation.ua.builtintypes.LocalizedText;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.opcfoundation.ua.core.ApplicationDescription;
import org.opcfoundation.ua.core.ApplicationType;
import org.opcfoundation.ua.core.MessageSecurityMode;
import org.opcfoundation.ua.core.TimestampsToReturn;
import org.opcfoundation.ua.transport.security.SecurityMode;
import org.opcfoundation.ua.transport.security.SecurityPolicy;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.prosysopc.ua.ApplicationIdentity;
import com.prosysopc.ua.ServiceException;
import com.prosysopc.ua.UserIdentity;
import com.prosysopc.ua.client.UaClient;
import com.siemens.primecult.models.ValueRt;
import com.siemens.primecult.utils.ObjectToJSonMapper;
import com.siemens.primecult.utils.Util;
import com.siemens.primecult.utils.Util.KeyStorePasswordPair;

public class PumpDataPublisher {
	private static final String PUMP_TOPIC = "centrifugalPumpData";
	private AWSIotMqttClient awsIotClient;
	private UaClient uaClient;
	private Map<String, Timer> timerMapping = new HashMap<>();

	public PumpDataPublisher() {
		initOPCUAClient();
		initMqttClient();
		addShutDownHookThread();
	}

	private void addShutDownHookThread() {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				uaClient.disconnect();
				try {
					awsIotClient.disconnect();
				} catch (AWSIotException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}));

	}

	private void initMqttClient() {
		String clientEndpoint = "a22j678ixe7pxh.iot.us-east-2.amazonaws.com";
		String clientId = "centrifugalPump";

		String certificateFile = "e055fd4e37-certificate.pem.crt";
		String privateKeyFile = "e055fd4e37-private.pem.key";
		if (awsIotClient == null && certificateFile != null && privateKeyFile != null) {
			String algorithm = "RSA";
			KeyStorePasswordPair pair = Util.getKeyStorePasswordPair(certificateFile, privateKeyFile, algorithm);
			awsIotClient = new AWSIotMqttClient(clientEndpoint, clientId, pair.keyStore, pair.keyPassword);
		}
		if (awsIotClient == null) {
			throw new IllegalArgumentException("Failed to construct client due to missing certificate or credentials.");
		}
	}

	/*
	 * private void initOPCUAClient() { try { uaClient = new
	 * UaClient("opc.tcp://This-PC:53530/OPCUA/SimulationServer"); final
	 * ApplicationIdentity identity = ApplicationIdentity
	 * .loadOrCreateCertificate(getApplicationDescription(), "Organisation", null,
	 * null, true); uaClient.setApplicationIdentity(identity);
	 * uaClient.setSecurityMode(SecurityMode.NONE); uaClient.connect();
	 * 
	 * } catch (Exception exception) { exception.printStackTrace(); } }
	 */

	private void initOPCUAClient() {
		try {
			uaClient = new UaClient("opc.tcp://192.168.27.12:4841/OpcUaServer_901");
			final ApplicationIdentity identity = ApplicationIdentity
					.loadOrCreateCertificate(getApplicationDescription(), "Organisation", null, null, true);
			uaClient.setApplicationIdentity(identity);
			uaClient.setSecurityMode(new SecurityMode(SecurityPolicy.NONE, MessageSecurityMode.None));
			uaClient.setUserIdentity(new UserIdentity("alam", "daac"));
		} catch (Exception exception) {

		}

	}

	/*
	 * private static ApplicationDescription getApplicationDescription() { String
	 * APP_NAME = "SimulationServer"; ApplicationDescription appDescription = new
	 * ApplicationDescription(); appDescription.setApplicationName(new
	 * LocalizedText(APP_NAME + "@localhost", Locale.ENGLISH));
	 * appDescription.setApplicationUri("urn:localhost:OPCUA:" + APP_NAME);
	 * appDescription.setProductUri("urn:prosysopc.com:OPCUA:" + APP_NAME);
	 * appDescription.setApplicationType(ApplicationType.Client); return
	 * appDescription; }
	 */

	private ApplicationDescription getApplicationDescription() {
		String APP_NAME = "OpcUaServer_901";
		ApplicationDescription appDescription = new ApplicationDescription();
		appDescription.setApplicationName(new LocalizedText(APP_NAME + "@localhost", Locale.ENGLISH));
		appDescription.setApplicationUri("urn:localhost:OPCUA:" + APP_NAME);
		appDescription.setProductUri("urn:prosysopc.com:OPCUA:" + APP_NAME);
		appDescription.setApplicationType(ApplicationType.Client);
		return appDescription;
	}

	private void subscribeAssetData(String assetID) {
		try {
			awsIotClient.connect();
			NodeId[] nodeIds = getNodeIds(assetID);
			Timer timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					try {
						readAndPublish(assetID, nodeIds);
					} catch (AWSIotException e) {
						e.printStackTrace();
					}
				}
			}, 100, 10000);
			timerMapping.put(assetID, timer);
		} catch (AWSIotException e1) {
			System.out.println(e1);
		}
	}

	// will be called when configured asset will be removed
	@SuppressWarnings("unused")
	private void stopFetchingData(String assetID) {
		timerMapping.get(assetID).cancel();
	}

	private static NodeId[] getNodeIds(String assetID) {

		NodeId ffNode = new NodeId(2, assetID + "FF||OUT");
		NodeId psNode = new NodeId(2, assetID + "Ps|1|OUT");
		NodeId pdNode = new NodeId(2, assetID + "Pd|1|OUT");
		NodeId epmNode = new NodeId(2, assetID + "PWR|1|OUT");
		return new NodeId[] { ffNode, psNode, pdNode, epmNode };

		/*
		 * NodeId nodeId1 = new NodeId(5, "Counter1"); NodeId nodeId2 = new NodeId(5,
		 * "Random1"); NodeId nodeId3 = new NodeId(5, "Sawtooth1"); NodeId nodeId4 = new
		 * NodeId(5, "Sinusoid1"); return new NodeId[] { nodeId1, nodeId2, nodeId3,
		 * nodeId4 };
		 */
	}

	private void readAndPublish(String assetID, NodeId[] nodeIds) throws AWSIotException {

		DataValue[] values = null;
		try {
			values = uaClient.readValues(nodeIds, TimestampsToReturn.Source);
		} catch (ServiceException e) {
			System.out.println(e);
		}
		double[] valuesArr = new double[4];
		for (int i = 0; i < 4; i++)
			valuesArr[i] = values[i].getValue().doubleValue();
		ValueRt valueRt = new ValueRt(assetID, values[0].getSourceTimestamp().getMilliSeconds(), valuesArr);
		String payload = new ObjectToJSonMapper().mapObjToJSonStr(valueRt);
		System.out.println(payload);
		awsIotClient.publish(PUMP_TOPIC, payload);
	}

	public static void main(String args[]) {
		PumpDataPublisher publishPumpData = new PumpDataPublisher();
		publishPumpData.subscribeAssetData("01");// will be invoked on asset addition
	}

}
