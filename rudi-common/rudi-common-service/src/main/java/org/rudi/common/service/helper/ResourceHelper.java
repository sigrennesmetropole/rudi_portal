package org.rudi.common.service.helper;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;

@Component
public class ResourceHelper {

	@Value("${temporary.directory:${java.io.tmpdir}}")
	private String temporaryDirectory;

	@Nonnull
	public File copyResourceToTempFile(Resource media) throws IOException {
		final File tempFile = File.createTempFile("rudi", FilenameUtils.getExtension(media.getFilename()),
				new File(temporaryDirectory));
		FileUtils.copyInputStreamToFile(media.getInputStream(), tempFile);
		return tempFile;
	}
}
