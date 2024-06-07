package org.rudi.microservice.selfdata.storage.entity.selfdatadataset;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import net.jcip.annotations.Immutable;
import org.rudi.microservice.selfdata.core.common.SchemaConstants;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "selfdata_dataset", schema = SchemaConstants.DATA_SCHEMA)
@Immutable
@Getter
@Setter
@ToString
public class SelfdataDatasetEntity {

	public static final String TITLE_FIELD = "title";
	public static final String INITIATOR_FIELD = "initiator";

	@Id
	@Column(name = "id")
	private Long id;

	@Column(name = "description")
	private String title;

	@Column(name = "process_definition_key")
	private String processDefinitionKey;

	@Column(name = "updated_date")
	private OffsetDateTime updatedDate;

	@Column(name = "dataset_uuid")
	private UUID datasetUuid;

	@Column(name = "initiator")
	private String initiator;

	@Column(name = "functional_status")
	private String functionalStatus;

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof SelfdataDatasetEntity)) {
			return false;
		}
		return super.equals(obj);
	}
}
