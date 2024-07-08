package org.rudi.microservice.konsult.service.sitemap.impl;

import java.util.ArrayList;
import java.util.List;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.konsult.core.sitemap.SitemapDescriptionData;
import org.rudi.microservice.konsult.core.sitemap.SitemapEntryData;
import org.rudi.microservice.konsult.core.sitemap.UrlListTypeData;
import org.rudi.microservice.konsult.service.sitemap.AbstractUrlListComputer;
import org.sitemaps.schemas.sitemap.TUrl;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class CmsUrlListComputerImpl extends AbstractUrlListComputer {

	@Override
	public UrlListTypeData getAcceptedData() {
		return UrlListTypeData.CMS;
	}

	@Override
	public List<TUrl> computeInternal(SitemapEntryData sitemapEntryData, SitemapDescriptionData sitemapDescriptionData) throws AppServiceException {
		log.error("TODO implement computer for CMS elements");
		return new ArrayList<>();
	}

}
