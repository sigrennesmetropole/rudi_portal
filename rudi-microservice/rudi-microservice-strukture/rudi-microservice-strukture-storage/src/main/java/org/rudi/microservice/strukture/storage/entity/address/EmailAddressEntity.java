package org.rudi.microservice.strukture.storage.entity.address;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.rudi.microservice.strukture.core.common.SchemaConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

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
