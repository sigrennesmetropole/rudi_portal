/**
 * 
 */
package org.rudi.facet.generator.model;

import lombok.Getter;

/**
 * @author FNI18300
 *
 */
public enum GenerationFormat {

	/** Mime-type PDF */
	PDF("application/pdf", "pdf"),
	/** Mime-type Excel */
	EXCEL("application/vnd.ms-excel", "xls"),
	/** Mime-type HTML */
	HTML("text/html", "html"),
	/** Mime-type Text */
	TEXT("text/plain", "txt"),
	/** Mime-type Text */
	XML("application/xml", "xml"),
	/** Mime-type CSV */
	CSV("text/csv", "csv"),
	/** Mime-type ZIP */
	ZIP("application/zip", "zip"),
	/** Mime-type json */
	JSON("application/json", "json"),
	/** Docx */
	DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx"),
	DOC("application/msword", "doc"), DOT("application/msword", "dot"),
	DOTX("application/vnd.openxmlformats-officedocument.wordprocessingml.template", "dotx");

	@Getter
	private String mimeType;

	@Getter
	private String extension;

	/**
	 * Constructeur pour GenerationFormat
	 * 
	 * @param format
	 */
	private GenerationFormat(String format, String extension) {
		this.mimeType = format;
		this.extension = extension;
	}

	/**
	 * @param prefix
	 * @return le nom d'un fichier avec son extension
	 */
	public String generateFileName(String prefix) {
		if (prefix == null) {
			throw new IllegalArgumentException("le nom du fichier ne peut être null");
		}
		return new StringBuffer(prefix).append('.').append(getExtension()).toString();
	}

	/**
	 * @param mimeType
	 * @return le format pour le type mime
	 */
	public static GenerationFormat lookupFromMimeType(String mimeType) {
		GenerationFormat result = null;
		for (GenerationFormat formatDocumentEnum : values()) {
			if (formatDocumentEnum.getMimeType().equalsIgnoreCase(mimeType)) {
				result = formatDocumentEnum;
			}
		}
		return result;
	}

	/**
	 * @param extension
	 * @return le format pour l'extension
	 */
	public static GenerationFormat lookupFromExtension(String extension) {
		GenerationFormat result = null;
		for (GenerationFormat formatDocumentEnum : values()) {
			if (formatDocumentEnum.getExtension().equalsIgnoreCase(extension)) {
				result = formatDocumentEnum;
			}
		}
		return result;
	}
}
