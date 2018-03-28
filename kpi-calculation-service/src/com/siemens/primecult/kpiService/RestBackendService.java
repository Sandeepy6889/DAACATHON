package com.siemens.primecult.kpiService;


import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path("/KPI-Calculation/*")
public class RestBackendService {

	@POST
	@Path("/users")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.TEXT_PLAIN)
	public String getUsers(String plainText) {
		//Object obj = new ObjectToJSonMapper().mapJSonStrToObj(plainText,ValueRt.class);
		System.out.println(plainText);
		return plainText;
	}
	
	
	@GET
	@Path("/test")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.TEXT_PLAIN)
	public String getKPI() {
		//Object obj = new ObjectToJSonMapper().mapJSonStrToObj(plainText,ValueRt.class);
		System.out.println("HELLO");
		return "Hello";
	}
/*	private void getValueRt() throws IOException {
		 double[] v=  {9.0, 0.28614743881722116, 0.0, 0.0};
		ValueRt val = new ValueRt("1", 13166690909000l, v);
		System.out.println(new ObjectToJSonMapper().mapObjToJSonStr(val));
	}
	
	public static void main(String[] args) throws IOException {
		 double[] v=  {9.0, 0.28614743881722116, 0.0, 0.0};
			ValueRt val = new ValueRt("1", 13166690909000l, v);
			System.out.println(new ObjectToJSonMapper().mapObjToJSonStr(val));
	}*/
}
