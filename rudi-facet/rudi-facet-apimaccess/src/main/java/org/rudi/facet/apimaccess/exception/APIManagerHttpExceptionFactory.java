package org.rudi.facet.apimaccess.exception;

import javax.annotation.Nullable;

import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Primary
public class APIManagerHttpExceptionFactory {
	public APIManagerHttpException createFrom(HttpStatus status, HttpHeaders headers, @Nullable String errorBody) {
		if (status == HttpStatus.UNAUTHORIZED && errorBody == null) {
			return new UnauthorizedException();
		}
		return new APIManagerHttpException(status, errorBody);
	}

}
