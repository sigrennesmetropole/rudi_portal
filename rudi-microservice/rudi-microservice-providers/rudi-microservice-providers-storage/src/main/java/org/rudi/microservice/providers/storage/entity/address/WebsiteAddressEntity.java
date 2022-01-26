package org.rudi.microservice.providers.storage.entity.address;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.rudi.microservice.providers.core.common.SchemaConstants;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * WebsiteAddress Entity
 */

@Entity
@Table(name = "web_site_address", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
@ToString
public class WebsiteAddressEntity extends AbstractAddressEntity {

	private static final long serialVersionUID = 6532289280799661093L;

	@Column(name = "url", length = 1024, nullable = false)
	private String url;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((getUrl() == null) ? 0 : getUrl().hashCode());
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
		if (!(obj instanceof WebsiteAddressEntity)) {
			return false;
		}
		WebsiteAddressEntity other = (WebsiteAddressEntity) obj;
		if (getUrl() == null) {
			if (other.getUrl() != null) {
				return false;
			}
		} else if (!getUrl().equals(other.getUrl())) {
			return false;
		}
		return true;
	}

}
