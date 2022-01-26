package org.rudi.common.core;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class DocumentContent {
	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentContent.class);

	@Getter
	private final String fileName;
	@Getter
	private final String contentType;
	@Getter
	private final long fileSize;
	@Getter
	private final File file;
	private final String url;
	private InputStream fileStream;

	/**
	 * Constructeur pour DocumentContent @param fileName
	 */
	public DocumentContent(String fileName, String contentType, File file) {
		this(fileName, contentType, file != null ? file.length() : -1, file);
	}

	/**
	 * Constructeur pour DocumentContent
	 */
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
