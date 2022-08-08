/**
 * 
 */
package org.rudi.facet.generator.impl;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.rudi.facet.generator.Generator;
import org.rudi.facet.generator.exception.GenerationException;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Classes abstraite pour la génération des documents
 * 
 * @author FNI18300
 *
 */
@Slf4j
public abstract class AbstractGenerator<T> implements Generator<T> {

	private static final String TEMP_FILE_EXTENSION = "gen";

	private static final String TEMP_FILE_PREFIX = "rudi-generator-";

	@Getter
	@Setter
	private String temporaryDirectory = null;

	protected File createOutputFile() throws IOException {
		File generateFile = null;
		if (StringUtils.isNotEmpty(temporaryDirectory)) {
			generateFile = File.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_EXTENSION, new File(temporaryDirectory));
		} else {
			generateFile = File.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_EXTENSION);
		}
		if (log.isDebugEnabled()) {
			log.debug("Temporary generation file:{}", generateFile);
		}
		return generateFile;
	}

	/**
	 * Créé les répertoires demandés
	 * 
	 * @param directory
	 * @throws GenerationException
	 */
	protected void ensureDirectoryFile(String directory) throws GenerationException {
		File targetDirectoryFile = new File(directory);
		if (!targetDirectoryFile.exists()) {
			if (log.isDebugEnabled()) {
				log.debug("Create directory:{}", directory);
			}
			if (!targetDirectoryFile.mkdirs()) {
				throw new GenerationException("Failed to create directory:" + directory);
			}
		}
	}
}
