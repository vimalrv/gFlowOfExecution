package com.orchestrator.handlers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONHandler {
	
	public static HashMap<String, JsonNode> parseJSONUsingJackson(String json) throws Exception {
		if(json.length() < 1) {
			return null;
		}
		
		HashMap<String, JsonNode> parameterMap = new HashMap<String, JsonNode>();
		JsonFactory factory = new JsonFactory();

		ObjectMapper mapper = new ObjectMapper(factory);
		JsonNode rootNode = mapper.readTree(json);

		Iterator<Map.Entry<String, JsonNode>> fieldsIterator = rootNode.fields();
		while (fieldsIterator.hasNext()) {
			Map.Entry<String, JsonNode> field = fieldsIterator.next();
			parameterMap.put(field.getKey(), field.getValue());
		}
		
		return parameterMap;
	}
	
	public static String  writeJSONUsingJackson(HashMap<String, Object> parameterMap) throws Exception {
		
		JsonFactory factory = new JsonFactory();

		ObjectMapper mapper = new ObjectMapper(factory);
		
		String jsonResp = mapper.writeValueAsString(parameterMap);
		
		return jsonResp;
	}

	public static HashMap<String, Object> parseJSON(String myJSONString) {
		HashMap<String, Object> parameterMap = new HashMap<String, Object>();
		JSONObject jsonObj = new JSONObject(myJSONString);
		String[] keys = JSONObject.getNames(jsonObj);

		for (String key : keys) {
			Object value = jsonObj.get(key);
			parameterMap.put(key, value);
			// Determine type of value and do something with it...
		}

		return parameterMap;
	}

}
