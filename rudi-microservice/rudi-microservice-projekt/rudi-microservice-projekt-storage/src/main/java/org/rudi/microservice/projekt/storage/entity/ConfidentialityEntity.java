package org.rudi.microservice.projekt.storage.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.rudi.common.storage.entity.AbstractStampedEntity;
import org.rudi.microservice.projekt.core.common.SchemaConstants;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Caractère confidentiel du projet
 */
@Entity
@Table(name = "confidentiality", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
@ToString
public class ConfidentialityEntity extends AbstractStampedEntity {

	private static final long serialVersionUID = 5760958317776942316L;

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ConfidentialityEntity)) {
			return false;
		}
		return super.equals(obj);
	}

}
