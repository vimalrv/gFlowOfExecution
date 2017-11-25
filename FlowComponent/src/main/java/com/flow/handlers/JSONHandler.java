package com.flow.handlers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(JSONHandler.class);

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
			logger.debug("Key: " + field.getKey() + "\tValue:" + field.getValue());
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

}
