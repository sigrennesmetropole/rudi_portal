package org.rudi.microservice.projekt.storage.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.rudi.common.storage.entity.AbstractStampedEntity;
import org.rudi.microservice.projekt.core.common.SchemaConstants;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "reutilisation_status", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
public class ReutilisationStatusEntity extends AbstractStampedEntity {

	private static final long serialVersionUID = -8837224571760331975L;

	@Column(name = "dataset_set_modification_allowed", nullable = false)
	private boolean datasetSetModificationAllowed;

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
