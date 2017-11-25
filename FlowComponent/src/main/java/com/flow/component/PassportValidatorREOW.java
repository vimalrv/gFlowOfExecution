package com.flow.component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.UntypedActor;

public class PassportValidatorREOW extends UntypedActor {
	
	private static final Logger logger = LoggerFactory.getLogger(PassportValidatorREOW.class);

	@Override
	public void onReceive(Object msg) throws Exception {
		String response = "";
		if (msg instanceof String) {
			logger.debug("@ PPV - Input received in the actor:" + msg);

			// making a dummy call to a public RESTFul service hosted somewhere on the internet to depct a 3rd party
			// RESTFul outward call.
			response = callPOSTService((String) msg);

			// mocking the response to depict relevant data in the employee flow sample
			response = "{" + "\"status\":\"OK\"" + ",\"expiryDate\":\"10-nov-2022\""
					+ ",\"address\":\"#28, Nehru Street, Gandhi Nagar, Chennai\"" + "}";
		} else {
			throw new RuntimeException("Failed : unsupported input format, input needs to be in JSON format.");
		}

		getSender().tell(response, getSender());
	}

	/***
	 * mocked method to depict a call to a real component, I have not created the real component in this sample project,
	 * but idea is to hook the component call here
	 ***/
	public String callPOSTService(String jsonInput) {
		try {
			URL url = new URL("http://validate.jsontest.com/?json=" + jsonInput);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");

			OutputStream os = conn.getOutputStream();
			os.write(jsonInput.getBytes());
			os.flush();

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			logger.debug("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				logger.debug(output);
			}

			conn.disconnect();

			return output;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
