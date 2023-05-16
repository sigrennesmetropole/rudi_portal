package org.rudi.microservice.selfdata.storage.entity.selfdatainformationrequest;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.rudi.facet.bpmn.entity.workflow.AbstractAssetDescriptionEntity;
import org.rudi.microservice.selfdata.core.common.SchemaConstants;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Selfdata entity
 */
@Entity
@Table(name = "selfdata_information_request", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
@ToString
public class SelfdataInformationRequestEntity extends AbstractAssetDescriptionEntity {

	private static final long serialVersionUID = -6508639499690690560L;
	public static final String SELFDATA_INFORMATION_REQUEST_STATUS = "selfdataInformationRequestStatus";

	@Column(name = "dataset_uuid", nullable = false, unique = true)
	private UUID datasetUuid;

	@Column(name = "selfdata_information_request_status", length = 20, nullable = false)
	@Enumerated(EnumType.STRING)
	private SelfdataInformationRequestStatus selfdataInformationRequestStatus = SelfdataInformationRequestStatus.DRAFT;

	@Column(name = "user_present", nullable = true)
	private Boolean userPresent;

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof SelfdataInformationRequestEntity)) {
			return false;
		}
		return super.equals(obj);
	}

}
