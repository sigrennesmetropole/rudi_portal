package org.rudi.facet.apimaccess.api.admin;

import org.rudi.facet.apimaccess.api.APIManagerProperties;
import org.rudi.facet.apimaccess.api.AbstractManagerAPI;
import org.rudi.facet.apimaccess.api.MonoUtils;
import org.rudi.facet.apimaccess.exception.APIManagerHttpExceptionFactory;
import org.rudi.facet.apimaccess.exception.AdminOperationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.wso2.carbon.apimgt.rest.api.admin.APICategory;
import org.wso2.carbon.apimgt.rest.api.admin.APICategoryList;
import org.wso2.carbon.apimgt.rest.api.admin.RoleAliasList;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Component
public class AdminOperationAPI extends AbstractManagerAPI {

	AdminOperationAPI(
			WebClient.Builder apimWebClientBuilder,
			APIManagerHttpExceptionFactory apiManagerHttpExceptionFactory,
			APIManagerProperties apiManagerProperties
	) {
		super(apimWebClientBuilder, apiManagerHttpExceptionFactory, apiManagerProperties);
	}

	public APICategoryList getApiCategories() throws AdminOperationException {
		final Mono<APICategoryList> mono = populateRequestWithAdminRegistrationId(HttpMethod.GET, buildAdminURIPath(apiManagerProperties.getApiCategoriesPath()), Collections.emptyMap())
				.contentType(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(APICategoryList.class);
		return MonoUtils.blockOrThrow(mono, e -> new AdminOperationException("Cannot get API Categories", e));
	}

	public void createApiCategory(APICategory apiCategory) throws AdminOperationException {
		final Mono<APICategory> mono = populateRequestWithAdminRegistrationId(HttpMethod.POST, buildAdminURIPath(apiManagerProperties.getApiCategoriesPath()), Collections.emptyMap())
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(apiCategory))
				.retrieve()
				.bodyToMono(APICategory.class);
		MonoUtils.blockOrThrow(mono, e -> new AdminOperationException("Cannot create API Category", e));
	}

	/**
	 * @see <a href="https://apim.docs.wso2.com/en/latest/reference/product-apis/admin-apis/admin-v2/admin-v2/#tag/System-Scopes/paths/~1system-scopes~1role-aliases/get">Documentation WSO2</a>
	 */
	public RoleAliasList getSystemScopesRoleAliases() throws AdminOperationException {
		final Mono<RoleAliasList> mono = populateRequestWithAdminRegistrationId(HttpMethod.GET, buildAdminURIPath(apiManagerProperties.getSystemScopesRoleAliasesPath()), Collections.emptyMap())
				.contentType(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(RoleAliasList.class);
		return MonoUtils.blockOrThrow(mono, e -> new AdminOperationException("Cannot get system scopes role aliases", e));
	}

	/**
	 * @see <a href="https://apim.docs.wso2.com/en/latest/reference/product-apis/admin-apis/admin-v2/admin-v2/#tag/System-Scopes/paths/~1system-scopes~1role-aliases/put">Documentation WSO2</a>
	 */
	public void updateSystemScopesRoleAliases(RoleAliasList roleAliasList) throws AdminOperationException {
		final Mono<RoleAliasList> mono = populateRequestWithAdminRegistrationId(HttpMethod.PUT, buildAdminURIPath(apiManagerProperties.getSystemScopesRoleAliasesPath()), Collections.emptyMap())
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(roleAliasList))
				.retrieve()
				.bodyToMono(RoleAliasList.class);
		MonoUtils.blockOrThrow(mono, e -> new AdminOperationException("Cannot update system scopes role aliases", e));
	}

}
