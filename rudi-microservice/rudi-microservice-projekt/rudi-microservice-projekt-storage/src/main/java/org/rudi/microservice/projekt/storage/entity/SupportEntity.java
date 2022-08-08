package org.rudi.microservice.projekt.storage.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.rudi.common.storage.entity.AbstractStampedEntity;
import org.rudi.microservice.projekt.core.common.SchemaConstants;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Type d’accompagnement souhaité
 */
@Entity
@Table(name = "support", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
@ToString
public class SupportEntity extends AbstractStampedEntity {

	private static final long serialVersionUID = -8837224501760331803L;

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof SupportEntity)) {
			return false;
		}
		return super.equals(obj);
	}

}
