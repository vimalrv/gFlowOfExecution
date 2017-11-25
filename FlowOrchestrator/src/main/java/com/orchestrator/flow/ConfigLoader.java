package com.orchestrator.flow;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orchestrator.base.FlowVO;
import com.orchestrator.handlers.CsvHandler;

public class ConfigLoader {
	
	private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);

	private static HashMap<String, String[]> componentMetadata = new HashMap<String, String[]>();
	private static HashMap<String, ArrayList<FlowVO>> allFlows = new HashMap<String, ArrayList<FlowVO>>();

	public static void loadComponentMetadata() throws Exception {
		if (componentMetadata != null && !componentMetadata.isEmpty()) {
			return;
		}

		Iterable<CSVRecord> csvIter = CsvHandler.readFromCSV("./config/deployed-components.csv");
		for (CSVRecord record : csvIter) {
			Map<String, String> csvMap = record.toMap();

			String componentID = csvMap.get("ComponentID");
			if (componentID != null && componentID.length() > 0) {
				String[] params = new String[3];

				params[0] = csvMap.get("CompClassName");
				params[1] = csvMap.get("ExpectedInputParameters");
				params[2] = csvMap.get("ResponseFields");

				componentMetadata.put(componentID, params);
			}
		}
	}

	public static ArrayList<FlowVO> constructFlowObjects(String flowName) throws Exception {
		// make sure that we have loaded the component metadata / config
		loadComponentMetadata();

		HashMap<String, ArrayList<FlowVO>> allFlows = getFlowOfExecutionFromPowerUser();

		ArrayList<FlowVO> flowList = allFlows.get(flowName);

		if (flowList == null) {
			throw new IllegalArgumentException("flow name provided is not supported:" + flowName);
		}

		ArrayList<FlowVO> returnList = new ArrayList<FlowVO>();
		for (int i = 0; i < flowList.size(); i++) {
			FlowVO flow = flowList.get(i);
			String[] params = componentMetadata.get(flow.componentID);
			flow.compClassName = params[0];
			String[] inputFields = params[1].split("#");

			flow.defaultInputFieldNames = Arrays.asList(inputFields);
			returnList.add(flow);
		}

		return returnList;
	}

	public static HashMap<String, ArrayList<FlowVO>> getFlowOfExecutionFromPowerUser() throws Exception {
		if (!allFlows.isEmpty()) {
			return allFlows;
		}

		File configFolder = new File("./config");
		logger.debug("power user config absolute folder path:" + configFolder.getAbsolutePath());

		File[] listOfFiles = configFolder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			File configFile = listOfFiles[i];
			if (!configFile.isDirectory()) {
				logger.debug("config file path:" + configFile.getAbsolutePath());

				ArrayList<FlowVO> flowExecution = new ArrayList<FlowVO>();
				Iterable<CSVRecord> csvIter = CsvHandler.readFromCSV(configFile.getAbsolutePath());
				for (CSVRecord record : csvIter) {
					Map<String, String> csvMap = record.toMap();

					FlowVO flow = new FlowVO();
					String componentID = csvMap.get("ComponentID");
					if (componentID != null && componentID.length() > 0) {
						flow.setComponentID(csvMap.get("ComponentID"));

						String callType = csvMap.get("CallType");
						if ("ASYNC".equalsIgnoreCase(callType)) {
							flow.asyncCall = true;
						} else {
							flow.asyncCall = false;
						}

						flow.flowName = csvMap.get("FlowName");

						flow.dataTransformationRules = csvMap.get("DataTransformationRules");
						flowExecution.add(flow);
					}
				}

				allFlows.put(configFile.getName().split("\\.")[0], flowExecution);
			}
		}

		return allFlows;
	}
}
