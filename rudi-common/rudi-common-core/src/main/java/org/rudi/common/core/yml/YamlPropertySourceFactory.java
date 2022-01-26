/**
 * 
 */
package org.rudi.common.core.yml;

import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

/**
 * @author FNI18300
 *
 */
public class YamlPropertySourceFactory implements PropertySourceFactory {

	@Override
	public PropertySource<?> createPropertySource(String name, EncodedResource encodedResource) throws IOException {
		YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();

		factory.setResources(encodedResource.getResource());
		Properties properties = factory.getObject();
		if (properties == null) {
			properties = new Properties();
		}

		String sourceName = "unknown";
		if (encodedResource.getResource().getFilename() != null) {
			sourceName = encodedResource.getResource().getFilename();
		}
		return new PropertiesPropertySource(sourceName, properties);
	}
}