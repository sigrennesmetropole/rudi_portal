package org.rudi.microservice.apigateway.facade.controller;

import java.util.UUID;

import org.rudi.common.facade.util.UtilPageable;
import org.rudi.microservice.apigateway.core.bean.Api;
import org.rudi.microservice.apigateway.core.bean.ApiSearchCriteria;
import org.rudi.microservice.apigateway.core.bean.Confidentiality;
import org.rudi.microservice.apigateway.core.bean.PagedApiList;
import org.rudi.microservice.apigateway.facade.controller.api.ApisApi;
import org.rudi.microservice.apigateway.service.api.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import static org.rudi.common.core.security.QuotedRoleCodes.MODULE_APIGATEWAY_ADMINISTRATOR;
import static org.rudi.common.core.security.QuotedRoleCodes.MODULE_KALIM_ADMINISTRATOR;

@RestController
public class ApisController implements ApisApi {

	@Autowired
	private ApiService apiService;

	@Autowired
	private UtilPageable utilPageable;

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	@Override
	@PreAuthorize("hasAnyRole(" + MODULE_APIGATEWAY_ADMINISTRATOR + "," + MODULE_KALIM_ADMINISTRATOR + ")")
	public ResponseEntity<Api> createApi(Api api) throws Exception {
		Api result = apiService.createApi(api);
		applicationEventPublisher.publishEvent(new RefreshRoutesEvent(result));
		return ResponseEntity.ok(result);
	}

	@Override
	@PreAuthorize("hasAnyRole(" + MODULE_APIGATEWAY_ADMINISTRATOR + "," + MODULE_KALIM_ADMINISTRATOR + ")")
	public ResponseEntity<Api> updateApi(Api api) throws Exception {
		Api result = apiService.updateApi(api);
		applicationEventPublisher.publishEvent(new RefreshRoutesEvent(api));
		return ResponseEntity.ok(result);
	}

	@Override
	@PreAuthorize("hasAnyRole(" + MODULE_APIGATEWAY_ADMINISTRATOR + "," + MODULE_KALIM_ADMINISTRATOR + ")")
	public ResponseEntity<Void> deleteApi(UUID apiUuid) throws Exception {
		apiService.deleteApi(apiUuid);
		applicationEventPublisher.publishEvent(new RefreshRoutesEvent(apiUuid));
		return ResponseEntity.noContent().build();
	}

	@Override
	@PreAuthorize("hasAnyRole(" + MODULE_APIGATEWAY_ADMINISTRATOR + "," + MODULE_KALIM_ADMINISTRATOR + ")")
	public ResponseEntity<PagedApiList> searchApis(UUID apiId, UUID globalId, UUID providerId, UUID nodeProviderId,
			UUID producerId, UUID mediaId, String contract, String url, Confidentiality confidentiality, Integer limit,
			Integer offset, String order) throws Exception {
		PagedApiList result = new PagedApiList();
		ApiSearchCriteria searchCriteria = new ApiSearchCriteria().producerId(producerId).providerId(providerId)
				.nodeProviderId(nodeProviderId).globalId(globalId).mediaId(mediaId).url(url)
				.confidentiality(confidentiality);

		Page<Api> apis = apiService.searchApis(searchCriteria, utilPageable.getPageable(offset, limit, order));
		result.setElements(apis.getContent());
		result.setTotal(apis.getTotalElements());
		return ResponseEntity.ok(result);
	}

	/**
	 * GET /apis/{api-id} : Get an API
	 * Get an API
	 *
	 * @param apiId (required)
	 * @return OK (status code 200)
	 * or Internal server error (status code 500)
	 */
	@Override
	public ResponseEntity<Api> getApi(UUID apiId) throws Exception {
		return ResponseEntity.ok(apiService.getApi(apiId));
	}
}
