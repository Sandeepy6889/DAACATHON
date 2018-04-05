package com.siemens.primecult.clientServices;

import static com.siemens.primecult.core.OPCServerCommunicator.startFetchingData;
import static com.siemens.primecult.core.OPCServerCommunicator.stopFetchingData;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/Client-Invocation")
public class ClientInvocationService {

	@GET
	@Path("/added/{assetId}")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.TEXT_PLAIN)
	public String assetAdded(@PathParam("assetId") String assetId) {
		return startFetchingData(assetId);
	}

	@GET
	@Path("/removed/{assetId}")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.TEXT_PLAIN)
	public String assetRemoved(@PathParam("assetId") String assetId) {
		return stopFetchingData(assetId);
	}

}
