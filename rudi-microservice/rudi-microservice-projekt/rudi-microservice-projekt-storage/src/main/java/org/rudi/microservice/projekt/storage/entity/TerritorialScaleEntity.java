package org.rudi.microservice.projekt.storage.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.rudi.common.storage.entity.AbstractStampedEntity;
import org.rudi.microservice.projekt.core.common.SchemaConstants;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Échelle du territoire
 */
@Entity
@Table(name = "territorial_scale", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
@ToString
public class TerritorialScaleEntity extends AbstractStampedEntity {

	private static final long serialVersionUID = 2789162205673165295L;

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof TerritorialScaleEntity)) {
			return false;
		}
		return super.equals(obj);
	}

}
