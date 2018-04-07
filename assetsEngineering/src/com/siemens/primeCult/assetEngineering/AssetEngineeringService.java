package com.siemens.primeCult.assetEngineering;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.siemens.primeCult.core.ParameterizedData;

@Path("/assetsEngineering")
public class AssetEngineeringService {
	
	@GET
	@Path("/get")
	@Produces(MediaType.APPLICATION_JSON)
	public List<ParameterizedData> getAll() {
		return getAssetsEnginneringInfo();
	}

	private List<ParameterizedData> getAssetsEnginneringInfo() {
		JSONParser parser = new JSONParser();
		List<ParameterizedData> paramsData = new ArrayList<>();
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			InputStream inputStream = classLoader.getResourceAsStream("assetsConfig.json");
			Reader reader = new InputStreamReader(inputStream);
			Object obj = parser.parse(reader);

			JSONArray jsonObject = (JSONArray) obj;

			for (Object paramData : jsonObject) {
				JSONObject paramObject = (JSONObject) paramData;
				ParameterizedData param = new ParameterizedData();
				param.setAssetID((String) paramObject.get("assetID"));
				param.setAssetName((String) paramObject.get("assetName"));
				param.setRatedPower(Double.valueOf((String) paramObject.get("ratedPower")));
				param.setMotorEfficiency(Double.valueOf((String) paramObject.get("motorEfficiency")));
				param.setMotorRatedSpeed(Double.valueOf((String) paramObject.get("motorRatedSpeed")));
				param.setMinRatedFlowOfPump(Double.valueOf((String) paramObject.get("minRatedFlowOfPump")));
				param.setWaterDensity(Double.valueOf((String) paramObject.get("waterDensity")));
				param.setThreadholdLT(Double.valueOf((String) paramObject.get("threadholdLT")));
				param.setSuctionDiameter(Double.valueOf((String) paramObject.get("suctionDiameter")));
				param.setDischargeDiameter(Double.valueOf((String) paramObject.get("dischargeDiameter")));
				param.setEleveationDiff(Double.valueOf((String) paramObject.get("eleveationDiff")));
				paramsData.add(param);
				System.out.println(param);
			}
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return paramsData;
	}

}
