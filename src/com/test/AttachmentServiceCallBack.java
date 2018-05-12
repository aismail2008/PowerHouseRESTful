package com.test;

import java.io.File;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.json.JSONObject;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("/calculator")
public class AttachmentServiceCallBack {
	@POST
	@Path("/multi")
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public Response multi(String data) {
		JSONObject result = null;
		try {
			result = new JSONObject(data);
			Long a = Long.valueOf(result.getString("a"));
			Long b = Long.valueOf(result.getString("b"));

			result = new JSONObject();
			result.put("RESULT", "success multi");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.status(Status.OK).entity(result.toString()).build();
	}

	/**
	 * URL in this format : http://aaadsfl/rest/calculator/multi2?param1=ad&param2=asdf
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	@POST
	@Path("/multi2")
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public Response multi2(@QueryParam("param1") String p1, @QueryParam("param2") String p2) {
		JSONObject result = new JSONObject();
		try {
			result.put("RESULT", "success multi2");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.status(Status.OK).entity(result.toString()).build();
	}

	/**
	 * URL in this format : http://aaadsfl/rest/calculator/minus/25/36
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	@GET
	@Path("/minus/{param1}/{param2}")
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public Response minus(@PathParam("param1") String p1, @PathParam("param2") String p2) {
		JSONObject result = null;
		try {
			result = new JSONObject();

			result = new JSONObject();
			result.put("RESULT", Long.valueOf(p1) - Long.valueOf(p2));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.status(Status.OK).entity(result.toString()).build();
	}

	/**
	 * URL in this format : http://aaadsfl/rest/calculator/minus2;param1=25;param2=36
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	@GET
	@Path("/minus2")
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public Response minus2(@MatrixParam("param1") String p1, @MatrixParam("param2") String p2) {
		JSONObject result = null;
		try {
			result = new JSONObject();

			result = new JSONObject();
			result.put("RESULT", Long.valueOf(p1) - Long.valueOf(p2));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.status(Status.OK).entity(result.toString()).build();
	}

	/**
	 * URL in this format : html page with <form action ="http://aaadsfl/rest/calculator/minus3" method =post> <input name=param1/> ...etc </form>
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	@GET
	@Path("/minus3")
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public Response minus3(@FormParam("param1") String p1, @FormParam("param2") String p2) {
		JSONObject result = null;
		try {
			result = new JSONObject();

			result = new JSONObject();
			result.put("RESULT", Long.valueOf(p1) - Long.valueOf(p2));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.status(Status.OK).entity(result.toString()).build();
	}

	@GET
	@Path("/getFile")
	public Response saveTextFile() {
		File f = new File("c:\\aa.txt");
		ResponseBuilder response = Response.ok((Object) f);
		response.header("Content-Dispostion", "attachment; filename=DisplayName-DemoFile.txt");
		return response.build();
	}

	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String uploadFile(@FormDataParam("file") InputStream uploadInputStream, @FormDataParam("file") FormDataContentDisposition fileDetail) {

		// here use input stream to save file

		return "success";
	}

}