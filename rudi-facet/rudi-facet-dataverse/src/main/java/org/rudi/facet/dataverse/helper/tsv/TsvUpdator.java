package org.rudi.facet.dataverse.helper.tsv;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TsvUpdator {
	private final TsvGenerator tsvGenerator;
	private final TsvReader tsvReader = new TsvReader();

	public void update(InputStream currentTsvInputStream, OutputStream mergedTsvOutputStream) throws IOException {
		final var currentTsv = tsvReader.read(currentTsvInputStream);

		final var generatedTsv = tsvGenerator.generate();

		final var tsvMerger = new TsvMerger();
		final var mergedTsv = tsvMerger.merge(currentTsv, generatedTsv);

		try (final var tsvWriter = new TsvWriter(mergedTsvOutputStream)) {
			tsvWriter.write(mergedTsv);
		}
	}

}
