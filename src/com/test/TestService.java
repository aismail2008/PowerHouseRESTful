package com.test;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONException;
import org.json.JSONObject;

@Path("/testservice")
public class TestService {

	@POST
	@Path("/add")
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public Response add(JSONObject data) {
		JSONObject result = null;
		try {
			result = new JSONObject(data);
			result.append("test", "succesfully");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return Response.status(Status.OK).entity(result.toString()).build();
	}
	
	
	@GET
	@Path("/test")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response add() {
		JSONObject result = null;
		try {
			result.append("test", "succesfully");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return Response.status(Status.OK).entity(result.toString()).build();
	}
}
