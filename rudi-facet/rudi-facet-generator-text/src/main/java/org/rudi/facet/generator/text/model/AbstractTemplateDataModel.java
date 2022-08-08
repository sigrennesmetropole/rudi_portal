/**
 * 
 */
package org.rudi.facet.generator.text.model;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.apache.commons.io.FilenameUtils;
import org.rudi.facet.generator.exception.GenerationException;
import org.rudi.facet.generator.model.GenerationFormat;
import org.rudi.facet.generator.model.impl.AbstractDocumentDataModel;
import org.rudi.facet.generator.text.impl.TemplateGeneratorConstants;

/**
 * @author fni18300
 *
 */
public abstract class AbstractTemplateDataModel extends AbstractDocumentDataModel {

	protected AbstractTemplateDataModel(@NotNull GenerationFormat format, @NotNull Locale locale,
			@NotNull String model) {
		super(format, locale, model);
	}

	@Override
	public boolean isInlineModel() {
		return getModel().startsWith(TemplateGeneratorConstants.STRING_TEMPLATE_LOADER_PREFIX);
	}

	@Override
	public boolean isFileModel() {
		if (isInlineModel()) {
			return false;
		}
		try {
			String extension = FilenameUtils.getExtension(getModel());
			return TemplateGeneratorConstants.getFreeMarkerExtension().contains(extension);
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public Map<String, Object> getDataModel() throws GenerationException {
		Map<String, Object> data = new HashMap<>();
		data.put("dataUtils", this);
		fillDataModel(data);
		return data;
	}

	protected abstract void fillDataModel(Map<String, Object> data);

}
