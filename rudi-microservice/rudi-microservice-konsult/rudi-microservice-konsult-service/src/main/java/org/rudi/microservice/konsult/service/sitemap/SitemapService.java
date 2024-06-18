package org.rudi.microservice.konsult.service.sitemap;

import java.io.IOException;

import org.rudi.common.core.DocumentContent;
import org.rudi.common.service.exception.AppServiceException;

public interface SitemapService {

	public void generateSitemap() throws AppServiceException;

	/**
	 * @param resourceName nom de la ressource demandée sous forme d'un string
	 * @return la ressource demandée sous forme d'un DocumentContent
	 */
	DocumentContent getRessourceByName(String resourceName) throws IOException;

	/**
	 * Initialisation du service
	 */
	void initService();

}
