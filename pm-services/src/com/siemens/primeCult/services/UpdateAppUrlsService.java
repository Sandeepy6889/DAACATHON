package com.siemens.primeCult.services;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.siemens.primeCult.core.UrlsInfo;
import com.siemens.storage.DBUtil;
import com.siemens.storage.TableRow;

@Path("/appUrls")
public class UpdateAppUrlsService {

	@GET
	@Path("/get")
	@Produces(MediaType.APPLICATION_JSON)
	public UrlsInfo get() {
		String qString = "select * from urls_info where id=1";
		List<Object> records = DBUtil.get(qString, new UrlsInfo());
		return (UrlsInfo) records.get(0);
	}

	@POST
	@Path("/update")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String update(UrlsInfo urlsInfo) {

		TableRow row = new TableRow("urls_info");
		row.update("asset_created", urlsInfo.getAssetCreated().trim());
		row.update("asset_trained", urlsInfo.getAssetTrained().trim());
		row.update("alarms_status", urlsInfo.getAlarmsStatus().trim());
		row.update("teach_model", urlsInfo.getTeachModel().trim());
		row.update("asset_created_p_client", urlsInfo.getAssetCreatedPClient().trim());
		row.update("engg_assets", urlsInfo.getEnggAssets().trim());
		row.where("id", 1);
		boolean update = DBUtil.update(row);
		return update ? "updated successfully" : "failure! could not update";
	}
}