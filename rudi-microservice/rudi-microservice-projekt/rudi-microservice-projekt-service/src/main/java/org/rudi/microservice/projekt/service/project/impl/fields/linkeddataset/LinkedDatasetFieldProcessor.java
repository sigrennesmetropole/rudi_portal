package org.rudi.microservice.projekt.service.project.impl.fields.linkeddataset;

import javax.annotation.Nullable;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetEntity;

/**
 * @author FNI18300
 */
interface LinkedDatasetFieldProcessor {
	void process(@Nullable LinkedDatasetEntity linkedDataset, @Nullable LinkedDatasetEntity existingLinkedDataset)
			throws AppServiceException, APIManagerException;
}
