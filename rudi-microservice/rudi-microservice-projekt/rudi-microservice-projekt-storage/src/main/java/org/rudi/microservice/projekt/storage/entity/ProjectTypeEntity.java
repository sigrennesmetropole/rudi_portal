package org.rudi.microservice.projekt.storage.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.rudi.common.storage.entity.AbstractStampedEntity;
import org.rudi.microservice.projekt.core.common.SchemaConstants;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "project_type", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
@ToString
public class ProjectTypeEntity extends AbstractStampedEntity {

	private static final long serialVersionUID = 6461158324237968947L;

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
