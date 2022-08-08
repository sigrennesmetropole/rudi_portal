package org.rudi.microservice.projekt.storage.entity.newdatasetrequest;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.rudi.facet.bpmn.entity.workflow.AbstractAssetDescriptionEntity;
import org.rudi.microservice.projekt.core.common.SchemaConstants;
import org.rudi.microservice.projekt.storage.entity.ProjectTypeEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "new_dataset_request", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
@ToString
public class NewDatasetRequestEntity extends AbstractAssetDescriptionEntity {

	private static final long serialVersionUID = -4643950814750418854L;

	@Column(name = "new_dataset_request_status", length = 20, nullable = false)
	@Enumerated(EnumType.STRING)
	private NewDatasetRequestStatus newDatasetRequestStatus = NewDatasetRequestStatus.DRAFT;

	/**
	 * Titre
	 */
	@Column(name = "title", length = 150, nullable = false)
	private String title;

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ProjectTypeEntity)) {
			return false;
		}
		return super.equals(obj);
	}
}
