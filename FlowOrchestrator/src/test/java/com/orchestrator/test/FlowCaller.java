package com.orchestrator.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orchestrator.FlowOrchestrator;

public class FlowCaller {
	
	private static final Logger logger = LoggerFactory.getLogger(FlowCaller.class);

	public static void main(String[] args) throws Exception {
		logger.debug(">>> Flow Caller - starting the flow of execution >>>");
		
		//+ "\"flow-name\":\"flow-config-create-employee\""
		//+ "\"flow-name\":\"flow-config-get-employee\""
		
		String input = "{"		
				+ "\"flow-name\":\"flow-config-create-employee\""
				+ ",\"passportNumber\":\"ABC12364781\""
				+ ",\"firstname\":\"Vimalasekar\""
				+ ",\"lastname\":\"Rajendran\""
				+ ",\"dateOfBirth\":\"05-nov-1982\""
				+ ",\"nationality\":\"Indian\""
				+ ",\"expiryDate\":\"XXXXXXX\""
				+ ",\"address\":\"#28, Nehru Street, Gandhi Nagar, Chennai\""
				+ "}";

		// if the caller wants to call the flow as a synchronous flow
		String result = FlowOrchestrator.synchronousFlow("flow-config-create-employee",input);
		logger.debug(">>> Flow Caller - Result synch flow  - " + result);

		// if the caller wants to call the flow as an aync-flow
		//FlowOrchestrator.asyncFlow("flow-config-create-employee",input);

		logger.debug(">>> Flow Caller - Finished the flow...");
	}
}
