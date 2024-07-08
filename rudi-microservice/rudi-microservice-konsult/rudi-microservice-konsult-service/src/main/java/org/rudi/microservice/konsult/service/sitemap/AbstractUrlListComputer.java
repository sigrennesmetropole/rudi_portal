package org.rudi.microservice.konsult.service.sitemap;

import java.util.ArrayList;
import java.util.List;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.konsult.core.sitemap.SitemapDescriptionData;
import org.rudi.microservice.konsult.core.sitemap.SitemapEntryData;
import org.rudi.microservice.konsult.core.sitemap.UrlListTypeData;
import org.rudi.microservice.konsult.service.helper.sitemap.SitemapUtils;
import org.sitemaps.schemas.sitemap.TUrl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractUrlListComputer {

	/**
	 * @return une UrlListTypeData
	 */
	public abstract UrlListTypeData getAcceptedData();

	/**
	 * @param sitemapEntryData un SitemapEntryData
	 * @return un boolean indiquant si sitemapEntryData est du même type que celui accepté
	 */
	public boolean accept(SitemapEntryData sitemapEntryData) {
		return sitemapEntryData.getType() == getAcceptedData();
	}

	/**
	 * Méthode de vérification de type, d'appel à computeInternal et de gestion des limit et des tailles des URLs des Listes
	 *
	 * @param entryData              un SitemapEntryData
	 * @param sitemapDescriptionData un SitemapDescriptionData
	 * @return une Liste de TUrl
	 */
	public List<TUrl> compute(SitemapEntryData entryData, SitemapDescriptionData sitemapDescriptionData) throws AppServiceException {
		List<TUrl> listUrl = new ArrayList<>();
		if (!accept(entryData)) {
			log.debug("Le type de données reçu par le computer n'est pas valide");
			return listUrl;
		}

		try {
			listUrl = computeInternal(entryData, sitemapDescriptionData);
		} catch (AppServiceException e) {
			log.error("Erreur lors de la récupération des éléments de type {} pour le sitemap", entryData.getType(), e);
		}

		List<TUrl> listLimit = SitemapUtils.limitList(listUrl, sitemapDescriptionData.getMaxUrlCount());
		log.info("Sitemap: nombre d'url pour {} avant la limite: {}, après la limite: {}", entryData.getType(), listUrl.size(), listLimit.size());
		return SitemapUtils.truncateUrls(listLimit, sitemapDescriptionData.getMaxUrlSize());
	}

	protected abstract List<TUrl> computeInternal(SitemapEntryData entryData, SitemapDescriptionData sitemapDescriptionData) throws AppServiceException;
}
