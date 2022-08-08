package org.rudi.microservice.projekt.service.support.impl.validator;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.projekt.core.bean.Support;

public interface SupportValidator {
	void validate(Support support) throws AppServiceException;
}
