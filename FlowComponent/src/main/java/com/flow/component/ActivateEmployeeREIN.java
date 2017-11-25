package com.flow.component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.UntypedActor;

public class ActivateEmployeeREIN extends UntypedActor {
	
	private static final Logger logger = LoggerFactory.getLogger(ActivateEmployeeREIN.class);

	@Override
	public void onReceive(Object msg) throws Exception {
		String response = "";
		if (msg instanceof String) {
			logger.debug("@ AE - Input received in the actor:" + msg);
			
			response = callActivateEmpRestINService((String) msg);

			response = "{" + "\"activate-status\":\"SUCCESS\"" + "}";

		} else {
			throw new RuntimeException("Failed : unsupported input format, input needs to be in JSON format.");
		}

		getSender().tell(response, getSender());
	}

	/***
	 * mocked method to depict a call to a real component, I have not created the real component in this sample project,
	 * but idea is to hook the component call here
	 ***/
	public String callActivateEmpRestINService(String jsonInput) {
		String response = "";
		try {
			URL url = new URL("http://ip.jsontest.com/");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			logger.debug("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				response += output;
				logger.debug(output);
			}

			conn.disconnect();

		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed : Malformed URL ");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed : IOException ");
		}

		return response;
	}

}
