package com.siemens.primecult.core;

import static com.siemens.primecult.core.DataClientConfiguration.NAME_SPACE_INDEX;
import static com.siemens.primecult.init.MqttClientFactory.getMqttClient;
import static com.siemens.primecult.init.OPCUaClientFactory.getOPCUaClient;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.opcfoundation.ua.builtintypes.DataValue;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.opcfoundation.ua.core.TimestampsToReturn;

import com.amazonaws.services.iot.client.AWSIotException;
import com.prosysopc.ua.ServiceException;
import com.siemens.primecult.models.ValueRt;
import com.siemens.primecult.oracle.OracleConnection;
import com.siemens.primecult.oracle.OracleDBOperation;
import com.siemens.primecult.utils.ObjectToJSonMapper;

public class OPCServerCommunicator {

	private static final String PUMP_TOPIC = "centrifugalPumpData";
	private static Map<String, Timer> timerMapping = new HashMap<>();
	private static final int READ_UPDATE_INTERVAL = 1000;
	private static final int INITIAL_DELAY = 100;
	private static Connection connection;
	private static final int noOfRecords = 10;// 16384;
	private static int currentPointer = 0;

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
		return "success";
	}

	public static String stopFetchingData(String assetID) {
		if (!timerMapping.containsKey(assetID))
			return "No asset with this ID was subscribed";
		timerMapping.get(assetID).cancel();
		timerMapping.remove(assetID);
		return "success";

	}

	private static NodeId[] getNodeIds(String assetID) {
		NodeId ffNode = new NodeId(NAME_SPACE_INDEX, assetID + "_FF||OUT");
		NodeId psNode = new NodeId(NAME_SPACE_INDEX, assetID + "_Ps||OUT");
		NodeId pdNode = new NodeId(NAME_SPACE_INDEX, assetID + "_Pd||OUT");
		NodeId motorPower = new NodeId(NAME_SPACE_INDEX, assetID + "_PWR||OUT");
		return new NodeId[] { ffNode, psNode, pdNode, motorPower };

	}

	private static void readAndPublish(String assetID, NodeId[] nodeIds) throws AWSIotException {

		DataValue[] values = null;
		try {
			values = getOPCUaClient().readValues(nodeIds, TimestampsToReturn.Source);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		float[] valuesArr = new float[4];
		for (int i = 0; i < 4; i++)
			valuesArr[i] = (float) (Math.round(values[i].getValue().floatValue() * 100.0) / 100.0);
		float[] vibrationValues = getVibrationData(currentPointer++);
		ValueRt valueRt = new ValueRt(assetID, values[0].getSourceTimestamp().getTimeInMillis(), valuesArr,
				vibrationValues);
		String payload = new ObjectToJSonMapper().mapObjToJSonStr(valueRt);
		getMqttClient().publish(PUMP_TOPIC, payload);
		currentPointer %= (noOfRecords - 1);
		System.out.println(payload);
	}

	public static void main(String[] args) {
		System.out.println((float)Math.round(12.34578 * 100.0) / 100.0);
	}
	
	private static float[] getVibrationData(int startOffset) {
		float[] values = new float[64];
		try {
			if (connection == null)
				connection = OracleConnection.getDbConnection();
			System.out.println((startOffset * 64 + 1) + " - " + ((startOffset * 64 + 1) + 63));
			List<Double> list = OracleDBOperation.get(connection, startOffset * 64 + 1, (startOffset * 64 + 1) + 63);
			for (int i = 0; i < list.size(); i++)
				values[i] = list.get(i).floatValue();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return values;
	}

	public static void disconnect() {
		if (getOPCUaClient() != null) {
			for (String assetID : timerMapping.keySet())
				timerMapping.get(assetID).cancel();
			timerMapping.clear();
			if (getOPCUaClient().isConnected())
				getOPCUaClient().disconnect();
			try {
				getMqttClient().disconnect();
			} catch (AWSIotException e) {
				e.printStackTrace();
			}
		}
	}
}
