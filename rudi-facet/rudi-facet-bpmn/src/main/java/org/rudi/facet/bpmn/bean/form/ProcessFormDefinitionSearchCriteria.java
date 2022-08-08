/**
 * 
 */
package org.rudi.facet.bpmn.bean.form;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author FNI18300
 *
 */
@Getter
@Setter
@NoArgsConstructor
@Builder
public class ProcessFormDefinitionSearchCriteria {

	private String processDefinitionId;

	private Integer revision;

	private boolean acceptFlexRevision;

	private String userTaskId;

	private boolean acceptFlexUserTaskId;

	private String actionName;

	private boolean acceptFlexActionName;

	public ProcessFormDefinitionSearchCriteria(String processDefinitionId, Integer revision, boolean acceptFlexRevision,
			String userTaskId, boolean acceptFlexUserTaskId, String actionName, boolean acceptFlexActionName) {
		super();
		this.processDefinitionId = processDefinitionId;
		this.revision = revision;
		this.acceptFlexRevision = acceptFlexRevision;
		this.userTaskId = userTaskId;
		this.acceptFlexUserTaskId = acceptFlexUserTaskId;
		this.actionName = actionName;
		this.acceptFlexActionName = acceptFlexActionName;
	}

}
