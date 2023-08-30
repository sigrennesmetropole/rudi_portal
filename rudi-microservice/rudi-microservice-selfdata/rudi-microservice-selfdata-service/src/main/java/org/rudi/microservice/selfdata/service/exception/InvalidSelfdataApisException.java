package org.rudi.microservice.selfdata.service.exception;

import java.util.UUID;

import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.springframework.dao.EmptyResultDataAccessException;

public class InvalidSelfdataApisException extends AppServiceNotFoundException {

	private static final long serialVersionUID = 1748973246752933018L;
	private final UUID datasetUuid;

	public InvalidSelfdataApisException(UUID datasetUuid) {
		super(new EmptyResultDataAccessException(1));
		this.datasetUuid = datasetUuid;
	}

	@Override
	public String getMessage() {
		return "Le JDD d'uuid : " + this.datasetUuid
				+ " ne possède pas les APIs selfdata nécessaires pour une consultation (Une API TPBC et une API GDATA).";
	}
}
