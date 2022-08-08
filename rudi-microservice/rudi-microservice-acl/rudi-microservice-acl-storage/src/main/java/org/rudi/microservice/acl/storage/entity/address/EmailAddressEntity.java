package org.rudi.microservice.acl.storage.entity.address;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.rudi.microservice.acl.core.common.SchemaConstants;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * EmailAddress Entity
 */

@Entity
@Table(name = "email_address", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
@ToString
public class EmailAddressEntity extends AbstractAddressEntity {

	private static final long serialVersionUID = 9099267916742316242L;

	@Column(name = "email", length = 150, nullable = false)
	private String email;

	public EmailAddressEntity() {
		super(AddressType.EMAIL);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((getEmail() == null) ? 0 : getEmail().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof EmailAddressEntity)) {
			return false;
		}
		EmailAddressEntity other = (EmailAddressEntity) obj;
		if (getEmail() == null) {
			if (other.getEmail() != null) {
				return false;
			}
		} else if (!getEmail().equals(other.getEmail())) {
			return false;
		}
		return true;
	}

}
