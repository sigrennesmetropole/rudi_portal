/**
 * 
 */
package org.rudi.facet.generator.model.impl;

import java.util.Locale;

import javax.validation.constraints.NotNull;

import org.rudi.facet.generator.model.DocumentDataModel;
import org.rudi.facet.generator.model.GenerationFormat;

/**
 * Data model pour les données injectées dans les modèles de documents
 * 
 * @author FNI18300
 *
 */
public abstract class AbstractDocumentDataModel extends AbstractDataModel implements DocumentDataModel {

	private String model;

	private Locale locale;

	/**
	 * Constructeur pour DataModel
	 * 
	 * @param format
	 */
	protected AbstractDocumentDataModel(@NotNull GenerationFormat format, Locale locale) {
		this(format, locale, null);
	}

	protected AbstractDocumentDataModel(@NotNull GenerationFormat format, Locale locale, String model) {
		super(format);
		this.model = model;
		this.locale = locale;
	}

	@Override
	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	@Override
	protected String generateFileName() {
		String prefix = getModel();
		if (prefix == null) {
			prefix = getClass().getSimpleName().toLowerCase();
		}
		return getFormat().generateFileName(prefix);
	}

	@Override
	public Locale getLocale() {
		return locale;
	}
}
