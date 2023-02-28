package org.rudi.facet.dataverse.helper.tsv;

class UnexpectedTsvLineException extends RuntimeException {
	public UnexpectedTsvLineException(String message, int currentLineNumber) {
		super(String.format("Unexpected line at line number %s : %s", currentLineNumber, message));
	}
}
