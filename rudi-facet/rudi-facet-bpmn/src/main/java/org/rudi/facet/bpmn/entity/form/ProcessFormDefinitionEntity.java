/**
 * 
 */
package org.rudi.facet.bpmn.entity.form;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.rudi.common.storage.entity.AbstractLongIdEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author FNI18300
 *
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "process_form_definition")
public class ProcessFormDefinitionEntity extends AbstractLongIdEntity {

	private static final long serialVersionUID = -934341416359414671L;

	@Column(name = "process_definition_id", nullable = false, length = 64)
	private String processDefinitionId;

	@Column(name = "revision")
	private Integer revision;

	@Column(name = "user_task_id", length = 64)
	private String userTaskId;

	@Column(name = "action_name", length = 64)
	private String actionName;

	@ManyToOne
	@JoinColumn(name = "form_definition_fk")
	private FormDefinitionEntity formDefinition;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((getProcessDefinitionId() == null) ? 0 : getProcessDefinitionId().hashCode());
		result = prime * result + ((getRevision() == null) ? 0 : getRevision().hashCode());
		result = prime * result + ((getActionName() == null) ? 0 : getActionName().hashCode());
		result = prime * result + ((getUserTaskId() == null) ? 0 : getUserTaskId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof ProcessFormDefinitionEntity)) {
			return false;
		}
		ProcessFormDefinitionEntity other = (ProcessFormDefinitionEntity) obj;
		if (getProcessDefinitionId() == null) {
			if (other.getProcessDefinitionId() != null) {
				return false;
			}
		} else if (!getProcessDefinitionId().equals(other.getProcessDefinitionId())) {
			return false;
		}
		if (getActionName() == null) {
			if (other.getActionName() != null) {
				return false;
			}
		} else if (!getActionName().equals(other.getActionName())) {
			return false;
		}
		if (getRevision() == null) {
			if (other.getRevision() != null) {
				return false;
			}
		} else if (!getRevision().equals(other.getRevision())) {
			return false;
		}
		if (getUserTaskId() == null) {
			if (other.getUserTaskId() != null) {
				return false;
			}
		} else if (!getUserTaskId().equals(other.getUserTaskId())) {
			return false;
		}
		return true;
	}
}
