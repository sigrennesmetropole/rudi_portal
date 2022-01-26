package org.rudi.microservice.acl.storage.entity.role;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.rudi.common.storage.entity.AbstractStampedEntity;
import org.rudi.microservice.acl.core.common.SchemaConstants;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "role", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
@ToString
public class RoleEntity extends AbstractStampedEntity {

	private static final long serialVersionUID = -5673157586856745693L;

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
}
