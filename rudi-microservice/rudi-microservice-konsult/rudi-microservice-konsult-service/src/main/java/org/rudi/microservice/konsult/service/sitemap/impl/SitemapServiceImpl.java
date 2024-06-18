package org.rudi.microservice.konsult.service.sitemap.impl;

import java.io.IOException;

import org.rudi.common.core.DocumentContent;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.konsult.core.sitemap.SitemapDescriptionData;
import org.rudi.microservice.konsult.service.helper.sitemap.SitemapHelper;
import org.rudi.microservice.konsult.service.sitemap.SitemapService;
import org.sitemaps.schemas.sitemap.Urlset;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SitemapServiceImpl implements SitemapService {

	private final SitemapHelper sitemapHelper;

	@Override
	public void generateSitemap() throws AppServiceException {
		SitemapDescriptionData sitemapDescriptionData = sitemapHelper.getSitemapDescriptionData();
		Urlset urlset = sitemapHelper.buildUrlset(sitemapDescriptionData);
		sitemapHelper.storeSitemapFile(urlset);
	}

	@Override
	public DocumentContent getRessourceByName(String resourceName) throws IOException {
		return sitemapHelper.loadResources(resourceName);
	}

	@Override
	public void initService() {
		sitemapHelper.fillResourceMapping(sitemapHelper.getSitemapGeneratedFilename(), "sitemap");
	}
}
