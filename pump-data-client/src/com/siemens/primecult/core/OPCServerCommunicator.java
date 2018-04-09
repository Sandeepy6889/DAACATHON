package com.siemens.primecult.core;

import static com.siemens.primecult.init.MqttClientFactory.getMqttClient;
import static com.siemens.primecult.init.OPCUaClientFactory.getOPCUaClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.opcfoundation.ua.builtintypes.DataValue;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.opcfoundation.ua.core.TimestampsToReturn;

import com.amazonaws.services.iot.client.AWSIotException;
import com.prosysopc.ua.ServiceException;
import com.siemens.primecult.models.ValueRt;
import com.siemens.primecult.utils.ObjectToJSonMapper;

public class OPCServerCommunicator {

	private static final String PUMP_TOPIC = "centrifugalPumpData";
	private static Map<String, Timer> timerMapping = new HashMap<>();
	private static final int READ_UPDATE_INTERVAL = 10000;
	private static final int INITIAL_DELAY = 100;

	public static String startFetchingData(String assetID) {
		if (timerMapping.containsKey(assetID))
			return "asset Id  already subscribed";
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
		}, INITIAL_DELAY, READ_UPDATE_INTERVAL);
		timerMapping.put(assetID, timer);
		return "data subscribed";
	}

	public static String stopFetchingData(String assetID) {
		if (!timerMapping.containsKey(assetID))
			return "No asset with this ID was subscribed";
		timerMapping.get(assetID).cancel();
		timerMapping.remove(assetID);
		return "successfully unsubscribed";

	}

	private static NodeId[] getNodeIds(String assetID) {

		// NodeId ffNode = new NodeId(2, assetID + "_FF");
		// NodeId psNode = new NodeId(2, assetID + "_Ps");
		// NodeId pdNode = new NodeId(2, assetID + "_Pd");
		// NodeId motorCurrent = new NodeId(2, assetID + "_AMP");
		// return new NodeId[] { ffNode, psNode, pdNode, motorCurrent };

		NodeId nodeId1 = new NodeId(5, "Counter1_1");
		NodeId nodeId2 = new NodeId(5, "Counter2_1");
		NodeId nodeId3 = new NodeId(5, "Counter3_1");
		NodeId nodeId4 = new NodeId(5, "Counter4_1");
		return new NodeId[] { nodeId1, nodeId2, nodeId3, nodeId4 };

	}

	private static void readAndPublish(String assetID, NodeId[] nodeIds) throws AWSIotException {

		DataValue[] values = null;
		try {
			values = getOPCUaClient().readValues(nodeIds, TimestampsToReturn.Source);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		double[] valuesArr = new double[4];
		for (int i = 0; i < 4; i++)
			valuesArr[i] = values[i].getValue().doubleValue();
		ValueRt valueRt = new ValueRt(assetID, values[0].getSourceTimestamp().getMilliSeconds(), valuesArr);
		String payload = new ObjectToJSonMapper().mapObjToJSonStr(valueRt);
		getMqttClient().publish(PUMP_TOPIC, payload);
		System.out.println(payload);
	}
}
