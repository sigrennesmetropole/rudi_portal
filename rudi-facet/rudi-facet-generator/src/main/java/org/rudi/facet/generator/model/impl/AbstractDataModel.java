/**
 * 
 */
package org.rudi.facet.generator.model.impl;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.rudi.facet.generator.model.DataModel;
import org.rudi.facet.generator.model.DataModelUtils;
import org.rudi.facet.generator.model.GenerationFormat;

/**
 * Data model abstrait générique
 * 
 * @author FNI18300
 *
 */
public abstract class AbstractDataModel implements DataModel {

	private GenerationFormat format;

	private String outputFileName;

	/**
	 * Constructeur pour DataModel
	 * 
	 * @param format
	 */
	protected AbstractDataModel(@NotNull GenerationFormat format) {
		this.format = format;
	}

	protected abstract String generateFileName();

	public String getOutputFileName() {
		if (outputFileName == null) {
			outputFileName = generateFileName();
		}
		return outputFileName;
	}

	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

	public GenerationFormat getFormat() {
		return format;
	}

	public void setFormat(GenerationFormat format) {
		this.format = format;
	}

	/**
	 * @param input
	 * @return la chaine encodé HTML
	 */
	public String encodeOdt(String input) {
		return DataModelUtils.encodeOdt(input);
	}

	/**
	 * @param input
	 * @return la chaine encodé HTML
	 */
	public String encodeHtml(String input) {
		return DataModelUtils.encodeHtml(input);
	}

	/**
	 * @param input
	 * @return la chaine encodé HTML
	 */
	public String encodeHtmlMemo(String input) {
		return DataModelUtils.encodeHtmlMemo(input);
	}

	/**
	 * @param input
	 * @return la chaine encodé HTML
	 */
	public String encodeCharset(String input, String charset) {
		try {
			return DataModelUtils.encodeCharset(input, charset);
		} catch (Exception e) {
			return input;
		}
	}

	/**
	 * @param input
	 * @return la chaine trimmée
	 */
	public String trim(String input) {
		return StringUtils.trim(input);
	}

	/**
	 * @param input
	 * @param maxlength
	 * @param suspens
	 * @return la chaine tronquée
	 */
	public String truncate(String input, int maxlength, String suspens) {
		return StringUtils.abbreviate(input, suspens, maxlength);
	}

	/**
	 * @param input
	 * @param lineLength
	 * @return
	 */
	public int countLines(String input, int lineLength) {
		return DataModelUtils.countLines(input, lineLength);
	}

	/**
	 * @param input
	 * @param maxLines
	 * @param lineLength
	 * @param suspens
	 * @return la chaine tronquées en nombre de lignes
	 */
	public String truncateLines(String input, int maxLines, int lineLength, String suspens) {
		return DataModelUtils.truncateLines(input, maxLines, lineLength, suspens);
	}

}
