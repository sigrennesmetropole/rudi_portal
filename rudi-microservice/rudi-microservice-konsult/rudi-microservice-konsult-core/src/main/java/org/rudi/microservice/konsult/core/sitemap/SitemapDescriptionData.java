/**
 * RUDI Portail
 */
package org.rudi.microservice.konsult.core.sitemap;

import java.util.List;

import lombok.Data;

/**
 * Description du JSON permettant de décrire les éléments à embarquer dans le sitemap.xml
 * 
 * @author PFO23835
 *
 */
@Data
public class SitemapDescriptionData {

	private StaticSitemapEntryData staticSitemapEntries;
	private List<SitemapEntryData> sitemapEntries;

}
