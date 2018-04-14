package com.siemens.primecult.emailsubscrition;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.siemens.primecult.emailutil.EmailUtil;


public class mailSubscriptionService {
	@GET
	@Path("/subscibe/{emailId}")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.TEXT_PLAIN)
	public String subscribeMail(@PathParam("emailId") String emailId) {
		return EmailUtil.subscribeEMailID(emailId);
	}
	
}
