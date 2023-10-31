package org.rudi.facet.apimaccess.helper.generator;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.annotation.Nullable;

import org.apache.commons.io.FilenameUtils;
import org.rudi.facet.apimaccess.constant.BeanIds;
import org.rudi.facet.dataset.bean.InterfaceContract;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class OpenApiTemplates {

	@Cacheable(value = BeanIds.API_MACCESS_TEMPLATE_BY_INTERFACE_CONTRACT, cacheManager = BeanIds.API_MACCESS_CACHE_MANAGER)
	public Template findByInterfaceContract(InterfaceContract interfaceContract) throws IOException {
		final String fileName = getFileNameFromInterfaceContract(interfaceContract);
		Configuration configuration = new Configuration(Configuration.VERSION_2_3_30);
		configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
		configuration.setClassForTemplateLoading(this.getClass(), FilenameUtils.getFullPath(fileName));
		return getTemplate(interfaceContract, fileName, configuration);
	}

	@Cacheable(value = BeanIds.API_MACCESS_TEMPLATE_EXISTENCE, cacheManager = BeanIds.API_MACCESS_CACHE_MANAGER)
	public boolean existsByInterfaceContract(InterfaceContract interfaceContract) {
		final String fileName = getFileNameFromInterfaceContract(interfaceContract);
		final URL resource = this.getClass().getResource(fileName);
		return resource != null;
	}

	@Nullable
	private Template getTemplate(InterfaceContract interfaceContract, String fileName, Configuration configuration)
			throws IOException {
		try {
			final String name = FilenameUtils.getName(fileName);
			return configuration.getTemplate(name);
		} catch (TemplateNotFoundException e) {
			log.error("Template for interfaceContract {} not found", interfaceContract, e);
			return null;
		}
	}

	private String getFileNameFromInterfaceContract(InterfaceContract interfaceContract) {
		return String.format("/openapi/templates-by-interface-contract/%s.json", interfaceContract.getUrlPath());
	}

}
