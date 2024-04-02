/**
 * RUDI Portail
 */
package org.rudi.facet.cms.impl;

import org.rudi.facet.cms.bean.CmsAssetType;
import org.rudi.facet.cms.impl.configuration.BeanIds;
import org.rudi.facet.cms.impl.configuration.CmsMagnoliaConfiguration;
import org.rudi.facet.cms.impl.model.CmsMagnoliaProjectValue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author FNI18300
 *
 */
@Component
public class CmsMagnoliaProjectValuesHandler extends AbstractCmsMagnoliaHandler<CmsMagnoliaProjectValue> {

	public CmsMagnoliaProjectValuesHandler(CmsMagnoliaConfiguration cmsMagnoliaConfiguration,
			@Qualifier(BeanIds.CMS_WEB_CLIENT) WebClient magnoliaWebClient) {
		super(cmsMagnoliaConfiguration, magnoliaWebClient);
	}

	@Override
	public CmsAssetType getAssetType() {
		return CmsAssetType.PROJECTVALUES;
	}

}
