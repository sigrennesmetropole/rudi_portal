package org.rudi.microservice.strukture.storage.entity.address;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.rudi.microservice.strukture.core.common.SchemaConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * PostalAddress Entity
 */

@Entity
@Table(name = "postal_address", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
@ToString
public class PostalAddressEntity extends AbstractAddressEntity {

	private static final long serialVersionUID = 5928492308899757432L;

	@Column(name = "recipientIdentification")
	private String recipientIdentification;

	@Column(name = "additionalIdentification")
	private String additionalIdentification;

	@Column(name = "streetNumber")
	private String streetNumber;

	@Column(name = "distributionService")
	private String distributionService;

	@Column(name = "locality")
	private String locality;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((getAdditionalIdentification() == null) ? 0 : getAdditionalIdentification().hashCode());
		result = prime * result + ((getDistributionService() == null) ? 0 : getDistributionService().hashCode());
		result = prime * result + ((getLocality() == null) ? 0 : getLocality().hashCode());
		result = prime * result
				+ ((getRecipientIdentification() == null) ? 0 : getRecipientIdentification().hashCode());
		result = prime * result + ((getStreetNumber() == null) ? 0 : getStreetNumber().hashCode());
		return result;
	}

	@Override
	@SuppressWarnings("java:S3776") // méthode générée
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof PostalAddressEntity)) {
			return false;
		}
		PostalAddressEntity other = (PostalAddressEntity) obj;
		if (getAdditionalIdentification() == null) {
			if (other.getAdditionalIdentification() != null) {
				return false;
			}
		} else if (!getAdditionalIdentification().equals(other.getAdditionalIdentification())) {
			return false;
		}
		if (getDistributionService() == null) {
			if (other.getDistributionService() != null) {
				return false;
			}
		} else if (!getDistributionService().equals(other.getDistributionService())) {
			return false;
		}
		if (getLocality() == null) {
			if (other.getLocality() != null) {
				return false;
			}
		} else if (!getLocality().equals(other.getLocality())) {
			return false;
		}
		if (getRecipientIdentification() == null) {
			if (other.getRecipientIdentification() != null) {
				return false;
			}
		} else if (!getRecipientIdentification().equals(other.getRecipientIdentification())) {
			return false;
		}
		if (getStreetNumber() == null) {
			if (other.getStreetNumber() != null) {
				return false;
			}
		} else if (!getStreetNumber().equals(other.getStreetNumber())) {
			return false;
		}
		return true;
	}

}
