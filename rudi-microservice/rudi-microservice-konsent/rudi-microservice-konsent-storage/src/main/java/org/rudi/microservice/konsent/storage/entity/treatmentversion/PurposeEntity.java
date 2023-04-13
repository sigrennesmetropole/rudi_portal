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
@Table(name = "purpose", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
@AssociationOverrides(
		{
				@AssociationOverride(name = "labels",
						joinTable = @JoinTable(name = "purpose_dictionary_entry",
								joinColumns = @JoinColumn(name = "purpose_fk"), inverseJoinColumns = @JoinColumn(name = "dictionary_entry_fk")
						)
				)
		}
)
public class PurposeEntity extends AbstractMultilangualStampedEntity {
	private static final long serialVersionUID = 4513519721998137595L;

}
