/**
 * RUDI Portail
 */
package org.rudi.facet.generator;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.rudi.facet.generator.exception.GenerationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author FNI18300
 *
 */
@Component
@Slf4j
public class TemporaryHelper {

	private static final String TEMP_FILE_EXTENSION = ".gen";

	private static final String TEMP_FILE_PREFIX = "rudi-generator-";

	@Getter
	@Value("${temporary.directory:${java.io.tmpdir}}")
	private String temporaryDirectory = null;

	public File createOutputFile() throws IOException {
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
	public void ensureDirectoryFile(String directory) throws GenerationException {
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
