package com.flow.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.UntypedActor;

public class InsertEmployeeDB extends UntypedActor {
	
	private static final Logger logger = LoggerFactory.getLogger(InsertEmployeeDB.class);

	@Override
	public void onReceive(Object msg) throws Exception {
		String empID = "";
		String response = "";
		if (msg instanceof String) {
			logger.debug("@ IE - Input received in the actor:" + msg);

			empID = callEmployeeInsertComponent((String) msg);

			response = "{" + "\"status\":\"OK\"" + ",\"employeeID\":\"" + empID + "\"" + "}";

		} else {
			throw new RuntimeException("Failed : unsupported input format, input needs to be in JSON format.");
		}

		getSender().tell(response, getSender());
	}

	/***
	 * mocked method to depict a call to a real component, I have not created the real component in this sample project,
	 * but idea is to hook the component call here
	 ***/
	public String callEmployeeInsertComponent(String jsonInput) {
		// presuming a component will be called from here to insert the employee record using JDBC and send back the
		// employee id that has been created in the database .
		return "" + Math.ceil(Math.random()) * 3;
	}

}
