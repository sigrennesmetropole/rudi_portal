package org.rudi.facet.dataverse.helper.tsv;

class TsvPartHeaderLineExpectedException extends UnexpectedTsvLineException {

	private static final long serialVersionUID = -2573334386560574416L;

	public TsvPartHeaderLineExpectedException(int currentLineNumber) {
		super("Expected part header line", currentLineNumber);
	}
}
