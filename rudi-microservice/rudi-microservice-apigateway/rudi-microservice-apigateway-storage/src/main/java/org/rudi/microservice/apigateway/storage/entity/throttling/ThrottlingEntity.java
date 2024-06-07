package org.rudi.microservice.apigateway.storage.entity.throttling;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.rudi.common.storage.entity.AbstractStampedEntity;
import org.rudi.microservice.apigateway.core.common.SchemaConstants;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Type de
 */
@Entity
@Table(name = "throttling", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
@ToString
public class ThrottlingEntity extends AbstractStampedEntity {

	private static final long serialVersionUID = -8837224501760331803L;

	private int rate;

	private int burstCapacity;

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ThrottlingEntity)) {
			return false;
		}
		return super.equals(obj);
	}

}
