package org.rudi.microservice.kos.service.helper;

import org.ehcache.Cache;
import org.rudi.common.core.DocumentContent;
import org.rudi.common.core.resources.ResourcesHelper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.Getter;
import static org.rudi.microservice.kos.service.constant.BeanIds.CONCEPT_ICONS_CACHE;

@Component
public class ResourceIconsHelper extends ResourcesHelper {

	@Getter(AccessLevel.PROTECTED)
	@Value("${customization.base-package:customization}")
	private String basePackage;

	@Getter(AccessLevel.PROTECTED)
	@Value("${customization.base-directory:}")
	private String baseDirectory;

	@Getter(AccessLevel.PROTECTED)
	private final Cache<String, DocumentContent> cache;


	ResourceIconsHelper(@Qualifier(CONCEPT_ICONS_CACHE) Cache<String, DocumentContent> cache){
		this.cache = cache;
	}

}
