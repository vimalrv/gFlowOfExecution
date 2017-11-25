package com.orchestrator.base;

import java.util.HashMap;
import java.util.List;

public class FlowVO {

	// fields to be filled from the flow diagram from the power user.
	public String flowName;
	public String componentID;
	public boolean asyncCall;
	public String dataTransformationRules;

	// fields to be filled from the deployment configuration of the component
	public String compClassName;
	public List<String> defaultInputFieldNames;

	// fields and values that would be sent to the components during execution of the flow
	public HashMap<String, Object> inputParameterMap = new HashMap<String, Object>();
	
	// additional new fields and values that would be sent to the components during execution of the flow
	//public HashMap<String, String> addonParameterMap = new HashMap<String, String>();
	
	public void setComponentID(String compID) {
		this.componentID = compID;
	}

}
