package org.rudi.facet.dataverse.helper.tsv;

class TsvPartHeaderLineExpectedException extends UnexpectedTsvLineException {
	public TsvPartHeaderLineExpectedException(int currentLineNumber) {
		super("Expected part header line", currentLineNumber);
	}
}
