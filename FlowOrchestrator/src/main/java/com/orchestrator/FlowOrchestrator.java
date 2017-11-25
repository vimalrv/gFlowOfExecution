package com.orchestrator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.orchestrator.base.FlowVO;
import com.orchestrator.flow.ConfigLoader;
import com.orchestrator.flow.DataTransformer;
import com.orchestrator.handlers.JSONHandler;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;

public class FlowOrchestrator extends UntypedActor {
	
	private static final Logger logger = LoggerFactory.getLogger(FlowOrchestrator.class);

	public static String synchronousFlow(String flowConfigName, String input) throws Exception {
		ActorSystem system = ActorSystem.create("synchronous-" + flowConfigName);
		ActorRef flowActor = system.actorOf(Props.create(FlowOrchestrator.class));

		// validateINput();
		Timeout timeout = new Timeout(5, TimeUnit.SECONDS);
		Future<Object> future = Patterns.ask(flowActor, input, timeout);

		// blocks the thread to wait for the response from the actor
		String result = (String) Await.result(future, timeout.duration());

		system.shutdown();

		return result;
	}

	public static void asyncFlow(String flowConfigName, String input) throws Exception {
		ActorSystem system = ActorSystem.create("async-" + flowConfigName);
		ActorRef flowActor = system.actorOf(Props.create(FlowOrchestrator.class));

		// validateINput();
		flowActor.tell(input, ActorRef.noSender());

		// wait for 15 seconds before shutting down the system
		// TimeUnit.SECONDS.sleep(15);
		system.shutdown();
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		// ActorSystem system = ActorSystem.create("flow-execution");
		logger.debug("*** Orchestrator Start, input received:" + msg);

		HashMap<String, JsonNode> inputReceivedMap = JSONHandler.parseJSONUsingJackson((String) msg);
		String result = "async-call-no-result";

		ArrayList<FlowVO> flowList = ConfigLoader.constructFlowObjects(inputReceivedMap.get("flow-name").asText());

		// create a total parameters map to ensure we keep accumulating all parameters, such that all the flows will
		// have all input fields/params
		HashMap<String, Object> totalParametersMap = new HashMap<String, Object>();
		totalParametersMap.putAll(inputReceivedMap);

		for (int i = 0; i < flowList.size(); i++) {
			FlowVO flowVO = flowList.get(i);
			logger.debug("*** Flow Step:" + flowVO.flowName + ", Call Type (per PUser):" + (flowVO.asyncCall
					? "ASYNC" : "Sync"));

			Class compClass = Class.forName(flowVO.compClassName);
			ActorRef flowActor = getContext().actorOf(Props.create(compClass));

			// choose only those fields relevant to this component...
			flowVO.inputParameterMap = getRelevantInputParams(flowVO, totalParametersMap);

			// send off to do the defined data transformations...
			HashMap<String, Object> addOnParameterMap = DataTransformer.tranformAndPrepareInput(totalParametersMap,
					flowVO.dataTransformationRules);
			if (addOnParameterMap != null) {
				// add the transformed data into the input for this component/flow
				flowVO.inputParameterMap.putAll(addOnParameterMap);
				// also add the transformed data into the total params map such that any subsequent flow also has access
				// to this field
				totalParametersMap.putAll(addOnParameterMap);
			}
			logger.debug("addon params post data transformation:" + addOnParameterMap);

			// create a new json string for passing to the components.
			String inputJsonString = JSONHandler.writeJSONUsingJackson(flowVO.inputParameterMap);

			if (flowVO.asyncCall) {
				// if the flow is supposed to be async...
				flowActor.tell(inputJsonString, getContext().self());
			} else {
				// if the flow is supposed to synchronous...
				Timeout timeout = new Timeout(5, TimeUnit.SECONDS);
				Future<Object> future = Patterns.ask(flowActor, inputJsonString, timeout);

				// blocks the thread to wait for the response from the actor
				result = (String) Await.result(future, timeout.duration());

				logger.debug("!!! Call Done:" + flowVO.componentID);

				HashMap<String, JsonNode> responseMap = JSONHandler.parseJSONUsingJackson(result);
				if (responseMap != null) {
					// add the response data/fields received from a flow call to total params map such that any
					// subsequent flow also has access to this field
					totalParametersMap.putAll(responseMap);
				}
			}
		}

		// finally after all the flows are completed, send back the final response for any synchronous caller...
		getSender().tell(getFinalResponse(totalParametersMap), getSender());

		// wait for 15 seconds before shutting down the system
		TimeUnit.SECONDS.sleep(5);
		// system.shutdown();

		logger.debug("===== FlowOrchestrator - Completed the onReceive");
	}

	public HashMap<String, Object> getRelevantInputParams(FlowVO flow, HashMap<String, Object> responseParameterMap)
			throws Exception {
		HashMap<String, Object> inputMap = new HashMap<String, Object>();

		for (int i = 0; i < flow.defaultInputFieldNames.size(); i++) {
			String defFldName = (String) flow.defaultInputFieldNames.get(i);

			String fieldName = defFldName.split("=")[0];

			Object value = responseParameterMap.get(fieldName);
			if (value != null) {
				inputMap.put(fieldName, (JsonNode) value);
			}
		}

		return inputMap;
	}

	private String getFinalResponse(HashMap<String, Object> responseMap) throws Exception {
		return JSONHandler.writeJSONUsingJackson(responseMap);
	}

}
