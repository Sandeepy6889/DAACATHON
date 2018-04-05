package com.siemens.primecult.init;

import static com.siemens.primecult.init.MqttClientFactory.getMqttClient;
import static com.siemens.primecult.init.OPCUaClientFactory.getOPCUaClient;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public class LoadOnStartupServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException {
		
		super.init();
		getMqttClient();
		getOPCUaClient();
	}
}
