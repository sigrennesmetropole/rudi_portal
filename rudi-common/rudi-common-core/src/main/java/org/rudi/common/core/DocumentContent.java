package org.rudi.common.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.annotation.Nonnull;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import lombok.Builder;
import lombok.Getter;

public class DocumentContent {
	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentContent.class);

	private static final String DEFAULT_MIME_TYPE = "application/octet-stream";
	private static final String TEMP_FILE_EXTENSION = ".file";
	private static final String TEMP_FILE_PREFIX = "upload";

	@Getter
	private final String fileName;
	@Getter
	private final String contentType;
	@Getter
	private final long fileSize;
	@Getter
	private final File file;
	@Getter
	private final String url;
	private InputStream fileStream;

	/**
	 * Constructeur pour DocumentContent
	 *
	 * @param contentType exemple : {@link org.springframework.http.MediaType#IMAGE_PNG_VALUE MediaType.IMAGE_PNG_VALUE}
	 */
	public DocumentContent(String contentType, @Nonnull File file) {
		this(file.getName(), contentType, file);
	}

	/**
	 * Constructeur pour DocumentContent @param fileName
	 */
	public DocumentContent(String fileName, String contentType, File file) {
		this(fileName, contentType, file != null ? file.length() : -1, file);
	}

	/**
	 * Constructeur pour DocumentContent
	 */
	@Builder(toBuilder = true)
	public DocumentContent(String fileName, String contentType, long fileSize, InputStream fileStream) {
		super();
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.contentType = contentType;
		this.fileStream = fileStream;
		this.file = null;
		this.url = null;
	}

	/**
	 * Constructeur pour DocumentContent
	 */
	public DocumentContent(String fileName, String contentType, long fileSize, File file) {
		super();
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.contentType = contentType;
		this.file = file;
		this.url = null;
	}

	public static DocumentContent fromResource(Resource resource, boolean asFile) throws IOException {
		DocumentContent documentContent;
		String fileName = resource.getFilename();
		String mimeType = DEFAULT_MIME_TYPE;
		if (fileName != null) {
			mimeType = URLConnection.guessContentTypeFromName(resource.getFilename());
		} else {
			fileName = "unknown";
		}
		if (resource.isFile()) {
			documentContent = new DocumentContent(fileName, mimeType, resource.getFile());
		} else if (!asFile) {
			documentContent = new DocumentContent(fileName, mimeType, resource.contentLength(),
					resource.getInputStream());
		} else {
			File tmpFile = File.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_EXTENSION);
			FileUtils.copyInputStreamToFile(resource.getInputStream(), tmpFile);
			documentContent = new DocumentContent(fileName, mimeType, tmpFile);
		}
		return documentContent;
	}

	/**
	 * Permet de transformer une resource en Document content via son path
	 *
	 * @param path Le chemin vers le fichier que l'on souhaite transformer en DocumentContent
	 * @return un document content contenant le fichier au path indiqué.
	 * @throws IOException en cas de problème avec la lecture du fichier
	 */
	public static DocumentContent fromResourcePath(String path) throws IOException {
		URL url = Thread.currentThread().getContextClassLoader().getResource(path);

		try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);) {
			String file = url.getFile();
			String extension = FilenameUtils.getExtension(file);
			String mimeType = URLConnection.guessContentTypeFromName(file);
			String fileName = FilenameUtils.getName(file);

			File tmpFile = File.createTempFile(TEMP_FILE_PREFIX, "." + extension);
			FileUtils.copyInputStreamToFile(inputStream, tmpFile);
			return new DocumentContent(fileName, mimeType, tmpFile);
		}

	}

	/**
	 * @return vrai si contient un fichier
	 */
	public boolean isFile() {
		return file != null;
	}

	/**
	 * @return vrai si url
	 */
	public boolean isURL() {
		return url != null;
	}

	/**
	 * @return vrai si est un stream
	 */
	public boolean isStream() {
		return fileStream != null;
	}

	/**
	 * Accesseur pour fileStream
	 *
	 * @return le fileStream
	 */
	public InputStream getFileStream() throws FileNotFoundException {
		if (fileStream == null && file != null) {
			fileStream = new FileInputStream(file);
		}
		return fileStream;
	}

	/**
	 * cloture du flux s'il est ouvert
	 */
	public void closeStream() {
		if (fileStream != null) {
			try {
				fileStream.close();
				fileStream = null;
			} catch (Exception e) {
				LOGGER.debug("Impossible de clore le flux:{}", this, e);
			}
		}
	}

	@Override
	public String toString() {
		return "DocumentContent [" + (url != null ? "url=" + url + ", " : "")
				+ (fileName != null ? "fileName=" + fileName + ", " : "") + "fileSize=" + fileSize + ", "
				+ (file != null ? "file=" + file + ", " : "")
				+ (contentType != null ? "contentType=" + contentType + ", " : "")
				+ (fileStream != null ? "fileStream=" + fileStream : "") + "]";
	}

}
