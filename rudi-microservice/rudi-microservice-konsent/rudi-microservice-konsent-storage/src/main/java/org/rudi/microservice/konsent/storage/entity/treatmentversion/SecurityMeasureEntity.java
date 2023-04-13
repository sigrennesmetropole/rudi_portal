package org.rudi.microservice.konsent.storage.entity.treatmentversion;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;

import org.rudi.microservice.konsent.core.common.SchemaConstants;
import org.rudi.microservice.konsent.storage.entity.common.AbstractMultilangualStampedEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "security_measure", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
@AssociationOverrides(
		{
				@AssociationOverride(name = "labels",
						joinTable = @JoinTable(name = "security_measure_dictionary_entry",
								joinColumns = @JoinColumn(name = "security_measure_fk"), inverseJoinColumns = @JoinColumn(name = "dictionary_entry_fk")
						)
				)
		}
)
public class SecurityMeasureEntity extends AbstractMultilangualStampedEntity {
	private static final long serialVersionUID = 4513827721998137462L;

}
