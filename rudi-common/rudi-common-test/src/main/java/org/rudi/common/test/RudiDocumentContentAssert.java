package org.rudi.common.test;

import org.assertj.core.api.FileAssert;
import org.rudi.common.core.DocumentContent;

@SuppressWarnings({ "java:S2160" }) // La méthode equals correspond à une méthode déprécié de Assert
public class RudiDocumentContentAssert extends RudiObjectAssert<DocumentContent> {

	private final FileAssert fileAssert;

	RudiDocumentContentAssert(DocumentContent actual) {
		super(actual);
		fileAssert = new FileAssert(actual.getFile());
	}

	public void hasFileContent(String expected) {
		hasFieldOrPropertyWithValue("isFile", true);
		hasFieldOrPropertyWithValue("fileSize", (long) expected.length());
		fileAssert.hasContent(expected);
	}

	/**
	 * @param contentType see {@link org.springframework.http.MediaType}
	 */
	public RudiDocumentContentAssert hasContentType(String contentType) {
		hasFieldOrPropertyWithValue("contentType", contentType);
		return this;
	}
}
