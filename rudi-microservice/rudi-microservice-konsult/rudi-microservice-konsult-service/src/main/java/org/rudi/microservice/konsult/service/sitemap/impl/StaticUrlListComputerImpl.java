package org.rudi.microservice.konsult.service.sitemap.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Arrays;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.konsult.core.sitemap.SitemapEntryData;
import org.rudi.microservice.konsult.core.sitemap.StaticSitemapEntry;
import org.rudi.microservice.konsult.core.sitemap.StaticSitemapEntryData;
import org.rudi.microservice.konsult.core.sitemap.UrlListTypeData;
import org.rudi.microservice.konsult.service.sitemap.UrlListComputer;
import org.sitemaps.schemas.sitemap.TUrl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StaticUrlListComputerImpl implements UrlListComputer {

	@Getter(AccessLevel.PUBLIC)
	@Value("${front.urlServer:http://www.rudi.bzh}")
	private String urlServer;

	@Override
	public UrlListTypeData getAcceptedData() {
		return UrlListTypeData.STATICS;
	}

	@Override
	public List<TUrl> compute(SitemapEntryData sitemapEntryData) throws AppServiceException {
		if (!(sitemapEntryData instanceof StaticSitemapEntryData)) {
			throw new AppServiceException(
					String.format("Le type de données recu par le computer n'est pas valide : %s au lieu de %s",
							sitemapEntryData.getClass().toString(), StaticSitemapEntryData.class.toString()));
		}

		StaticSitemapEntryData data = (StaticSitemapEntryData) sitemapEntryData;

		List<TUrl> staticUrlList = new ArrayList<>();

		for (StaticSitemapEntry entry : data.getUrlList()) {
			TUrl url = new TUrl();
			if (entry.getIsRelative()) {
				url.setLoc(buildLocation(entry.getLocation()));
			} else {
				url.setLoc(entry.getLocation());
			}
			staticUrlList.add(url);
		}
		return staticUrlList;
	}

	private String buildLocation(String location) {
		return StringUtils
				.join(Arrays.array(StringUtils.removeEnd(urlServer, "/"), StringUtils.removeStart(location, "/")), "/");
	}
}
