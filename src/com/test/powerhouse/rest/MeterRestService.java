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
import com.test.powerhouse.service.MeterService;

@Path("/meterreading")
public class MeterRestService extends BaseService{
	private static final String DOWNLOAD_FOLDER = "C:\\input\\download\\";
	
	// ---------------------------GET------------------------------//
	@GET
	@Path("/consumption")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getConsumption(@QueryParam("meterId") String meterId, @QueryParam("month") String monthName) {
		JSONObject result = new JSONObject();
		Response response;
		try {
			result.accumulate("Value", MeterService.getConsumption(meterId, getMonth(monthName)));
			
			response = Response.status(Status.OK).entity(result.toString()).build();
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(OperationStatusEnum.ERROR.getCode()).build();
		}
		return response;
	}
	
	@GET
	@Path("/getmeter")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getMeterReadingList(@QueryParam("meterId") String meterId) {
		Response response;
		JSONObject result = new JSONObject();
		try {
			result.accumulate("Value", Arrays.toString(MeterService.getAllMeterReadings(meterId)));
			response = Response.status(Status.OK).entity(result.toString()).build();
		} catch (Exception e) {
			e.printStackTrace();
			 response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(OperationStatusEnum.ERROR.getCode()).build();
		}
		return response;
	}

	@GET
	@Path("/getreading")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getReadingValue(@QueryParam("meterId") String meterId, @QueryParam("month") String monthName) {
		JSONObject result = new JSONObject();
		Response response;
		try {
			result.accumulate("Value", MeterService.getMeterReadings(meterId,  getMonth(monthName)));
			
			response = Response.status(Status.OK).entity(result.toString()).build();
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(OperationStatusEnum.ERROR.getCode()).build();
		}
		return response;
	}

	// ---------------------------CREATE------------------------------//
	@POST
	@Path("/uploadreadingsfile")
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
			
			String errorLog = MeterService.importData(uploadedFileLocation);
			
			if(errorLog != null){
				File f = new File(errorLog);
				ResponseBuilder responseBuilder = Response.ok((Object) f);
				responseBuilder.header("Content-Dispostion", "attachment; filename=DisplayName-errorLog.log");
				response = responseBuilder.build();
			}
		} catch (IOException e) {
			e.printStackTrace();
			Response.status(Status.INTERNAL_SERVER_ERROR).entity(OperationStatusEnum.ERROR.getCode()).build();
		}
		return response;
	}
	
	@POST
	@Path("/createreadinglist")
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public Response addReadingValueList(String data) {
		Response response = Response.status(Status.OK).entity(OperationStatusEnum.SUCESSS.getCode()).build();
		try {
			JSONArray jsonarray = new JSONArray(data);
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject jsonobject = jsonarray.getJSONObject(i);
				MeterService.addReading(jsonobject.getString("meterId"), jsonobject.getString("profileName"), getMonth(jsonobject.getString("month")), Integer.valueOf(jsonobject.getString("reading")));
			}
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(OperationStatusEnum.ERROR.getCode()).build();
		}
		return response;
	}

	@POST
	@Path("/createreading")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addReadingValue(@QueryParam("meterId") String meterId, @QueryParam("profileName") String profileName, @QueryParam("month") String monthName, @QueryParam("reading") String reading) {
		Response response = Response.status(Status.OK).entity(OperationStatusEnum.SUCESSS.getCode()).build();
		try {
			MeterService.addReading(meterId, profileName, getMonth(monthName), Integer.valueOf(reading));
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(OperationStatusEnum.ERROR.getCode()).build();
		}
		return response;
	}

	// ---------------------------UPDATE------------------------------//
	@PUT
	@Path("/updatereadinglist")
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public Response updateReadingList(String data) {
		Response response = Response.status(Status.OK).entity(OperationStatusEnum.SUCESSS.getCode()).build();
		try {
			JSONArray jsonarray = new JSONArray(data);
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject jsonobject = jsonarray.getJSONObject(i);
				MeterService.updateReading(jsonobject.getString("meterId"), jsonobject.getString("profileName"), getMonth(jsonobject.getString("month")), Integer.valueOf(jsonobject.getString("reading")));
			}
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(OperationStatusEnum.ERROR.getCode()).build();
		}
		return response;
	}

	@PUT
	@Path("/updateReading")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateReading(@QueryParam("meterId") String meterId, @QueryParam("profileName") String profileName, @QueryParam("month") String monthName, @QueryParam("reading") String reading) {
		Response response = Response.status(Status.OK).entity(OperationStatusEnum.SUCESSS.getCode()).build();
		try {
			MeterService.updateReading(meterId, profileName, getMonth(monthName), Integer.valueOf(reading));
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(OperationStatusEnum.ERROR.getCode()).build();
		}
		return response;
	}

	// ---------------------------DELETE------------------------------//
	@DELETE
	@Path("/delreadinglist")
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public Response deleteReadingList(String data) {
		Response response = Response.status(Status.OK).entity(OperationStatusEnum.SUCESSS.getCode()).build();
		try {
			JSONArray jsonarray = new JSONArray(data);
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject jsonobject = jsonarray.getJSONObject(i);
				MeterService.deleteReading(jsonobject.getString("meterId"), getMonth(jsonobject.getString("month")));
			}
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(OperationStatusEnum.ERROR.getCode()).build();
		}
		return response;
	}

	@DELETE
	@Path("/delreading")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteReadingValue(@QueryParam("meterId") String meterId, @QueryParam("month") String monthName) {
		Response response = Response.status(Status.OK).entity(OperationStatusEnum.SUCESSS.getCode()).build();
		try {
			MeterService.deleteReading(meterId, getMonth(monthName));
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(OperationStatusEnum.ERROR.getCode()).build();
		}
		return response;
	}

	@DELETE
	@Path("/delmeterlist")
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public Response deleteMeterList(String data) {
		Response response = Response.status(Status.OK).entity(OperationStatusEnum.SUCESSS.getCode()).build();
		try {
			JSONArray jsonarray = new JSONArray(data);
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject jsonobject = jsonarray.getJSONObject(i);
				MeterService.deleteMeter(jsonobject.getString("meterId"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(OperationStatusEnum.ERROR.getCode()).build();
		}
		return response;
	}

	@DELETE
	@Path("/delmeter")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteMeter(@QueryParam("meterId") String meterId) {
		Response response = Response.status(Status.OK).entity(OperationStatusEnum.SUCESSS.getCode()).build();
		try {
			MeterService.deleteMeter(meterId);
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(OperationStatusEnum.ERROR.getCode()).build();
		}
		return response;
	}
}