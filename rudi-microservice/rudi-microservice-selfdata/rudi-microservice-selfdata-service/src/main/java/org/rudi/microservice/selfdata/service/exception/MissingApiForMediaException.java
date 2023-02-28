package org.rudi.microservice.selfdata.service.exception;

import java.util.UUID;

import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.springframework.dao.EmptyResultDataAccessException;

public class MissingApiForMediaException extends AppServiceNotFoundException {

	private final UUID datasetUuid;
	private final UUID mediaUuid;

	public MissingApiForMediaException(UUID datasetUuid, UUID mediaUuid) {
		super(new EmptyResultDataAccessException(1));
		this.datasetUuid = datasetUuid;
		this.mediaUuid = mediaUuid;
	}

	@Override
	public String getMessage() {
		return "Le JDD d'uuid : " + this.datasetUuid + " contenant le media d'uuid : " + this.mediaUuid + " ne contient pas d'API dans WSO2";
	}
}
