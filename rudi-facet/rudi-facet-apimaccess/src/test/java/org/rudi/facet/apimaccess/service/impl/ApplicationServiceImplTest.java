package org.rudi.facet.apimaccess.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rudi.facet.apimaccess.api.application.ApplicationOperationAPI;
import org.rudi.facet.apimaccess.api.subscription.SubscriptionOperationAPI;
import org.rudi.facet.apimaccess.bean.ApplicationAPISubscription;
import org.rudi.facet.apimaccess.bean.ApplicationInfo;
import org.rudi.facet.apimaccess.bean.ApplicationSearchCriteria;
import org.rudi.facet.apimaccess.bean.Applications;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.apimaccess.helper.search.SearchCriteriaMapper;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceImplTest {
	@InjectMocks
	private ApplicationServiceImpl applicationService;
	@Mock
	private ApplicationOperationAPI applicationOperationAPI;
	@Mock
	private SearchCriteriaMapper searchCriteriaMapper;
	@Mock
	private SubscriptionOperationAPI subscriptionOperationAPI;

	private static final String DEFAULT_SUBSCRIPTION_POLICY = "Unlimited";
	private static final String ANONYMOUS_SUBSCRIPTION_POLICY = "Bronze";
	private static final String ANONYMOUS_USERNAME = "anonymous";

	@Captor
	private ArgumentCaptor<ApplicationAPISubscription> applicationAPISubscriptionCaptor;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(applicationService, "defaultSubscriptionPolicy", DEFAULT_SUBSCRIPTION_POLICY);
		ReflectionTestUtils.setField(applicationService, "anonymousSubscriptionPolicy", ANONYMOUS_SUBSCRIPTION_POLICY);
		ReflectionTestUtils.setField(applicationService, "anonymousUsername", ANONYMOUS_USERNAME);
	}

	@DisplayName("RUDI-413")
	@ParameterizedTest(name = "user = {0} => throttlingPolicy = {1}")
	@CsvSource({
			"buddy.love," + DEFAULT_SUBSCRIPTION_POLICY,
			ANONYMOUS_USERNAME + "," + ANONYMOUS_SUBSCRIPTION_POLICY
	})
	void subscribeAPIToDefaultUserApplication(final String username, final String expectedThrottlingPolicy) throws APIManagerException {
		final String apiId = "API_ID";

		final ApplicationSearchCriteria applicationSearchCriteria = mock(ApplicationSearchCriteria.class);
		final ApplicationInfo applicationInfo = new ApplicationInfo()
				.applicationId("application_id");
		final Applications applications = new Applications().addListItem(applicationInfo).count(1);

		when(searchCriteriaMapper.buildApplicationSearchCriteriaQuery(any())).thenReturn(applicationSearchCriteria);
		when(applicationOperationAPI.searchApplication(applicationSearchCriteria, username)).thenReturn(applications);

		applicationService.subscribeAPIToDefaultUserApplication(apiId, username);

		verify(subscriptionOperationAPI).createApplicationAPISubscription(applicationAPISubscriptionCaptor.capture(), eq(username));

		assertThat(applicationAPISubscriptionCaptor.getValue())
				.as("throttlingPolicy matches user")
				.hasFieldOrPropertyWithValue("throttlingPolicy", expectedThrottlingPolicy);
	}
}