/**
 * RUDI Portail
 */
package org.rudi.microservice.projekt.core.bean.workflow;

import org.rudi.facet.bpmn.bean.workflow.TaskSearchCriteria;
import org.rudi.microservice.projekt.core.bean.ProjectStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

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
public class ProjektTaskSearchCriteria extends TaskSearchCriteria {

	private String title;

	private ProjectStatus projectStatus;

	private UUID datasetProducerUuid;

	private UUID projectUuid;
}
