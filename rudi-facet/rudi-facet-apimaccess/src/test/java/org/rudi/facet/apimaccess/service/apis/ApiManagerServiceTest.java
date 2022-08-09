package org.rudi.facet.apimaccess.service.apis;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.rudi.facet.apimaccess.bean.APIDescription;
import org.rudi.facet.apimaccess.bean.APILifecycleStatusAction;
import org.rudi.facet.apimaccess.bean.APISearchCriteria;
import org.rudi.facet.apimaccess.bean.APIWorkflowResponse;
import org.rudi.facet.apimaccess.bean.Application;
import org.rudi.facet.apimaccess.bean.ApplicationSearchCriteria;
import org.rudi.facet.apimaccess.bean.Applications;
import org.rudi.facet.apimaccess.bean.DevPortalSubscriptionSearchCriteria;
import org.rudi.facet.apimaccess.bean.InterfaceContract;
import org.rudi.facet.apimaccess.bean.LimitingPolicies;
import org.rudi.facet.apimaccess.bean.LimitingPolicy;
import org.rudi.facet.apimaccess.bean.SearchCriteria;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.apimaccess.exception.APIManagerHttpException;
import org.rudi.facet.apimaccess.service.APIsService;
import org.rudi.facet.apimaccess.service.ApimaccessSpringBootTest;
import org.rudi.facet.apimaccess.service.ApplicationService;
import org.rudi.facet.apimaccess.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.wso2.carbon.apimgt.rest.api.devportal.Subscription;
import org.wso2.carbon.apimgt.rest.api.devportal.SubscriptionList;
import org.wso2.carbon.apimgt.rest.api.publisher.API;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ApimaccessSpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ApiManagerServiceTest {

	@Autowired
	private PolicyService policyService;

	@Autowired
	private APIsService apIsService;

	@Autowired
	private ApplicationService applicationService;

	@Value("${apimanager.oauth2.client.admin.registration.id}")
	private String adminRegistrationId;

	private static API api = new API();
	private static Application application = new Application();

	@Test
	@Order(1)
	void testScenario() throws APIManagerException {
		// récupération des subscription policies
		LimitingPolicies subscriptionLimitingPolicies = policyService
				.searchSubscriptionLimitingPolicies(new SearchCriteria().limit(10).offset(0));
		Assertions.assertNotNull(subscriptionLimitingPolicies);
		Assertions.assertTrue(subscriptionLimitingPolicies.getCount() > 0);

		// récupération d'un subscription policy à partir de son nom
		LimitingPolicy subscriptionLimitingPolicy = policyService
				.getSubscriptionLimitingPolicy("Gold");
		Assertions.assertNotNull(subscriptionLimitingPolicy);

		// création d'un api
		APIDescription apiDescription = new APIDescription()
				.globalId(UUID.randomUUID())
				.mediaType("text/csv")
				.endpointUrl("https://demo.dataverse.org/api/access/datafile/1821528")
				.name("api-test-" + System.currentTimeMillis())
				.version("1.0.0")
				.interfaceContract(InterfaceContract.DOWNLOAD.getValue())
				.providerUuid(UUID.randomUUID())
				.providerCode(RandomStringUtils.randomAlphabetic(10))
				.mediaUuid(UUID.randomUUID());
		api = apIsService.createOrUnarchiveAPI(apiDescription);
		Assertions.assertNotNull(api);

		// publication de l'api
		APIWorkflowResponse apiWorkflowResponse = apIsService.updateAPILifecycleStatus(api.getId(), APILifecycleStatusAction.PUBLISH);
		Assertions.assertEquals(APIWorkflowResponse.WorkflowStatusEnum.APPROVED, apiWorkflowResponse.getWorkflowStatus());

		// récupération de l'api
		API apiGet = apIsService.getAPI(api.getId());
		Assertions.assertNotNull(apiGet);

		// recherche de l'api
		APISearchCriteria apiSearchCriteria = new APISearchCriteria()
				.limit(10)
				.offset(0)
				.providerUuid(apiDescription.getProviderUuid())
				.providerCode(apiDescription.getProviderCode())
				.globalId(apiDescription.getGlobalId());
		final var apiList = apIsService.searchAPI(apiSearchCriteria);
		Assertions.assertEquals(1, apiList.getCount());

		// récupération des subscription policies d'une api
		List<LimitingPolicy> throttlePolicies = apIsService
				.getAPISubscriptionPolicies(apiGet.getId());
		Assertions.assertNotNull(throttlePolicies);
		Assertions.assertTrue(throttlePolicies.size() > 0);

		// récupération des application policies
		LimitingPolicies applicationLimitingPolicies = policyService
				.searchApplicationLimitingPolicies(new SearchCriteria().limit(10).offset(0), adminRegistrationId);
		Assertions.assertTrue(applicationLimitingPolicies.getCount() > 0);

		// récupération d'une application policy à partir de son nom
		LimitingPolicy applicationPolicy = policyService
				.getApplicationLimitingPolicy(applicationLimitingPolicies.getList().get(0).getName(), adminRegistrationId);
		Assertions.assertNotNull(subscriptionLimitingPolicy);

		// Création d'une application
		application = applicationService.createApplication(new Application()
				.name(RandomStringUtils.randomAlphabetic(10))
				.description("application de test")
				.throttlingPolicy(applicationPolicy.getName()), adminRegistrationId);

		// récupération d'une application
		Application applicationGet = applicationService.getApplication(application.getApplicationId(), adminRegistrationId);
		Assertions.assertNotNull(applicationGet);

		// recherche de l'application
		Applications applications = applicationService.searchApplication(new ApplicationSearchCriteria().limit(10).offset(0).name(application.getName()), adminRegistrationId);
		Assertions.assertTrue(applications.getCount() > 0);

		// création d'une souscription
		Subscription applicationAPISubscription = applicationService.subscribeAPI(new Subscription()
				.apiId(api.getId())
				.applicationId(application.getApplicationId())
				.throttlingPolicy(subscriptionLimitingPolicy.getName()), adminRegistrationId);

		// Recherche des souscriptions pour l'application
		SubscriptionList applicationAPISubscriptions = applicationService.searchApplicationAPISubscriptions(new DevPortalSubscriptionSearchCriteria()
				.limit(10).offset(0).applicationId(application.getApplicationId()), adminRegistrationId);
		Assertions.assertEquals(1, applicationAPISubscriptions.getCount());
		Assertions.assertEquals(api.getId(), applicationAPISubscriptions.getList().get(0).getApiId());
		Assertions.assertTrue(applicationService.hasSubscribeAPI(api.getId(), application.getApplicationId(), adminRegistrationId));

		// supprimer la souscription et test pour s'assurer qu'elle n'existe plus
		applicationService.unsubscribeAPI(applicationAPISubscription.getSubscriptionId(), adminRegistrationId);
		SubscriptionList applicationAPISubscriptionsAfterRemove = applicationService.searchApplicationAPISubscriptions(new DevPortalSubscriptionSearchCriteria()
				.limit(10).offset(0).applicationId(application.getApplicationId()), adminRegistrationId);
		Assertions.assertEquals(0, applicationAPISubscriptionsAfterRemove.getCount());
	}

	@Test
	@Order(2)
	void testClean() throws APIManagerException {
		if (!StringUtils.isEmpty(application.getApplicationId())) {
			applicationService.deleteApplication(application.getApplicationId(), adminRegistrationId);

			try {
				applicationService.getApplication(application.getApplicationId(), adminRegistrationId);
			} catch (Exception e) {
				assertThat(e)
						.isInstanceOf(APIManagerException.class)
						.hasCauseInstanceOf(APIManagerHttpException.class)
						.hasRootCauseInstanceOf(APIManagerHttpException.class)
				;
				final var cause = e.getCause();
				assertThat(cause)
						.hasMessageContaining("404")
						.hasMessageContainingAll("Requested application with Id", "not found")
				;
			}
		}
		if (!StringUtils.isEmpty(api.getId())) {
			apIsService.deleteAPI(api.getId());

			try {
				apIsService.getAPI(api.getId());
			} catch (Exception e) {
				assertThat(e)
						.isInstanceOf(APIManagerException.class)
						.hasCauseInstanceOf(APIManagerHttpException.class)
						.hasRootCauseInstanceOf(APIManagerHttpException.class)
				;
				final var cause = e.getCause();
				assertThat(cause)
						.hasMessageContaining("404")
						.hasMessageContainingAll("Requested API with Id", "not found")
				;
			}
		}
	}
}
