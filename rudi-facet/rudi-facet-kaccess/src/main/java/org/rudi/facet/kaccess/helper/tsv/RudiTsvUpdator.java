package org.rudi.facet.kaccess.helper.tsv;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.rudi.facet.dataverse.helper.tsv.TsvUpdator;

import lombok.extern.slf4j.Slf4j;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Slf4j
public class RudiTsvUpdator extends TsvUpdator {

	private RudiTsvUpdator() {
		super(new RudiTsvGenerator());
	}

	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			throw new RuntimeException("Please specify rudi-facet-kaccess base directory (\"basedir\" in maven).");
		}
		final var basedir = args[0];

		final var tsvUpdator = new RudiTsvUpdator();

		final var existingTsvPath = Paths.get(basedir, "src/main/resources/metadata/rudi.tsv");
		final var mergedTsvPath = File.createTempFile(existingTsvPath.getFileName().toString(), ".tmp").toPath();

		try (
				final var existingTsvInputStream = Files.newInputStream(existingTsvPath);
				final var mergedTsvOutputStream = Files.newOutputStream(mergedTsvPath);
		) {
			tsvUpdator.update(existingTsvInputStream, mergedTsvOutputStream);
		}

		Files.move(mergedTsvPath, existingTsvPath, REPLACE_EXISTING);
	}

}
