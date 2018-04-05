package com.siemens.primecult.init;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.siemens.primecult.utils.Util;
import com.siemens.primecult.utils.Util.KeyStorePasswordPair;

public class MqttClientFactory {

	private static AWSIotMqttClient awsIotClient = initMqttClient();

	private MqttClientFactory() {

	}

	public static AWSIotMqttClient getMqttClient() {
		return awsIotClient;
	}

	private static AWSIotMqttClient initMqttClient() {
		String clientEndpoint = "a22j678ixe7pxh.iot.us-east-2.amazonaws.com";
		String clientId = "centrifugalPump";

		String certificateFile = "e055fd4e37-certificate.pem.crt";
		String privateKeyFile = "e055fd4e37-private.pem.key";
		AWSIotMqttClient mqttClient = null;
		try {
			if (certificateFile != null && privateKeyFile != null) {
				String algorithm = "RSA";
				KeyStorePasswordPair pair = Util.getKeyStorePasswordPair(certificateFile, privateKeyFile, algorithm);
				mqttClient = new AWSIotMqttClient(clientEndpoint, clientId, pair.keyStore, pair.keyPassword);
			}
			mqttClient.connect();
		} catch (AWSIotException e) {
			throw new IllegalArgumentException("Failed to construct client due to missing certificate or credentials.");
		}
		return mqttClient;
	}

}
