package org.rudi.microservice.konsent.service.treatment;

import java.util.List;
import java.util.UUID;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.konsent.core.bean.PagedTreatmentList;
import org.rudi.microservice.konsent.core.bean.PagedTreatmentVersionList;
import org.rudi.microservice.konsent.core.bean.Treatment;
import org.rudi.microservice.konsent.core.bean.TreatmentSearchCriteria;
import org.rudi.microservice.konsent.core.bean.TreatmentVersionSearchCriteria;
import org.springframework.data.domain.Pageable;

public interface TreatmentsService {
	Treatment createTreatment(Treatment treatment) throws AppServiceException;

	void deleteTreatment(UUID uuid) throws AppServiceException;

	void deleteTreatmentVersion(UUID treatmentUuid, UUID versionUuid) throws AppServiceException;

	Treatment getTreatment(UUID uuid, Boolean validated) throws AppServiceException;

	PagedTreatmentVersionList searchTreatmentVersions(TreatmentVersionSearchCriteria searchCriteria, Pageable pageable)
			throws AppServiceException;

	Treatment publishTreatment(UUID uuid) throws AppServiceException;

	PagedTreatmentList searchTreatments(TreatmentSearchCriteria searchCriteria, Pageable pageable)
			throws AppServiceException;

	Treatment updateTreatment(Treatment treatment) throws AppServiceException;

	void checkTreatmentVersionValididies(List<UUID> uuids) throws AppServiceException;
}
