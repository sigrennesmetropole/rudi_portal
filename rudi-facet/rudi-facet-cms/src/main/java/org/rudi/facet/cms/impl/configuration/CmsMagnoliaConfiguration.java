/**
 * 
 */
package org.rudi.facet.cms.impl.configuration;

import java.util.HashMap;
import java.util.Map;

import org.rudi.facet.cms.bean.CmsAssetType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Getter;

/**
 * @author fni18300
 *
 */
@Configuration
@ConfigurationProperties("cms.magnolia")
@PropertySource("classpath:cms-magnolia.properties")
@Getter
public class CmsMagnoliaConfiguration {

	private Map<String, String> assetTypeCategories = new HashMap<>();

	private Map<String, String> assetTypePaths = new HashMap<>();

	private Map<String, String> assetTypeClasses = new HashMap<>();

	private Map<String, String> assetTypeCssSelectors = new HashMap<>();

	@Value("${temporary.directory:${java.io.tmpdir}}")
	private String temporaryDirectory;

	private String temporaryFileExtension = ".html";

	private String temporaryFilePrefix = "rudi-cms-";

	@Value("${front-office.route-to-cms:/cms/detail}")
	private String frontOfficeRoute;

	@Value("${front-office.route-to-cms-resources:/cms/resources?resourcePath=%s}")
	private String frontOfficeResourcesRoute;

	public String getDefaultCategory(CmsAssetType cmsAssetType) {
		return assetTypeCategories.get(cmsAssetType.name().toLowerCase());
	}

	public String getPath(CmsAssetType cmsAssetType) {
		return assetTypePaths.get(cmsAssetType.name().toLowerCase());
	}

	public String getCssSelector(CmsAssetType cmsAssetType) {
		return assetTypeCssSelectors.get(cmsAssetType.name().toLowerCase());
	}

	public Class<?> getClass(CmsAssetType cmsAssetType) throws ClassNotFoundException {
		String value = assetTypeClasses.get(cmsAssetType.name().toLowerCase());
		return Thread.currentThread().getContextClassLoader().loadClass(value);
	}
}
