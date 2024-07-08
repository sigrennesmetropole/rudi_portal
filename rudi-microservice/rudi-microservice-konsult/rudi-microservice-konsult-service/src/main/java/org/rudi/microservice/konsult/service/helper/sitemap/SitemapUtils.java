package org.rudi.microservice.konsult.service.helper.sitemap;

import java.text.Normalizer;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.sitemaps.schemas.sitemap.TUrl;

public class SitemapUtils {
	private SitemapUtils() {
	}

	/**
	 * Methode de normalisation d'une string provenant du front
	 * normalizeString -> uri-component-codec.ts
	 *
	 * @param text un String
	 * @return le text normalizé respectant l'encodage d'une URL
	 */
	public static String normalize(String text) {
		return Normalizer
				.normalize(text, Normalizer.Form.NFD)
				.replaceAll("\\p{InCombiningDiacriticalMarks}+", "") // Retire les accents et caractères spéciaux
				.replaceAll("[^a-zA-Z0-9]", "-") // remplace les espaces et les ":" par des "-"
				.replaceAll("-+", "-") // remplace les "-" multiple par un seul "-"
				.toLowerCase(); // met tout en minuscules
	}


	/**
	 * limite la taille de la liste donnée à un certain nombre d'éléments.
	 *
	 * @param urlList une liste de TUrl
	 * @param limit   un int
	 * @return une liste de TUrl contenant au maximum 'limit' éléments
	 */
	public static List<TUrl> limitList(List<TUrl> urlList, int limit) {
		if (CollectionUtils.isEmpty(urlList)) {
			return urlList;
		}

		return urlList.stream().limit(limit).collect(Collectors.toList());
	}

	/**
	 * Tronque chaque URL dans la liste à une longueur maximale spécifiée.
	 *
	 * @param urlList    une liste d'objets TUrl
	 * @param urlMaxSize la longueur maximale pour chaque URL
	 * @return une liste d'objets TUrl avec des URLs tronquées à la longueur maximale spécifiée
	 */
	public static List<TUrl> truncateUrls(List<TUrl> urlList, int urlMaxSize) {
		if (CollectionUtils.isEmpty(urlList)) {
			return List.of();
		}

		urlList.forEach(url -> url.setLoc(StringUtils.defaultIfEmpty(StringUtils.substring(url.getLoc(), 0, urlMaxSize), StringUtils.EMPTY)));

		return urlList;
	}

}
