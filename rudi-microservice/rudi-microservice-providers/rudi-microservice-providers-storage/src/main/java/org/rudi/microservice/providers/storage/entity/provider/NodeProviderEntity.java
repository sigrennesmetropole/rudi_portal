package org.rudi.microservice.providers.storage.entity.provider;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.rudi.common.core.Stamped;
import org.rudi.common.storage.entity.AbstractLongIdEntity;
import org.rudi.microservice.providers.core.common.SchemaConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * NodeProvider Entity
 */
@Entity
@Table(name = "node_provider", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
@ToString
public class NodeProviderEntity extends AbstractLongIdEntity implements Stamped {

	private static final long serialVersionUID = 484255757870528148L;

	@Column(name = "opening_date", nullable = false)
	private LocalDateTime openingDate;

	@Column(name = "closing_date")
	private LocalDateTime closingDate;

	@Column(name = "version")
	private String version;

	@Column(name = "url")
	private String url;

	@Column(name = "harvestable", nullable = false)
	private boolean harvestable;

	@Column(name = "notifiable", nullable = false)
	private boolean notifiable;

	@Column(name = "harvesting_cron")
	private String harvestingCron;

	@Column(name = "last_harvesting_date")
	private LocalDateTime lastHarvestingDate;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((getVersion() == null) ? 0 : getVersion().hashCode());
		return result;
	}

	@SuppressWarnings("RedundantIfStatement") // generated code
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof NodeProviderEntity)) {
			return false;
		}
		NodeProviderEntity other = (NodeProviderEntity) obj;
		if (getVersion() == null) {
			if (other.getVersion() != null) {
				return false;
			}
		} else if (!getVersion().equals(other.getVersion())) {
			return false;
		}
		return true;
	}

}
