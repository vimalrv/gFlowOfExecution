package com.flow.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.UntypedActor;

public class RetrieveEmployeeDB extends UntypedActor {
	
	private static final Logger logger = LoggerFactory.getLogger(RetrieveEmployeeDB.class);

	@Override
	public void onReceive(Object msg) throws Exception {
		String response = "";
		if (msg instanceof String) {
			logger.debug("@ RETEMP - Input received in the actor:" + msg);

			response = callEmployeeRetrieveComponent((String) msg);
		} else {
			throw new RuntimeException("Failed : unsupported input format, input needs to be in JSON format.");
		}

		getSender().tell(response, getSender());
	}

	/***
	 * mocked method to depict a call to a real component, I have not created the real component in this sample project,
	 * but idea is to hook the component call here
	 ***/
	public String callEmployeeRetrieveComponent(String jsonInput) {
		// presuming a component will be called from here to select/retrieve the employee record using JDBC and send
		// back the
		// employee details from the database as per the component design.

		String dbResult = "{" + "\"displayName\":\"new displayname, from database\""
				+ ",\"dateOfBirth\":\"05-nov-1982-from-db\"" + ",\"passportNumber\":\"ABC12364781-from-db\"" + "}";

		return dbResult;
	}

}
