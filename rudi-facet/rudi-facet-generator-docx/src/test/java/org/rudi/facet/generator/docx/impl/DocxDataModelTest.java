/**
 *
 */
package org.rudi.facet.generator.docx.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.rudi.facet.generator.docx.model.AbstractDocxDataModel;
import org.rudi.facet.generator.exception.GenerationException;
import org.rudi.facet.generator.model.GenerationFormat;

/**
 * @author FNI18300
 *
 */
public class DocxDataModelTest extends AbstractDocxDataModel {

	public DocxDataModelTest() {
		super(GenerationFormat.DOCX, "generator/DocxTest.docx");
	}

	@Override
	protected String generateFileName() {
		return getFormat().generateFileName("toto");
	}

	@Override
	public Map<String, Object> getDataModel() throws GenerationException {
		Map<String, Object> data = new HashMap<>();
		data.put("count", 2L);
		data.put("date", new Date());
		data.put("items", Arrays.asList("a", "b", "c"));
		data.put("o", new DocxBeanTest("a", "title", true));
		data.put("dataUtils", this);
		return data;
	}

}
