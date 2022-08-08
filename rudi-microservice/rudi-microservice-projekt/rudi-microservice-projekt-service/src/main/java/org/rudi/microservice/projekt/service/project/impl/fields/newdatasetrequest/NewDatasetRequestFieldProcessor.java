package org.rudi.microservice.projekt.service.project.impl.fields.newdatasetrequest;

import javax.annotation.Nullable;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.projekt.storage.entity.newdatasetrequest.NewDatasetRequestEntity;

/**
 * 
 * @author FNI18300
 *
 */
interface NewDatasetRequestFieldProcessor {
	void process(@Nullable NewDatasetRequestEntity NewDatasetRequest,
			@Nullable NewDatasetRequestEntity existingNewDatasetRequest) throws AppServiceException;
}
