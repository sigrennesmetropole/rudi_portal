package org.rudi.common.facade.exception;

import lombok.Data;

@Data
class ApiError {
	private final String code;
	private final String label;
}
