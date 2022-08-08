/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.entity.workflow;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

import org.rudi.bpmn.core.bean.Status;
import org.rudi.common.storage.entity.AbstractLongIdEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * @author FNI18300
 *
 */
@MappedSuperclass
@Setter
@Getter
@ToString
@NoArgsConstructor
@SuperBuilder
public abstract class AbstractAssetDescriptionEntity extends AbstractLongIdEntity implements AssetDescriptionEntity {

	private static final long serialVersionUID = -7893084539992454721L;

	@Column(name = "process_definition_key", length = 150, nullable = false)
	private String processDefinitionKey;

	@Column(name = "process_definition_version")
	private Integer processDefinitionVersion;

	@Column(name = "status", length = 20, nullable = false)
	@Enumerated(EnumType.STRING)
	private Status status = Status.DRAFT;

	@Column(name = "functional_status", length = 50, nullable = false)
	private String functionalStatus;

	@Column(name = "initiator", length = 100, nullable = false)
	private String initiator;

	@Column(name = "updator", length = 100)
	private String updator;

	@Column(name = "creation_date", nullable = false)
	private LocalDateTime creationDate;

	@Column(name = "updated_date")
	private LocalDateTime updatedDate;

	@Column(name = "description", length = 1024)
	private String description;

	@Column(name = "assignee", length = 100)
	private String assignee;

	@Column(name = "data")
	private String data;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((getFunctionalStatus() == null) ? 0 : getFunctionalStatus().hashCode());
		result = prime * result + ((getInitiator() == null) ? 0 : getInitiator().hashCode());
		result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
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
		if (!(obj instanceof AbstractAssetDescriptionEntity)) {
			return false;
		}
		AbstractAssetDescriptionEntity other = (AbstractAssetDescriptionEntity) obj;
		if (getFunctionalStatus() == null) {
			if (other.getFunctionalStatus() != null) {
				return false;
			}
		} else if (!getFunctionalStatus().equals(other.getFunctionalStatus())) {
			return false;
		}
		if (getInitiator() == null) {
			if (other.getInitiator() != null) {
				return false;
			}
		} else if (!getInitiator().equals(other.getInitiator())) {
			return false;
		}
		if (getStatus() != other.getStatus()) {
			return false;
		}
		return true;
	}

}
