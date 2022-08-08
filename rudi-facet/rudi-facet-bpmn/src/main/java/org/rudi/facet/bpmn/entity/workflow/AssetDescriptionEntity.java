/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.entity.workflow;

import java.time.LocalDateTime;

import org.rudi.bpmn.core.bean.Status;
import org.rudi.common.core.LongId;

/**
 * @author FNI18300
 *
 */
public interface AssetDescriptionEntity extends LongId {

	Status getStatus();

	String getFunctionalStatus();

	String getInitiator();

	String getUpdator();

	LocalDateTime getCreationDate();

	LocalDateTime getUpdatedDate();

	String getDescription();

	String getData();

	String getAssignee();

	String getProcessDefinitionKey();

	Integer getProcessDefinitionVersion();

	void setStatus(Status status);

	void setFunctionalStatus(String functionalStatus);

	void setInitiator(String initiator);

	void setUpdator(String updator);

	void setCreationDate(LocalDateTime creationDate);

	void setUpdatedDate(LocalDateTime updatedDate);

	void setDescription(String assignee);

	void setData(String assignee);

	void setAssignee(String assignee);

	void setProcessDefinitionKey(String businessKey);

	void setProcessDefinitionVersion(Integer version);

}
