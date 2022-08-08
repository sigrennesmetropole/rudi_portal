/**
 * 
 */
package org.rudi.facet.generator.text.impl;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;

/**
 * @author FNI18300
 *
 */
public class CompositeTemplateLoader implements TemplateLoader {

	private static final String STRING_TEMPLATE_SOURCE = "StringTemplateSource";

	private TemplateLoader fileTemplateLoader = null;

	private TemplateLoader classTemplateLoader = null;

	private TemplateLoader stringTemplateLoader = null;

	private final List<TemplateLoader> templateLoaders = new ArrayList<>();

	/**
	 * @throws IOException
	 * 
	 */
	public CompositeTemplateLoader(File fileTemplateDirectory, ClassLoader classLoader, String basePackagePath)
			throws IOException {
		if (!fileTemplateDirectory.exists()) {
			fileTemplateDirectory.mkdirs();
		}
		addFileTemplateLoader(fileTemplateDirectory);
		addClassTemplateLoader(classLoader, basePackagePath);
		addStringTemplateLoader();
	}

	@Override
	public Object findTemplateSource(String name) throws IOException {
		Object result = null;
		if (name.startsWith(TemplateGeneratorConstants.STRING_TEMPLATE_LOADER_SHORT_PREFIX)) {
			result = stringTemplateLoader.findTemplateSource(name);
		} else {
			for (TemplateLoader templateLoader : templateLoaders) {
				result = templateLoader.findTemplateSource(name);
				if (result != null) {
					break;
				}
			}
		}
		return result;
	}

	@Override
	public long getLastModified(Object templateSource) {
		return getTemplateLoader(templateSource).getLastModified(templateSource);
	}

	@Override
	public Reader getReader(Object templateSource, String encoding) throws IOException {
		TemplateLoader templateLoader = getTemplateLoader(templateSource);
		return templateLoader.getReader(templateSource, encoding);
	}

	@Override
	public void closeTemplateSource(Object templateSource) throws IOException {
		getTemplateLoader(templateSource).closeTemplateSource(templateSource);
	}

	public void putTemplate(String name, String templateContent) {
		if (!name.startsWith(TemplateGeneratorConstants.STRING_TEMPLATE_LOADER_SHORT_PREFIX)) {
			throw new IllegalArgumentException(
					"Tempalte name must start with:" + TemplateGeneratorConstants.STRING_TEMPLATE_LOADER_SHORT_PREFIX);
		}
		((StringTemplateLoader) stringTemplateLoader).putTemplate(name, templateContent);
	}

	protected TemplateLoader getTemplateLoader(Object templateSource) {
		if (templateSource instanceof File) {
			return fileTemplateLoader;
		} else if (templateSource.getClass().getSimpleName().endsWith(STRING_TEMPLATE_SOURCE)) {
			return stringTemplateLoader;
		} else {
			return classTemplateLoader;
		}
	}

	protected void addStringTemplateLoader() {
		stringTemplateLoader = new StringTemplateLoader();
		templateLoaders.add(stringTemplateLoader);
	}

	protected void addFileTemplateLoader(File fileTemplateDirectory) throws IOException {
		fileTemplateLoader = new FileTemplateLoader(fileTemplateDirectory);
		templateLoaders.add(fileTemplateLoader);
	}

	protected void addClassTemplateLoader(ClassLoader classLoader, String basePackagePath) throws IOException {
		classTemplateLoader = new ClassTemplateLoader(classLoader, basePackagePath);
		templateLoaders.add(classTemplateLoader);
	}

}
