package com.siemens.primecult.servlet;

import static com.siemens.primecult.alarmservices.AlarmManagement.makeEntryToManageAlarmsForAsset;
import static com.siemens.primecult.core.KPICalculator.changeTrainingStatus;
import static com.siemens.primecult.core.KPICalculator.makeEntryToManageAssetTrainingStatus;
import static com.siemens.storage.DbConnection.getDbConnection;
import static com.siemens.storage.DbConnection.releaseResources;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.siemens.primecult.core.ParameterizedData;
import com.siemens.storage.DbConnection;
import com.siemens.storage.SelectOperations;

public class LoadOnStartupServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException {
		super.init();
		initializeCacheIfAssetConfigured();
		initializeTrainingStatusIfAssetConfigured();
	}

	private void initializeCacheIfAssetConfigured() {
		SelectOperations opr = new SelectOperations();
		String qStr = "select * from paramdata";
		ParameterizedData pdata = new ParameterizedData();
		Connection connection = null;
		try {
			connection = getDbConnection();
			opr.select(connection, qStr, pdata);
			List<Object> assetParams = pdata.getData();
			for (Object object : assetParams)
				initializeCacheIfAssetConfigured(((ParameterizedData) object).getAssetID());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			releaseResources(connection);
		}
	}

	private void initializeCacheIfAssetConfigured(String assetID) {
		makeEntryToManageAlarmsForAsset(assetID);
		makeEntryToManageAssetTrainingStatus(assetID);
	}

	private void initializeTrainingStatusIfAssetConfigured() {
		String qStr = "select distinct asset_id from bias_and_weight";
		Connection connection = null;
		try {
			connection = getDbConnection();
			Statement stmt = connection.createStatement();
			ResultSet rs;
			rs = stmt.executeQuery(qStr);
			List<String> assetIds = new ArrayList<>();
			while (rs.next()) {
				assetIds.add(rs.getString("asset_id"));
			}
			for (String assetId : assetIds)
				changeTrainingStatus(assetId);
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbConnection.releaseResources(connection);
		}
	}

	public static void main(String[] args) {
		new LoadOnStartupServlet().initializeTrainingStatusIfAssetConfigured();
	}
}
