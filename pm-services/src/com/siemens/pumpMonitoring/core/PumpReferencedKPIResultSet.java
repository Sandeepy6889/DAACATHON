package com.siemens.pumpMonitoring.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PumpReferencedKPIResultSet implements DbRowToObject {

	private List<Object> pdata = new ArrayList<>();
	@Override
	public void fill(ResultSet rs) throws SQLException {
		PumpReferencedKPI refKPI = new PumpReferencedKPI();
		refKPI.setAssetID(rs.getString("AssetId"));
		refKPI.setRefEfficiency(rs.getDouble("RefEfficiency"));
		refKPI.setRefTDH(rs.getDouble("RefTDH"));
		refKPI.setRefFlow(rs.getDouble("RefFlow"));
		refKPI.setRefTimeStamp(rs.getLong("RefTimeStamp"));
		pdata.add(refKPI);
	}

	@Override
	public List<Object> getData() {
		return pdata;
		}

}
