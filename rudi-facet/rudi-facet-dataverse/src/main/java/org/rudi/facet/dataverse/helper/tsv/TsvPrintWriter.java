package org.rudi.facet.dataverse.helper.tsv;

import java.io.OutputStream;
import java.io.PrintWriter;

class TsvPrintWriter extends PrintWriter {

	private static final char UNIX_LINE_SERATOR = '\n';

	TsvPrintWriter(OutputStream out) {
		super(out, false, TsvWriter.CHARSET);
	}

	@Override
	public void println() {
		super.write(UNIX_LINE_SERATOR);
	}
}
