package com.siemens.primeCult.core;

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
		refKPI.setRefEfficiency(rs.getDouble("Efficiency"));
		refKPI.setRefTDH(rs.getDouble("TDH"));
		refKPI.setRefFlow(rs.getDouble("Flow"));
		refKPI.setRefTimeStamp(rs.getLong("Timestamp"));
		pdata.add(refKPI);
	}

	@Override
	public List<Object> getData() {
		return pdata;
	}

}
