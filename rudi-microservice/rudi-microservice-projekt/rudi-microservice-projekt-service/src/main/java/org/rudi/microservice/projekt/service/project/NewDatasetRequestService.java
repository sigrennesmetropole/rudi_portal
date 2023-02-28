package org.rudi.microservice.projekt.service.project;

import java.util.UUID;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.projekt.core.bean.Project;

public interface NewDatasetRequestService {
	/**
	 *
	 * @param newDatasetRequestUuid
	 * @return nombre le projet rattaché à une nouvelle demande
	 */
	Project findProjectByNewDatasetRequest(UUID newDatasetRequestUuid) throws AppServiceException;
}
