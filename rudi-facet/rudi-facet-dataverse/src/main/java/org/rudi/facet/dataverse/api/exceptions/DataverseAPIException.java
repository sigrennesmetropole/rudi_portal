package org.rudi.facet.dataverse.api.exceptions;

import lombok.Getter;
import org.rudi.facet.dataverse.model.ApiResponseInfo;

public class DataverseAPIException extends Exception {

    private static final long serialVersionUID = 1L;

    @Getter
    private final ApiResponseInfo apiResponseInfo;

    public DataverseAPIException() {
        super();
        this.apiResponseInfo = null;
    }

    public DataverseAPIException(final String message) {
        super(message);
        this.apiResponseInfo = null;
    }

    public DataverseAPIException(final String message, final Throwable exception) {
        super(message, exception);
        this.apiResponseInfo = null;
    }

    public DataverseAPIException(Throwable e) {
        super(e);
        this.apiResponseInfo = null;
    }

    public DataverseAPIException(String message, ApiResponseInfo apiResponseInfo) {
        super(message);
        this.apiResponseInfo = apiResponseInfo;
    }
}
