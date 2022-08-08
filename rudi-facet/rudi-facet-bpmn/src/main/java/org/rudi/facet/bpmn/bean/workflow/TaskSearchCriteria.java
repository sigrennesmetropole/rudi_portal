/**
 * 
 */
package org.rudi.facet.bpmn.bean.workflow;

import java.time.LocalDateTime;
import java.util.List;

import org.rudi.bpmn.core.bean.Status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * @author FNI18300
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class TaskSearchCriteria {

	private boolean asAdmin;

	private List<String> processDefinitionKeys;

	private List<Status> status;

	private List<String> functionalStatus;

	private LocalDateTime minCreationDate;

	private LocalDateTime maxCreationDate;

	private String description;

}
