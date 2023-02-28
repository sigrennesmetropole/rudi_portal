package org.rudi.facet.doks.exceptions;

public class EmptyDocumentException extends RuntimeException {
	private static final long serialVersionUID = -6881813035198172155L;

	public EmptyDocumentException(String fileName) {
		super(String.format("Document \"%s\" is empty", fileName));
	}
}
