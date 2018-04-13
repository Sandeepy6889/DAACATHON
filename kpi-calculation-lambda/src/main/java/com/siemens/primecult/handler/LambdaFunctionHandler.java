package com.siemens.primecult.handler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.siemens.primecult.util.ObjectToJSonMapper;

public class LambdaFunctionHandler implements RequestHandler<Object, String> {

	@Override
	public String handleRequest(Object input, Context context) {
		context.getLogger().log("Input: " + input);
		try {
			System.out.println("lambda called : "+input);
			URL url = new URL("http://kpicalc-env.us-east-2.elasticbeanstalk.com/kpi-services/KPI-Calculation/calculate");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			conn.getOutputStream().write(new ObjectToJSonMapper().mapObjToJSonStr(input).getBytes());
			String reponse = conn.getResponseMessage();
			System.out.println("response "+reponse);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "OK";
	}

}
