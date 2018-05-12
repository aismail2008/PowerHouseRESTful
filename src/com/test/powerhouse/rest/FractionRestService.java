package com.test.powerhouse.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Calendar;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import com.test.powerhouse.BaseService;
import com.test.powerhouse.OperationStatusEnum;
import com.test.powerhouse.service.FractionService;

@Path("/fractions")
public class FractionRestService extends BaseService{
	private static final String DOWNLOAD_FOLDER = "C:\\input\\download\\";
	
	// ---------------------------GET------------------------------//
	@GET
	@Path("/getprofile")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getFractionValueList(@QueryParam("profileName") String profileName) {
		Response response;
		JSONObject result = new JSONObject();
		try {
			result.accumulate("Value", Arrays.toString(FractionService.getAllFractionValues(profileName)));
			response = Response.status(Status.OK).entity(result.toString()).build();
		} catch (Exception e) {
			e.printStackTrace();
			 response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(OperationStatusEnum.ERROR.getCode()).build();
		}
		return response;
	}

	@GET
	@Path("/getfraction")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFractionValue(@QueryParam("month") String monthName, @QueryParam("profileName") String profileName) {
		JSONObject result = new JSONObject();
		Response response;
		try {
			result.accumulate("Value", FractionService.getFractionValue(profileName, getMonth(monthName)));
			
			response = Response.status(Status.OK).entity(result.toString()).build();
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(OperationStatusEnum.ERROR.getCode()).build();
		}
		return response;
	}

	// ---------------------------CREATE------------------------------//
	@POST
	@Path("/uploadfractionsfile")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(@FormDataParam("file") InputStream uploadInputStream, @FormDataParam("file") FormDataContentDisposition fileDetail) {
		String uploadedFileLocation = DOWNLOAD_FOLDER + fileDetail.getFileName().substring(0, fileDetail.getFileName().lastIndexOf(".")) + Calendar.getInstance().getTimeInMillis() + fileDetail.getFileName().substring(fileDetail.getFileName().lastIndexOf("."));
		Response response = Response.status(Status.OK).entity(OperationStatusEnum.SUCESSS.getCode()).build();
		try {
			OutputStream out = new FileOutputStream(new File(uploadedFileLocation));
			int read = 0;
			byte[] bytes = new byte[1024];

			out = new FileOutputStream(new File(uploadedFileLocation));
			while ((read = uploadInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
			
			String errorLog = FractionService.importData(uploadedFileLocation);
			
			if(errorLog != null){
				File f = new File(errorLog);
				ResponseBuilder responseBuilder = Response.ok((Object) f);
				responseBuilder.header("Content-Dispostion", "attachment; filename=DisplayName-DemoFile.txt");
				response = responseBuilder.build();
			}
		} catch (IOException e) {
			e.printStackTrace();
			Response.status(Status.INTERNAL_SERVER_ERROR).entity(OperationStatusEnum.ERROR.getCode()).build();
		}
		return response;
	}
	
	@POST
	@Path("/createfractionlist")
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public Response addFractionValueList(String data) {
		Response response = Response.status(Status.OK).entity(OperationStatusEnum.SUCESSS.getCode()).build();
		try {
			JSONArray jsonarray = new JSONArray(data);
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject jsonobject = jsonarray.getJSONObject(i);
				FractionService.addFractionValue(getMonth(jsonobject.getString("month")), jsonobject.getString("profileName"), Float.valueOf(jsonobject.getString("fraction")));
			}
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(OperationStatusEnum.ERROR.getCode()).build();
		}
		return response;
	}

	@POST
	@Path("/createfraction")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addFractionValue(@QueryParam("month") String monthName, @QueryParam("profileName") String profileName, @QueryParam("fraction") String fraction) {
		Response response = Response.status(Status.OK).entity(OperationStatusEnum.SUCESSS.getCode()).build();
		try {
			FractionService.addFractionValue(getMonth(monthName), profileName, Float.valueOf(fraction));
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(OperationStatusEnum.ERROR.getCode()).build();
		}
		return response;
	}

	// ---------------------------UPDATE------------------------------//
	@PUT
	@Path("/updatefractionlist")
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public Response updateFractionList(String data) {
		Response response = Response.status(Status.OK).entity(OperationStatusEnum.SUCESSS.getCode()).build();
		try {
			JSONArray jsonarray = new JSONArray(data);
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject jsonobject = jsonarray.getJSONObject(i);
				FractionService.updateFractionValue(getMonth(jsonobject.getString("month")), jsonobject.getString("profileName"), Float.valueOf(jsonobject.getString("fraction")));
			}
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(OperationStatusEnum.ERROR.getCode()).build();
		}
		return response;
	}

	@PUT
	@Path("/updatefraction")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateFraction(@QueryParam("month") String monthName, @QueryParam("profileName") String profileName, @QueryParam("fraction") String fraction) {
		Response response = Response.status(Status.OK).entity(OperationStatusEnum.SUCESSS.getCode()).build();
		try {
			FractionService.updateFractionValue(getMonth(monthName), profileName, Float.valueOf(fraction));
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(OperationStatusEnum.ERROR.getCode()).build();
		}
		return response;
	}

	// ---------------------------DELETE------------------------------//
	@DELETE
	@Path("/delfractionlist")
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public Response deleteFractionList(String data) {
		Response response = Response.status(Status.OK).entity(OperationStatusEnum.SUCESSS.getCode()).build();
		try {
			JSONArray jsonarray = new JSONArray(data);
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject jsonobject = jsonarray.getJSONObject(i);
				FractionService.deleteFractionValue(jsonobject.getString("profileName"), getMonth(jsonobject.getString("month")));
			}
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(OperationStatusEnum.ERROR.getCode()).build();
		}
		return response;
	}

	@DELETE
	@Path("/delfraction")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteFractionValue(@QueryParam("profileName") String profileName, @QueryParam("month") String monthName) {
		Response response = Response.status(Status.OK).entity(OperationStatusEnum.SUCESSS.getCode()).build();
		try {
			FractionService.deleteFractionValue(profileName, getMonth(monthName));
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(OperationStatusEnum.ERROR.getCode()).build();
		}
		return response;
	}

	@DELETE
	@Path("/delprofilelist")
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public Response deleteProfileList(String data) {
		Response response = Response.status(Status.OK).entity(OperationStatusEnum.SUCESSS.getCode()).build();
		try {
			JSONArray jsonarray = new JSONArray(data);
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject jsonobject = jsonarray.getJSONObject(i);
				FractionService.deleteProfile(jsonobject.getString("profileName"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(OperationStatusEnum.ERROR.getCode()).build();
		}
		return response;
	}

	@DELETE
	@Path("/delprofile")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteProfile(@QueryParam("profileName") String profileName) {
		Response response = Response.status(Status.OK).entity(OperationStatusEnum.SUCESSS.getCode()).build();
		try {
			FractionService.deleteProfile(profileName);
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(OperationStatusEnum.ERROR.getCode()).build();
		}
		return response;
	}
}