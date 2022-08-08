package org.rudi.microservice.konsult.facade.exception;

import org.rudi.facet.apimaccess.exception.APINotFoundException;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.microservice.konsult.core.bean.ApiError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class KonsultExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(KonsultExceptionHandler.class);

    @ExceptionHandler(DataverseAPIException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected @ResponseBody ApiError handleDataverseService(final DataverseAPIException ex, final WebRequest request) {

        LOGGER.error(ex.getMessage(), ex);

        ApiError apiError = new ApiError();
        if (ex.getApiResponseInfo() != null) {
            apiError.setCode(ex.getApiResponseInfo().getStatus());
        }
        else {
            apiError.setCode(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
        apiError.setLabel(ex.getMessage());

        return apiError;
    }

    @ExceptionHandler(APINotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected @ResponseBody ApiError handleNotFoundException(final APINotFoundException e) {
        return new ApiError()
                .code(Integer.toString(HttpStatus.NOT_FOUND.value()))
                .label(e.getMessage());
    }

}
