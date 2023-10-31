package org.rudi.facet.apimaccess.api.registration;

import lombok.Getter;
import org.rudi.facet.apimaccess.exception.APIManagerHttpException;
import org.springframework.http.HttpStatus;

import javax.annotation.Nullable;
import java.text.MessageFormat;
import java.text.ParseException;

/**
 * @see <a href="https://docs.wso2.com/display/IS510/apidocs/OAuth2-dynamic-client-registration/#!/models#Error">Documentation WSO2</a>
 */
@Getter
public class ApplicationAlreadyExistException extends APIManagerHttpException {

	private static final long serialVersionUID = 1L;
	
	private static final HttpStatus STATUS_CODE = HttpStatus.BAD_REQUEST;
	
	private static final MessageFormat ERROR_DESCRIPTION_FORMAT = new MessageFormat("Application with the name {0} already exist in the system");
	
	private final String clientName;

	private ApplicationAlreadyExistException(String originalErrorBody, String clientName) {
		super(STATUS_CODE, originalErrorBody);
		this.clientName = clientName;
	}

	/**
	 * @return l'exception correspondant à errorDescription, null si l'errorDescription n'est pas géré par cette exception
	 */
	@Nullable
	static ApplicationAlreadyExistException from(String originalErrorBody, OAuth2DynamicClientRegistrationError parsedErrorBody) {
		try {
			final var objects = ERROR_DESCRIPTION_FORMAT.parse(parsedErrorBody.errorDescription);
			final var clientName = objects[0].toString();
			return new ApplicationAlreadyExistException(originalErrorBody, clientName);
		} catch (ParseException pe) {
			return null;
		}
	}
}
