package org.rudi.microservice.projekt.service.project;

import java.util.UUID;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.microservice.projekt.core.bean.NewDatasetRequestSearchCriteria;
import org.rudi.microservice.projekt.core.bean.PagedNewDatasetRequestList;
import org.rudi.microservice.projekt.core.bean.Project;
import org.springframework.data.domain.Pageable;


public interface NewDatasetRequestService {
	/**
	 * @param newDatasetRequestUuid
	 * @return nombre le projet rattaché à une nouvelle demande
	 */
	Project findProjectByNewDatasetRequest(UUID newDatasetRequestUuid) throws AppServiceException;

	PagedNewDatasetRequestList searchMyNewDatasetRequests(NewDatasetRequestSearchCriteria criteria, Pageable pageable) throws AppServiceNotFoundException, AppServiceException;
}
