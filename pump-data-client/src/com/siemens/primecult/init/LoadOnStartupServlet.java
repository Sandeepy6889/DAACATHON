package com.siemens.primecult.init;

import static com.siemens.primecult.core.OPCServerCommunicator.startFetchingData;
import static com.siemens.primecult.init.MqttClientFactory.getMqttClient;
import static com.siemens.primecult.init.OPCUaClientFactory.getOPCUaClient;
import static com.siemens.storage.DbConnection.getDbConnection;
import static com.siemens.storage.DbConnection.releaseResources;

import java.sql.Connection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.siemens.primecult.models.ParameterizedData;
import com.siemens.storage.SelectOperations;

public class LoadOnStartupServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException {

		super.init();
		getMqttClient();
		getOPCUaClient();
		startFecthingDataIfAssetConfigured();
	}

	private void startFecthingDataIfAssetConfigured() {
		SelectOperations opr = new SelectOperations();
		String qStr = "select * from paramdata";
		ParameterizedData pdata = new ParameterizedData();
		Connection connection = null;
		try {
			connection = getDbConnection();
			opr.select(connection, qStr, pdata);
			List<Object> assetParams = pdata.getData();
			for (Object object : assetParams) {
				startFetchingData(((ParameterizedData) object).getAssetID());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			releaseResources(connection);
		}
	}
	
	public static void main(String[] args) {
		new LoadOnStartupServlet().startFecthingDataIfAssetConfigured();
	}
}
