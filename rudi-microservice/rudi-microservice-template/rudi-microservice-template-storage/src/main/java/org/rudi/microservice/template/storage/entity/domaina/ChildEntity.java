package org.rudi.microservice.template.storage.entity.domaina;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.rudi.common.storage.entity.AbstractStampedEntity;
import org.rudi.microservice.template.core.common.SchemaConstants;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Caractère confidentiel du projet
 */
@Entity
@Table(name = "child_entity", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
@ToString
public class ChildEntity extends AbstractStampedEntity {

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ChildEntity)) {
			return false;
		}
		return super.equals(obj);
	}

}
