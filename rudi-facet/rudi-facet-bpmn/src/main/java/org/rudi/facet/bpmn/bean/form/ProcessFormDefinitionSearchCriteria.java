/**
 * 
 */
package org.rudi.facet.bpmn.bean.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * @author FNI18300
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessFormDefinitionSearchCriteria {

	private UUID uuid;

	private String processDefinitionId;

	private Integer revision;

	private boolean acceptFlexRevision;

	private String userTaskId;

	private boolean acceptFlexUserTaskId;

	private String actionName;

	private boolean acceptFlexActionName;

}
