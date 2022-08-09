package org.rudi.microservice.projekt.service.confidentiality.impl;

import org.rudi.microservice.projekt.storage.dao.confidentiality.ConfidentialityDao;
import org.rudi.microservice.projekt.storage.entity.ConfidentialityEntity;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ConfidentialityHelper {

	private static final String CONFIDENTIAL_CONFIDENTIALITY_CODE = "CONFIDENTIAL";
	private static final String OPEN_CONFIDENTIALITY_CODE = "OPEN";
	private static final String DEFAULT_CONFIDENTIALITY_CODE = OPEN_CONFIDENTIALITY_CODE;

	private final ConfidentialityDao confidentialityDao;

	public ConfidentialityEntity getDefaultConfidentiality() {
		return confidentialityDao.findByCode(DEFAULT_CONFIDENTIALITY_CODE);
	}

}
