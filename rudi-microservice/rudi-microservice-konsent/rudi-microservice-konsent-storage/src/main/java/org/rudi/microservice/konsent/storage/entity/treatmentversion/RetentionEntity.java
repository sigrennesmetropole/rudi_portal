package org.rudi.microservice.konsent.storage.entity.treatmentversion;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;

import org.rudi.microservice.konsent.core.common.SchemaConstants;
import org.rudi.microservice.konsent.storage.entity.common.AbstractMultilangualStampedEntity;
import org.rudi.microservice.konsent.storage.entity.common.RetentionUnit;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "retention", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
@AssociationOverrides(
		{
				@AssociationOverride(name = "labels",
						joinTable = @JoinTable(name = "retention_dictionary_entry",
								joinColumns = @JoinColumn(name = "retention_fk"), inverseJoinColumns = @JoinColumn(name = "dictionary_entry_fk")
						)
				)
		}
)
public class RetentionEntity extends AbstractMultilangualStampedEntity {
	private static final long serialVersionUID = 4513827721998162762L;

	@Column(name = "value", nullable = false)
	private Integer value;

	@Column(name = "unit", length = 20, nullable = false)
	@Enumerated(EnumType.STRING)
	private RetentionUnit unit;

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof RetentionEntity)) {
			return false;
		}
		return super.equals(obj);
	}
}
