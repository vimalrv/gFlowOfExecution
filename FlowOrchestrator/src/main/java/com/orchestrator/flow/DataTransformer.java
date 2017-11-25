package com.orchestrator.flow;

import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;

public class DataTransformer {

	public static String[] extractValue(String element[], HashMap<String, Object> totalParameterMap) {
		String elementType = element[0];
		String dataType = element[1];
		String valueOrFieldName = element[2];
		String finalValue = "";

		if (valueOrFieldName != null && "field".equalsIgnoreCase(elementType)) {

			Object value = totalParameterMap.get(valueOrFieldName);
			if (value != null && value instanceof JsonNode) {
				finalValue = ((JsonNode) value).asText();
			} else {
				finalValue = value.toString();
			}
		} else if ("constant".equalsIgnoreCase(elementType)) {
			finalValue = valueOrFieldName;
		} else {
			throw new IllegalArgumentException("valueType provided not supported. valueType:" + elementType + ", value:"
					+ valueOrFieldName);
		}
		return new String[] { dataType, finalValue };
	}

	public static HashMap<String, Object> tranformAndPrepareInput(HashMap<String, Object> totalParameterMap,
			String transformationRules) throws IllegalArgumentException {

		if (transformationRules == null || transformationRules.length() < 1) {
			return null;
		}

		HashMap<String, Object> addOnParameters = new HashMap<String, Object>();

		String[] rulesArr = transformationRules.split("\\$");
		for (int i = 0; i < rulesArr.length; i++) {
			String transformationRule = rulesArr[i];
			String[] ruleBreakupArr = transformationRule.split("@");

			String targetFieldName = ruleBreakupArr[0];
			String transformationLogic = ruleBreakupArr[1];
			String finalValue = "";

			String[] logicArr = transformationLogic.split("\\|");

			for (int k = 0; k < logicArr.length; k++) {
				String[] rightElements;

				// understand the transformation logic...
				String[] fields = logicArr[k].split("#");
				int j = 0;
				if (k == 0) {
					String[] leftElements = extractValue(fields[0].split("="), totalParameterMap);
					finalValue = leftElements[1];
					j++;
					// To ensure we don't encounter IndexOutOfBounds situation, as this could be a field mapping alone.
					if (fields.length <= 1) {
						break;
					}
				}

				String operator = fields[j];

				j++;
				rightElements = extractValue(fields[j].split("="), totalParameterMap);

				// process the transformation logic...
				if ("number".equalsIgnoreCase(rightElements[0])) {
					double numberValue = Double.parseDouble(rightElements[1]);
					if ("plus".equals(operator)) {
						numberValue = numberValue + Double.valueOf(rightElements[1]);
					} else if ("minus".equals(operator)) {
						numberValue = numberValue - Double.valueOf(rightElements[1]);
					} else if ("multiply".equalsIgnoreCase(operator)) {
						numberValue = numberValue * Double.valueOf(rightElements[1]);
					} else if ("divide".equalsIgnoreCase(operator)) {
						numberValue = numberValue / Double.valueOf(rightElements[1]);
					} else {
						throw new IllegalArgumentException(
								"operator provided for number data type is not supported. Operator:" + operator
										+ ", value:" + finalValue);
					}
					finalValue = String.valueOf(numberValue);
				} else if ("string".equalsIgnoreCase(rightElements[0])) {
					if ("concat".equalsIgnoreCase(operator)) {
						finalValue = finalValue + rightElements[1];
					} else {
						throw new IllegalArgumentException(
								"operator provided for string data type is not supported. Operator:" + operator
										+ ", value:" + rightElements[1]);
					}
				} else {
					throw new IllegalArgumentException("data type provided for is not supported. datatype:"
							+ rightElements[0] + ", sourceFieldName:" + rightElements[1]);
				}
			}

			// post transformation, set the value for the target field name and set that into the addons map
			addOnParameters.put(targetFieldName, finalValue);
		}
		return addOnParameters;
	}

}
