/**
 * 
 */
package org.rudi.facet.generator.text.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;

import org.rudi.common.core.DocumentContent;
import org.rudi.facet.generator.TemporaryHelper;
import org.rudi.facet.generator.exception.GenerationException;
import org.rudi.facet.generator.exception.GenerationModelNotFoundException;
import org.rudi.facet.generator.impl.AbstractGenerator;
import org.rudi.facet.generator.model.DocumentDataModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author fni18300
 *
 */
@Component
@Slf4j
public class TemplateGeneratorImpl extends AbstractGenerator<DocumentDataModel> {

	@Value("${freemarker.clearCache:true}")
	private boolean freemarkerClearCache;

	@Value("${freemarker.baseDirectory}")
	private String freemarkerBaseDirectory;

	@Value("${freemarker.basePackage}")
	private String freemarkerBasePackage;

	private CompositeTemplateLoader compositeTemplateLoader = null;

	public TemplateGeneratorImpl(TemporaryHelper temporaryHelper) {
		super(temporaryHelper);
	}
	
	@Override
	public DocumentContent generateDocument(DocumentDataModel dataModel)
			throws GenerationModelNotFoundException, GenerationException, IOException {
		if (!dataModel.isFileModel() && !dataModel.isInlineModel()) {
			throw new GenerationException("Invalid format:" + dataModel.getModel());
		}
		return generateFreeMarkerDocument(dataModel);
	}

	/**
	 * @param dataModel
	 * @return un document freemarker
	 * @throws GenerationModelNotFoundException
	 * @throws GenerationException
	 * @throws IOException
	 */
	protected DocumentContent generateFreeMarkerDocument(DocumentDataModel dataModel)
			throws GenerationModelNotFoundException, GenerationException, IOException {
		DocumentContent result = null;

		if (getTemporaryHelper().getTemporaryDirectory() != null) {
			getTemporaryHelper().ensureDirectoryFile(getTemporaryHelper().getTemporaryDirectory());
		}

		File generateFile = getTemporaryHelper().createOutputFile();

		String modele = dataModel.getModel();
		Template template = initModeleFreeMarker(modele, dataModel.getLocale());
		try {
			template.process(dataModel.getDataModel(), new FileWriter(generateFile));

			result = new DocumentContent(dataModel.getOutputFileName(), dataModel.getFormat().getMimeType(),
					generateFile);

		} catch (Exception e) {
			throw new GenerationException("Failed to generate document:" + dataModel.getModel(), e);
		}

		return result;
	}

	protected Template initModeleFreeMarker(String templateName, Locale locale)
			throws GenerationModelNotFoundException {
		Template template = null;
		String realTemplateName = templateName;
		try {
			Configuration configuration = new Configuration(Configuration.VERSION_2_3_28);
			if (log.isDebugEnabled()) {
				log.debug("Freemarker model base:{} /{}", freemarkerBaseDirectory, freemarkerBasePackage);
			}
			if (compositeTemplateLoader == null) {
				compositeTemplateLoader = new CompositeTemplateLoader(new File(freemarkerBaseDirectory),
						Thread.currentThread().getContextClassLoader(), freemarkerBasePackage);
			}
			configuration.setTemplateLoader(compositeTemplateLoader);

			if (templateName.startsWith(TemplateGeneratorConstants.STRING_TEMPLATE_LOADER_PREFIX)) {
				realTemplateName = extractTemplateName(templateName);
				compositeTemplateLoader.putTemplate(realTemplateName, extractTemplateContent(templateName));
			}
			configuration.setTemplateUpdateDelayMilliseconds(5L * 60L * 1000L);
			configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
			configuration.setLocale(locale);
			configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

			if (freemarkerClearCache) {
				configuration.clearTemplateCache();
			}

			template = configuration.getTemplate(realTemplateName);
		} catch (Exception e) {
			String message = "Failed to load template " + templateName;
			log.error(message);
			throw new GenerationModelNotFoundException(message, e);
		}

		return template;
	}

	/**
	 * Le nom complet peut être de la forme:<br>
	 * <ul>
	 * <li>stl:[nom du template]:[contenu inline]</li>
	 * <li>ou stl:[contenu inline]</li>
	 * </ul>
	 * 
	 * @param templateFullName
	 * @return le contenu inline
	 */
	private String extractTemplateContent(String templateFullName) {
		String content = templateFullName.substring(TemplateGeneratorConstants.STRING_TEMPLATE_LOADER_PREFIX.length());
		int index1 = content.indexOf(':');
		if (index1 >= 0) {
			content = content.substring(index1 + 1);
		}
		return content;
	}

	private String extractTemplateName(String templateFullName) {
		String name = templateFullName;
		int index = name.indexOf(':', TemplateGeneratorConstants.STRING_TEMPLATE_LOADER_PREFIX.length());
		if (index >= 0) {
			name = name.substring(0, index).replace(":", "_");
		} else {
			name = TemplateGeneratorConstants.STRING_TEMPLATE_LOADER_SHORT_PREFIX + "_" + UUID.randomUUID().toString();
		}
		return name;
	}

}
