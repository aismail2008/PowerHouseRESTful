package com.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

//import com.sun.jersey.api.client.*;

public class CallRest {

	public static void main(String[] args) {
		try {
			URL targetUrl = new URL("http://localhost:8080/SalesOrder/rest/testservice/add");

			HttpURLConnection httpConnection = (HttpURLConnection) targetUrl.openConnection();

			httpConnection.setDoOutput(true);

			httpConnection.setRequestMethod("POST");

			httpConnection.setRequestProperty("Content-Type", "application/json");

			JSONObject requestEntity = new JSONObject();
			try {
				requestEntity.append("test", "succesfully");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			OutputStream outputStream = httpConnection.getOutputStream();
			outputStream.write(requestEntity.toString().getBytes());
			outputStream.flush();

			if (httpConnection.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + httpConnection.getResponseCode());
			}

			BufferedReader responseBuffer = new BufferedReader(new InputStreamReader((httpConnection.getInputStream())));
			String output;
			System.out.println("Output from Server:\n");

			while ((output = responseBuffer.readLine()) != null) {

				System.out.println(output);

			}

			httpConnection.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}

	}

	// public static void main(String[] args) {
	// Client client = Client.create();
	// WebResource resource =
	// client.resource("http://localhost:8080/SalesOrder/rest/testservice/add");
	//
	// // Get response as String
	// JSONObject requestEntity = new JSONObject();
	// requestEntity.append("test", "succesfully");
	//
	// String string =
	// resource.path("1").accept(MediaType.APPLICATION_JSON).post(requestEntity);
	// System.out.println(string);
	//
	// // Get response as Customer
	// Customer customer =
	// resource.path("1").accept(MediaType.APPLICATION_XML).get(Customer.class);
	// System.out.println(customer.getLastName() + ", " +
	// customer.getFirstName());
	//
	// // Get response as List<Customer>
	// List<Customer> customers =
	// resource.path("findCustomersByCity/Any%20Town").accept(MediaType.APPLICATION_XML).get(new
	// GenericType<List<Customer>>() {
	// });
	// System.out.println(customers.size());
	// }
}
