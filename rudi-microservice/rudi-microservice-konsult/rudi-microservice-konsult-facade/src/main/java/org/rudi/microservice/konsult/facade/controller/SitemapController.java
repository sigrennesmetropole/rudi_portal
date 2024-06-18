package org.rudi.microservice.konsult.facade.controller;

import static org.rudi.common.core.security.QuotedRoleCodes.ADMINISTRATOR;
import static org.rudi.common.core.security.QuotedRoleCodes.MODULE_KONSULT_ADMINISTRATOR;

import org.rudi.common.core.DocumentContent;
import org.rudi.common.facade.helper.ControllerHelper;
import org.rudi.microservice.konsult.facade.controller.api.SitemapApi;
import org.rudi.microservice.konsult.service.sitemap.SitemapService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SitemapController implements SitemapApi {
	private final ControllerHelper controllerHelper;
	private final SitemapService sitemapService;

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + "," + MODULE_KONSULT_ADMINISTRATOR + ")")
	public ResponseEntity<Void> generateSitemap() throws Exception {
		sitemapService.generateSitemap();
		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<Resource> getRessourceByName(String resourceName) throws Exception {
		sitemapService.initService();
		DocumentContent documentContent = sitemapService.getRessourceByName(resourceName);
		if (documentContent != null) {
			return controllerHelper.downloadableResponseEntity(documentContent);
		}
		return ResponseEntity.notFound().build();
	}
}
