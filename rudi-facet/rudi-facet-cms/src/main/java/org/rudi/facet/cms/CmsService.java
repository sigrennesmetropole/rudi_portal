/**
 * RUDI Portail
 */
package org.rudi.facet.cms;

import java.util.List;
import java.util.Locale;

import org.rudi.facet.cms.bean.CmsAsset;
import org.rudi.facet.cms.bean.CmsAssetType;
import org.rudi.facet.cms.bean.CmsCategory;
import org.rudi.facet.cms.exception.CmsException;
import org.rudi.facet.cms.impl.model.CmsRequest;

/**
 * @author FNI18300
 *
 */
public interface CmsService {

	/**
	 * Récupération de toutes les catégories
	 * 
	 * @return La liste des catégories
	 * @throws CmsException
	 */
	List<CmsCategory> getCategories() throws CmsException;

	/**
	 * Récupération d'une catégorie par son path (exemple : /rudi/terms)</br>
	 * On utilise le path car le nom n'est pas unique
	 * 
	 * @param path le path
	 * @return
	 * @throws CmsException
	 */
	CmsCategory getCategory(String path) throws CmsException;

	/**
	 * Rendition d'un objet d'un certain type par son id avec un template
	 * 
	 * @param assetType     le type
	 * @param assetId       l'id
	 * @param assetTemplate le template
	 * @param locale        la locale (par défaut on tomber sur fr)
	 * @return un asset rendifié
	 * @throws CmsException
	 */
	CmsAsset renderAsset(CmsAssetType assetType, String assetId, String assetTemplate, Locale locale)
			throws CmsException;

	/**
	 * Rendition de tous les assets selons les critères utilisés
	 * 
	 * @param assetType     le type
	 * @param assetTemplate le template
	 * @param request       la requête vers le CMS
	 * @param locale        la locale
	 * @param categories    les catégories
	 * @param offset
	 * @param limit
	 * @param order
	 * @return
	 * @throws CmsException
	 */
	List<CmsAsset> renderAssets(CmsAssetType assetType, String assetTemplate, CmsRequest request, Integer offset,
			Integer limit, String order) throws CmsException;
}
