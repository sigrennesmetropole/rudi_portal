package org.rudi.facet.apimaccess.exception;

import javax.annotation.Nullable;

import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Primary
public class APIManagerHttpExceptionFactory {
	/**
	 *
	 * @param status statu HTTP (code de l'erreur, etc...)
	 * @param headers headers de la requête
	 * @param errorBody body contenant une erreur
	 * @return APIManagerHttpException fonction de l'erreur présente dans le body
	 */
	public APIManagerHttpException createFrom(HttpStatus status, HttpHeaders headers, @Nullable String errorBody) {
		if (status == HttpStatus.UNAUTHORIZED && errorBody == null) {
			return new UnauthorizedException();
		}
		return new APIManagerHttpException(status, errorBody);
	}

}
