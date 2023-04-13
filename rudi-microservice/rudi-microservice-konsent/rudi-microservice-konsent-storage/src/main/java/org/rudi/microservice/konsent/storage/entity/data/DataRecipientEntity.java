package org.rudi.microservice.konsent.storage.entity.data;

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
@Table(name = "data_recipient", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
@AssociationOverrides(
		{
				@AssociationOverride(name = "labels",
						joinTable = @JoinTable(name = "data_recipient_dictionary_entry",
								joinColumns = @JoinColumn(name = "data_recipient_fk"), inverseJoinColumns = @JoinColumn(name = "dictionary_entry_fk")
						)
				)
		}
)
public class DataRecipientEntity extends AbstractMultilangualStampedEntity {
	private static final long serialVersionUID = 4513827721954637462L;

}
