package org.rudi.facet.apimaccess.helper.generator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Repository
@Slf4j
public class OpenApiTemplates {

	@Cacheable("templatesByInterfaceContract")
	public Template findByInterfaceContract(String interfaceContract) throws IOException {
		final String fileName = getFileNameFromInterfaceContract(interfaceContract);
		Configuration configuration = new Configuration(Configuration.VERSION_2_3_30);
		configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
		configuration.setClassForTemplateLoading(this.getClass(), FilenameUtils.getFullPath(fileName));
		return getTemplate(interfaceContract, fileName, configuration);
	}

	@Nullable
	private Template getTemplate(String interfaceContract, String fileName, Configuration configuration) throws IOException {
		try {
			final String name = FilenameUtils.getName(fileName);
			return configuration.getTemplate(name);
		} catch (TemplateNotFoundException e) {
			log.error("Template for interfaceContract {} not found", interfaceContract, e);
			return null;
		}
	}

	private String getFileNameFromInterfaceContract(String interfaceContract) {
		return String.format("/openapi/templates-by-interface-contract/%s.json", interfaceContract);
	}

	@Cacheable("templatesExistence")
	public boolean existsByInterfaceContract(String interfaceContract) {
		final String fileName = getFileNameFromInterfaceContract(interfaceContract);
		final URL resource = this.getClass().getResource(fileName);
		return resource != null;
	}

}
