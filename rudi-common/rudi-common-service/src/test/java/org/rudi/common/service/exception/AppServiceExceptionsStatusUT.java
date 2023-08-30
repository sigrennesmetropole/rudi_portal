package org.rudi.common.service.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class AppServiceExceptionsStatusUT {
	@Test
	void from() {
		assertThat(AppServiceExceptionsStatus.stringValueFrom(HttpStatus.FORBIDDEN)).isEqualTo(AppServiceExceptionsStatus.FORBIDDEN.getStringValue());
	}
}
