package org.rudi.facet.dataverse.helper.tsv;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nonnull;

import org.apache.commons.io.IOUtils;
import org.rudi.facet.dataverse.bean.FieldType;

class TsvReader {
	public static final Charset CHARSET = TsvWriter.CHARSET;
	private int currentLineNumber = 0;

	Tsv read(InputStream inputStream) throws IOException {
		currentLineNumber = 0;
		final var lines = IOUtils.readLines(inputStream, CHARSET);
		return Tsv.builder()
				.metadataBlock(readMetadataBlock(lines))
				.datasetField(readDatasetField(lines))
				.controlledVocabulary(readControlledVocabulary(lines))
				.build();
	}

	private TsvPart<TsvMetadataBlockLine> readMetadataBlock(List<String> lines) {
		readTsvPartHeaderLine(lines);
		return new TsvPart<>(
				readTsvPartLines(lines, columns -> TsvMetadataBlockLine.builder()
						.name(columns[1])
						.displayName(columns[3])
						.build())
		);
	}

	@Nonnull
	private <L> List<L> readTsvPartLines(List<String> lines, Function<String[], L> lineBuilder) {
		final List<L> metadataBlockLines = new ArrayList<>();

		String currentMetadataBlockLine;
		while (currentLineNumber < lines.size() && !(currentMetadataBlockLine = lines.get(currentLineNumber)).startsWith("#")) {
			final var columns = currentMetadataBlockLine.split(TsvLine.COLUMN_SEPARATOR);
			metadataBlockLines.add(lineBuilder.apply(columns));
			++currentLineNumber;
		}
		return metadataBlockLines;
	}

	private void readTsvPartHeaderLine(List<String> lines) {
		final var partHeaderLine = lines.get(currentLineNumber);
		if (!partHeaderLine.startsWith("#")) {
			throw new TsvPartHeaderLineExpectedException(currentLineNumber);
		}
		++currentLineNumber;
	}

	private TsvPart<TsvDatasetFieldLine> readDatasetField(List<String> lines) {
		readTsvPartHeaderLine(lines);
		return new TsvPart<>(
				readTsvPartLines(lines, columns -> TsvDatasetFieldLine.builder()
						.name(columns[1])
						.title(columns[2])
						.description(columns[3])
						.watermark(columns[4])
						.fieldType(FieldType.fromValue(columns[5]))
						.displayOrder(Integer.parseInt(columns[6]))
						.displayFormat(columns[7])
						.advancedSearchField(Boolean.valueOf(columns[8]))
						.allowControlledVocabulary(Boolean.valueOf(columns[9]))
						.allowmultiples(Boolean.valueOf(columns[10]))
						.facetable(Boolean.valueOf(columns[11]))
						.displayoncreate(Boolean.valueOf(columns[12]))
						.required(Boolean.valueOf(columns[13]))
						.parent(columns[14])
						.metadatablockId(columns[15]) // C'est la dernière colonne qui n'est jamais vide, donc toujours présente avec un String.split
						.termURI(columns.length > 16 ? columns[16] : null)
						.build())
		);
	}

	private TsvPart<TsvControlledVocabularyLine> readControlledVocabulary(List<String> lines) {
		readTsvPartHeaderLine(lines);
		return new TsvPart<>(
				readTsvPartLines(lines, columns -> TsvControlledVocabularyLine.builder()
						.datasetField(columns[1])
						.value(columns[2])
						.identifier(columns[3])
						.displayOrder(Integer.parseInt(columns[4]))
						.build())
		);
	}
}
