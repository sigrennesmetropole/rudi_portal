package org.rudi.facet.crypto;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SecuredFileHelper {

	private Path tempDirectory;

	/**
	 * Équivalent de {@link File#createTempFile(String, String)} mais qui génère un fichier temporaire dans un dossier
	 * sécurisé.
	 */
	public Path createSecuredTempFile(String prefix, String suffix) throws IOException {
		return Files.createTempFile(getOrCreateTempDirectory(), prefix, suffix);
	}

	private Path getOrCreateTempDirectory() throws IOException {
		if (tempDirectory == null) {
			tempDirectory = createSecuredTempDirectory();
		}
		return tempDirectory;
	}

	private Path createSecuredTempDirectory() throws IOException {
		final var securedTempDirectory = Files.createTempDirectory("rudi-secured");
		log.info("Secured temporary directory created : " + securedTempDirectory);
		try {
			setOwnerOnlyPermissions(securedTempDirectory);
		} catch (final IOException | RuntimeException e) {
			Files.delete(securedTempDirectory);
			throw e;
		}
		return securedTempDirectory;
	}

	private void setOwnerOnlyPermissions(Path path) throws IOException {
		final var file = path.toFile();

		final Set<PosixFilePermission> perms = new HashSet<>();
		perms.add(PosixFilePermission.OWNER_READ);
		perms.add(PosixFilePermission.OWNER_WRITE);

		setPosixFilePermissions(file, perms);
	}

	private void setPosixFilePermissions(File file, Set<PosixFilePermission> perms) throws IOException {
		if (SystemUtils.IS_OS_UNIX) {
			Files.setPosixFilePermissions(file.toPath(), perms);
			return;
		}
		throw new NotImplementedException("Don't know how to set posix file permissions on this system");
	}

}
