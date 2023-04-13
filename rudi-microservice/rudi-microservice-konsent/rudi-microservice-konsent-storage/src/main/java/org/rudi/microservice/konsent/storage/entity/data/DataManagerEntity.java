package org.rudi.microservice.konsent.storage.entity.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.rudi.common.storage.entity.AbstractLongIdEntity;
import org.rudi.microservice.konsent.core.common.SchemaConstants;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "data_manager", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
public class DataManagerEntity extends AbstractLongIdEntity {
	private static final long serialVersionUID = 4546827721998162762L;

	@Column(name = "name", length = 150, nullable = false)
	private String name;

	@Column(name = "email", length = 150, nullable = false)
	private String email;

	@Column(name = "phone_number", length = 150)
	private String phoneNumber;

	@Column(name = "address_1", length = 150)
	private String address1;

	@Column(name = "address_2", length = 150)
	private String address2;

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof DataManagerEntity)) {
			return false;
		}
		return super.equals(obj);
	}
}
