package org.rudi.facet.apimaccess.exception;

public class APIManagerException extends Exception {

    public APIManagerException() {
        super();
    }

    public APIManagerException(final String message) {
        super(message);
    }

    public APIManagerException(final String message, final Throwable exception) {
        super(message, exception);
    }

    public APIManagerException(Throwable e) {
        super(e);
    }
}
