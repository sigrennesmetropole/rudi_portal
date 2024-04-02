/**
 * RUDI Portail
 */
package org.rudi.microservice.konsult.facade.controller.cms;

import java.util.List;

import org.apache.commons.lang3.LocaleUtils;
import org.rudi.facet.cms.CmsService;
import org.rudi.facet.cms.bean.CmsAsset;
import org.rudi.facet.cms.bean.CmsAssetType;
import org.rudi.facet.cms.bean.CmsCategory;
import org.rudi.facet.cms.impl.model.CmsRequest;
import org.rudi.microservice.konsult.facade.controller.api.CmsApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

/**
 * @author FNI18300
 *
 */
@RestController
@RequiredArgsConstructor
public class CmsController implements CmsApi {

	private final CmsService cmsService;

	@Override
	public ResponseEntity<List<CmsCategory>> getCategories() throws Exception {
		return ResponseEntity.ok(cmsService.getCategories());
	}

	@Override
	public ResponseEntity<CmsAsset> renderAsset(CmsAssetType assetType, String assetId, String assetTemplate,
			String locale) throws Exception {
		return ResponseEntity
				.ok(cmsService.renderAsset(assetType, assetId, assetTemplate, LocaleUtils.toLocale(locale)));
	}

	@Override
	public ResponseEntity<List<CmsAsset>> renderAssets(CmsAssetType assetType, String assetTemplate,
			List<String> categories, List<String> filters, String locale, Integer offset, Integer limit, String order)
			throws Exception {
		CmsRequest request = CmsRequest.builder().categories(categories).filters(filters)
				.locale(LocaleUtils.toLocale(locale)).build();
		return ResponseEntity.ok(cmsService.renderAssets(assetType, assetTemplate, request, offset, limit, order));
	}

}
