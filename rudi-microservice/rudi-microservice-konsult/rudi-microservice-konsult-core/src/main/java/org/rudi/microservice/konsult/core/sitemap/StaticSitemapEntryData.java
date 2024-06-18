package org.rudi.microservice.konsult.core.sitemap;

import java.util.List;

import lombok.Data;

@Data
public class StaticSitemapEntryData extends SitemapEntryData {
	List<StaticSitemapEntry> urlList;

	@Override
	public UrlListTypeData getType() {

		return UrlListTypeData.STATICS;
	}

}
