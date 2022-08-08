package org.rudi.microservice.kalim.core.bean;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class IntegrationRequestSearchCriteria {

	private IntegrationStatus integrationStatus;

	private List<ProgressStatus> progressStatus;

	private OffsetDateTime creationDateMin;

	private OffsetDateTime creationDateMax;

	private OffsetDateTime treatmentDateMin;

	private OffsetDateTime treatmentDateMax;

	private OffsetDateTime sendRequestDateMin;

	private OffsetDateTime sendRequestDateMax;

	private UUID globalId;

}
