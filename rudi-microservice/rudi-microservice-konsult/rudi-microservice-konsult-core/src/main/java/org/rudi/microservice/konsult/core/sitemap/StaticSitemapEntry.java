package org.rudi.microservice.konsult.core.sitemap;

import lombok.Data;

@Data
public class StaticSitemapEntry {
	private String location;
	private Boolean isRelative;

}
