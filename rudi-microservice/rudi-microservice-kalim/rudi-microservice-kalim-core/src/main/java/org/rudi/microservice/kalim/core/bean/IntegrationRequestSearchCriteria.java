package org.rudi.microservice.kalim.core.bean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class IntegrationRequestSearchCriteria {

	private IntegrationStatus integrationStatus;

	private List<ProgressStatus> progressStatus;

	private LocalDateTime creationDateMin;

	private LocalDateTime creationDateMax;

	private LocalDateTime treatmentDateMin;

	private LocalDateTime treatmentDateMax;

	private LocalDateTime sendRequestDateMin;

	private LocalDateTime sendRequestDateMax;

	private UUID globalId;

}
