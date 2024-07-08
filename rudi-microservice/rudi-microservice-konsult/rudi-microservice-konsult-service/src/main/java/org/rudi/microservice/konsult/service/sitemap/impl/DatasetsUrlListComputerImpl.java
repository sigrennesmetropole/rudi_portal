package org.rudi.microservice.konsult.service.sitemap.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Arrays;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.bean.DatasetSearchCriteria;
import org.rudi.facet.kaccess.bean.MetadataList;
import org.rudi.microservice.konsult.core.sitemap.SitemapDescriptionData;
import org.rudi.microservice.konsult.core.sitemap.SitemapEntryData;
import org.rudi.microservice.konsult.core.sitemap.UrlListTypeData;
import org.rudi.microservice.konsult.service.metadata.MetadataService;
import org.rudi.microservice.konsult.service.sitemap.AbstractUrlListComputer;
import org.sitemaps.schemas.sitemap.TUrl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static org.rudi.microservice.konsult.service.helper.sitemap.SitemapUtils.normalize;

@Component
@Slf4j
@RequiredArgsConstructor
public class DatasetsUrlListComputerImpl extends AbstractUrlListComputer {

	private final MetadataService metadataService;

	@Getter(AccessLevel.PUBLIC)
	@Value("${front.urlServer:http://www.rudi.bzh}")
	private String urlServer;

	@Getter(AccessLevel.PUBLIC)
	@Value("/catalogue/detail/")
	private String catalogueUrlPrefixe;

	@Override
	public UrlListTypeData getAcceptedData() {
		return UrlListTypeData.DATASETS;
	}

	@Override
	public List<TUrl> computeInternal(SitemapEntryData sitemapEntryData, SitemapDescriptionData sitemapDescriptionData) throws AppServiceException {

		MetadataList metadataList;

		try {
			metadataList = metadataService
					.searchMetadatas(new DatasetSearchCriteria().limit(sitemapDescriptionData.getMaxUrlCount()).offset(0));
		} catch (DataverseAPIException e) {
			throw new AppServiceException(
					String.format("Une erreur s'est produite lors de la récupération des datasets : %s ",
							e.getApiResponseInfo()));
		}

		List<TUrl> datasetUrlList = new ArrayList<>();
		metadataList.getItems().forEach(metadata -> {
			TUrl url = new TUrl();
			url.setLoc(buildLocation(metadata.getGlobalId(), metadata.getResourceTitle()));
			datasetUrlList.add(url);
		});

		return datasetUrlList;
	}

	private String buildLocation(UUID uuid, String title) {
		return StringUtils
				.join(Arrays.array(StringUtils.removeEnd(urlServer, "/"), catalogueUrlPrefixe, uuid, "/", normalize(title)));
	}

}
