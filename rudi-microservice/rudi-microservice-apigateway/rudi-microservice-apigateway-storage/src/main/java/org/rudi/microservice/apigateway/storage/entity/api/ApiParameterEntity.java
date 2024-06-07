package org.rudi.microservice.apigateway.storage.entity.api;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.rudi.common.storage.entity.AbstractLongIdEntity;
import org.rudi.microservice.apigateway.core.common.SchemaConstants;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Caractère confidentiel du projet
 */
@Entity
@Table(name = "apiparameter", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
@ToString
public class ApiParameterEntity extends AbstractLongIdEntity {

	private static final long serialVersionUID = 4468296664973218049L;

	@Column(name = "name", length = 100, nullable = false)
	private String name;

	@Column(name = "value_", length = 2048)
	private String value;

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ApiParameterEntity)) {
			return false;
		}
		return super.equals(obj);
	}

}
