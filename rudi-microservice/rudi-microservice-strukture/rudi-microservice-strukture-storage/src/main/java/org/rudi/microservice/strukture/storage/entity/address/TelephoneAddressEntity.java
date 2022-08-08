package org.rudi.microservice.strukture.storage.entity.address;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.rudi.microservice.strukture.core.common.SchemaConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * TelephoneAddress Entity
 */

@Entity
@Table(name = "telephone_address", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
@ToString
public class TelephoneAddressEntity extends AbstractAddressEntity {

	private static final long serialVersionUID = 6990556660409228821L;

	@Column(name = "phone_number", length = 20, nullable = false)
	private String phoneNumber;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((getPhoneNumber() == null) ? 0 : getPhoneNumber().hashCode());
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
		if (!(obj instanceof TelephoneAddressEntity)) {
			return false;
		}
		TelephoneAddressEntity other = (TelephoneAddressEntity) obj;
		if (getPhoneNumber() == null) {
			if (other.getPhoneNumber() != null) {
				return false;
			}
		} else if (!getPhoneNumber().equals(other.getPhoneNumber())) {
			return false;
		}
		return true;
	}

}
