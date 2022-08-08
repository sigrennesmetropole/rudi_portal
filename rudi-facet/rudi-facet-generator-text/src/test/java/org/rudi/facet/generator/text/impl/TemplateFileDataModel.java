/**
 * 
 */
package org.rudi.facet.generator.text.impl;

import java.util.Locale;
import java.util.Map;

import org.rudi.facet.generator.model.GenerationFormat;
import org.rudi.facet.generator.text.model.AbstractTemplateDataModel;

/**
 * @author fni18300
 *
 */
public class TemplateFileDataModel extends AbstractTemplateDataModel {

	public TemplateFileDataModel() {
		super(GenerationFormat.HTML, Locale.FRENCH, "templates/test.html");
	}

	@Override
	protected void fillDataModel(Map<String, Object> data) {
		data.put("a", "rudi");
		data.put("b", 1L);
	}

	@Override
	protected String generateFileName() {
		return getFormat().generateFileName("test");
	}

}
