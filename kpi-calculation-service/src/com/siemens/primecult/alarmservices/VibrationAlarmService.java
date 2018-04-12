package com.siemens.primecult.alarmservices;

import static com.siemens.primecult.constants.AlarmStatus.GONE;
import static com.siemens.primecult.constants.AlarmStatus.RAISED;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import com.siemens.primecult.constants.AlarmTypes;
import com.siemens.primecult.models.ValueRt;
import com.siemens.primecult.utils.ObjectToJSonMapper;

public class VibrationAlarmService {

	public static boolean checkVibrationAlarmStateChange(AlarmTypes alarmType, ValueRt rawValues,
			Map<String, List<Integer>> currentAlarmsStatus) {

		int currentState = checkforImpellerWearingFault(rawValues) ? RAISED : GONE;
		int previousStae = currentAlarmsStatus.get(rawValues.getAssetID()).get(alarmType.getIndex());
		return (currentState != previousStae);
	}

	private static boolean checkforImpellerWearingFault(ValueRt valueRt) {

		URL url;
		try {
			url = new URL("http://impellerfaultpredictionohio-dev.us-east-2.elasticbeanstalk.com/predict");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			conn.getOutputStream().write(new ObjectToJSonMapper().mapObjToJSonStr(valueRt).getBytes());
			conn.getResponseMessage();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder result = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				result.append(line);
			}
			System.out.println("vibration result-------1" +result.toString());
			System.out.println("vibration result------" + Boolean.valueOf(result.toString()));
			return Boolean.valueOf(result.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/*
	 * public static void main(String[] args) { float[] values = { 0.639588925f,
	 * -0.843515652f, -1.814916899f, 0.256660608f, 1.441098879f, 0.2645025f,
	 * -0.484934599f, 0.975416844f, 1.021705937f, -0.981929017f, -0.689522735f,
	 * 0.571403098f, 0.821074445f, 0.277298455f, -1.123855599f, 0.665026026f,
	 * 0.585799315f, 1.080349878f, -0.330868107f, -0.472598488f, 0.410218205f,
	 * 0.823747678f, -0.613660562f, 2.21675342f, 0.702618356f, 0.275937314f,
	 * -1.231741384f, 0.28815693f, 1.364298621f, -0.501268296f, 0.675026026f,
	 * 0.2208724f, 0.582690762f, 0.215090614f, -0.161835191f, -0.639172766f,
	 * 0.477760249f, -0.049295775f, -0.268586691f, -0.653955252f, -0.520919008f,
	 * 0.258873996f, 1.324758077f, -0.687180591f, -1.406415066f, 1.740733926f,
	 * 1.020805937f, 2.215675776f, 0.341842309f, 0.567876734f, -1.042821156f,
	 * -0.741840842f, 0.227671975f, 0.317746427f, -1.139441281f, 0.153514294f,
	 * 1.929699592f, -0.252283651f, -1.233642076f, 0.910964959f, 0.030239028f,
	 * -1.625141905f, -0.301554517f, 1.964286073f }; ValueRt valueRt = new
	 * ValueRt(); valueRt.setVibrationAmplitudes(values);
	 * System.out.println(checkforImpellerWearingFault(valueRt)); }
	 */

}
