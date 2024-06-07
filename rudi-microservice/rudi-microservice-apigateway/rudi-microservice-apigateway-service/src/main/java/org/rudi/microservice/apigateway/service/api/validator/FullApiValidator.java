package org.rudi.microservice.apigateway.service.api.validator;

import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.apigateway.core.bean.Api;
import org.springframework.stereotype.Component;

@Component
class FullApiValidator implements ApiValidator {

	@Override
	public void validateCreate(Api api) throws AppServiceException {
		validateCommon(api);
	}

	private void validateCommon(Api api) {
		if (api.getGlobalId() == null) {
			throw new IllegalArgumentException("Global id required");
		}
		if (api.getMediaId() == null) {
			throw new IllegalArgumentException("Media id required");
		}
		if (StringUtils.isEmpty(api.getContract())) {
			throw new IllegalArgumentException("Contrat required");
		}
		if (StringUtils.isEmpty(api.getUrl())) {
			throw new IllegalArgumentException("URL required");
		}
		try {
			new URL(api.getUrl());
		} catch (Exception e) {
			throw new IllegalArgumentException("URL invalid " + api.getUrl());
		}
	}

	@Override
	public void validateUpdate(Api api) throws AppServiceException {
		if (api.getUuid() == null) {
			throw new IllegalArgumentException("UUID required");
		}
		validateCommon(api);
	}
}
