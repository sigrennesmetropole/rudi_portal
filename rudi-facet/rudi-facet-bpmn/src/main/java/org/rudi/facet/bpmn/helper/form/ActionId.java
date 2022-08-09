package org.rudi.facet.bpmn.helper.form;

import lombok.Data;

@Data
public class ActionId {
	public final String processDefinitionId;
	public final String userTaskId;
	public final String actionName;
}
