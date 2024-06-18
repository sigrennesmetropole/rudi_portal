package org.rudi.microservice.konsult.service.sitemap;

import java.util.List;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.konsult.core.sitemap.SitemapEntryData;
import org.rudi.microservice.konsult.core.sitemap.UrlListTypeData;
import org.sitemaps.schemas.sitemap.TUrl;

public interface UrlListComputer {

	default boolean accept(UrlListTypeData urlListTypeData) {
		return urlListTypeData == getAcceptedData();
	}

	UrlListTypeData getAcceptedData();

	List<TUrl> compute(SitemapEntryData entryData) throws AppServiceException;

}
