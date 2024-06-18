package org.rudi.microservice.konsult.service.sitemap.impl;

import java.util.ArrayList;
import java.util.List;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.konsult.core.sitemap.SitemapEntryData;
import org.rudi.microservice.konsult.core.sitemap.UrlListTypeData;
import org.rudi.microservice.konsult.service.sitemap.UrlListComputer;
import org.sitemaps.schemas.sitemap.TUrl;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProjectsUrlListComputerImpl implements UrlListComputer {

	@Override
	public UrlListTypeData getAcceptedData() {
		return UrlListTypeData.PROJECTS;
	}

	@Override
	public List<TUrl> compute(SitemapEntryData sitemapEntryData) throws AppServiceException {
		log.error("TODO implement computer for projects elements");
		return new ArrayList<>();
	}

}
