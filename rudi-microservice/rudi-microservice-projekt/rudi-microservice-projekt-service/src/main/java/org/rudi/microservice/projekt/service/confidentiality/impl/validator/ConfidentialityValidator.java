package org.rudi.microservice.projekt.service.confidentiality.impl.validator;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.projekt.core.bean.Confidentiality;

public interface ConfidentialityValidator {
	void validate(Confidentiality confidentiality) throws AppServiceException;
}
