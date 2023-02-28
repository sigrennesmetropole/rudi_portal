package org.rudi.facet.dataverse.helper.tsv;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TsvWriter implements AutoCloseable {
	public static final Charset CHARSET = StandardCharsets.UTF_8;
	private final OutputStream outputStream;

	public void write(Tsv tsv) {
		try (final var printWriter = new TsvPrintWriter(outputStream)) {

			printWriter.println("#metadataBlock\tname\tdataverseAlias\tdisplayName\t\t\t\t\t\t\t\t\t\t\t\t\t");
			tsv.metadataBlock.lines.forEach(printWriter::println);

			printWriter.println("#datasetField\tname\ttitle\tdescription\twatermark\t fieldType\tdisplayOrder\tdisplayFormat\tadvancedSearchField\tallowControlledVocabulary\tallowmultiples\tfacetable\tdisplayoncreate\trequired\tparent\tmetadatablock_id\ttermURI");
			tsv.datasetField.lines.forEach(printWriter::println);

			printWriter.println("#controlledVocabulary\tDatasetField\tValue\tidentifier\tdisplayOrder");
			tsv.controlledVocabulary.lines.forEach(printWriter::println);
		}
	}

	@Override
	public void close() throws IOException {
		outputStream.close();
	}
}
