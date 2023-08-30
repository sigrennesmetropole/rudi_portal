package org.rudi.microservice.konsent.service.consent.replacer;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.konsent.core.bean.TreatmentVersion;
import org.springframework.transaction.annotation.Transactional;

public interface TransientDtoReplacerTest {
	@Transactional
	void replaceDtoFor(TreatmentVersion treatmentVersion) throws AppServiceException;
}
