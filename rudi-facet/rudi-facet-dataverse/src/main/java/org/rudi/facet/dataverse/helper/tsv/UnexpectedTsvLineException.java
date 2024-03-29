package org.rudi.facet.dataverse.helper.tsv;

class UnexpectedTsvLineException extends RuntimeException {

	private static final long serialVersionUID = -1121851624554738953L;

	public UnexpectedTsvLineException(String message, int currentLineNumber) {
		super(String.format("Unexpected line at line number %s : %s", currentLineNumber, message));
	}
}
