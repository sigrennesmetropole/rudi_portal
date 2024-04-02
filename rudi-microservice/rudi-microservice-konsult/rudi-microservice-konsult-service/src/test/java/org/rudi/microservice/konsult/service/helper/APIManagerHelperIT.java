package org.rudi.microservice.konsult.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.facet.acl.bean.ClientKey;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.apimaccess.bean.APISearchCriteria;
import org.rudi.facet.apimaccess.bean.HasSubscriptionStatus;
import org.rudi.facet.apimaccess.constant.APISearchPropertyKey;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.apimaccess.exception.APINotFoundException;
import org.rudi.facet.apimaccess.exception.APINotUniqueException;
import org.rudi.facet.apimaccess.exception.APIsOperationWithIdException;
import org.rudi.facet.apimaccess.exception.MissingAPIPropertiesException;
import org.rudi.facet.apimaccess.exception.MissingAPIPropertyException;
import org.rudi.facet.apimaccess.helper.api.AdditionalPropertiesHelper;
import org.rudi.facet.apimaccess.service.APIsService;
import org.rudi.facet.apimaccess.service.ApplicationService;
import org.rudi.facet.kaccess.bean.Media;
import org.rudi.facet.kaccess.bean.MediaFile;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.helper.dataset.metadatadetails.MetadataDetailsHelper;
import org.rudi.facet.organization.helper.OrganizationHelper;
import org.rudi.facet.projekt.helper.ProjektHelper;
import org.rudi.microservice.konsult.service.exception.AccessDeniedMetadataMediaException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.wso2.carbon.apimgt.rest.api.publisher.API;
import org.wso2.carbon.apimgt.rest.api.publisher.APIInfo;
import org.wso2.carbon.apimgt.rest.api.publisher.APIList;

import lombok.val;

@ExtendWith(MockitoExtension.class)
class APIManagerHelperIT {

	private static String anonymousUsername;
	private AdditionalPropertiesHelper additionalPropertiesHelper;
	private APIManagerHelper apiManagerHelper;
	@Mock
	private APIsService apIsService;
	@Mock
	private ApplicationService applicationService;
	@Mock
	private MetadataDetailsHelper metadataDetailsHelper;
	@Mock
	private ACLHelper aclHelper;
	@Mock
	private OrganizationHelper organizationHelper;
	@Mock
	private ProjektHelper projektHelper;

	@BeforeAll
	public static void beforeAll() {
		anonymousUsername = "anonymous";
	}

	@BeforeEach
	void beforeEachTest() {
		apiManagerHelper = new APIManagerHelper(apIsService, applicationService, metadataDetailsHelper,
				anonymousUsername, aclHelper, organizationHelper, projektHelper, additionalPropertiesHelper);
	}

	private User getUserWithUsername(String username) {
		final User authenticatedUser = new User();
		authenticatedUser.setType(org.rudi.facet.acl.bean.UserType.PERSON);
		UUID userUuid = UUID.randomUUID();
		authenticatedUser.setLogin(username);
		authenticatedUser.setUuid(userUuid);
		return authenticatedUser;
	}

	@Test
	@DisplayName("Test pour savoir si l'utilisateur a souscrit à un jdd alors que l'api n'existe pas")
	void TestUserHasSubscribeToMetadataMedia() throws APIManagerException {
		val authenticatedUser = getUserWithUsername("username");
		final ClientKey clientKey = new ClientKey();

		when(aclHelper.getUserByUUID(any())).thenReturn(authenticatedUser);
		when(apIsService.searchAPI(any())).thenReturn(new APIList().count(0)._list(Collections.emptyList()));

		UUID globalId = UUID.randomUUID();
		UUID mediaId = UUID.randomUUID();
		UUID ownerUuid = UUID.randomUUID();
		assertThatThrownBy(() -> apiManagerHelper.userHasSubscribeToMetadataMedia(globalId, mediaId, ownerUuid))
				.isInstanceOf(AppServiceException.class)
				.hasMessage("Erreur lors de la récupération de la souscription à l'api globalId = %s et mediaId = %s",
						globalId, mediaId)
				.hasCauseInstanceOf(APINotFoundException.class);
	}

	@Test
	@DisplayName("Test pour savoir si l'utilisateur a souscrit à un media d'un jdd, "
			+ "lorsque son application wso n'a pas souscrit à l'api correspondante dans WSO2")
	void testHasSubscribeToJddMediaWithNoSubscriptionToAPI() throws APIManagerException, AppServiceException {
		val authenticatedUser = getUserWithUsername("username");
		final ClientKey clientKey = new ClientKey();

		UUID globalId = UUID.randomUUID();
		UUID mediaId = UUID.randomUUID();
		UUID ownerUuid = UUID.randomUUID();
		APISearchCriteria apiSearchCriteria = new APISearchCriteria().globalId(globalId).mediaUuid(mediaId);
		APIInfo apiInfo = new APIInfo().id(UUID.randomUUID().toString());

		when(aclHelper.getUserByUUID(any())).thenReturn(authenticatedUser);
		when(apIsService.searchAPI(apiSearchCriteria)).thenReturn(new APIList().count(1)._list(List.of(apiInfo)));
		when(applicationService.hasSubscribeAPIToDefaultUserApplication(apiInfo.getId(), authenticatedUser.getLogin()))
				.thenReturn(HasSubscriptionStatus.NOT_SUBSCRIBED);

		assertThat(apiManagerHelper.userHasSubscribeToMetadataMedia(globalId, mediaId, ownerUuid)
				.equals(HasSubscriptionStatus.NOT_SUBSCRIBED)).isTrue();
	}

	@Test
	@DisplayName("Test pour savoir si l'utilisateur a souscrit à un media d'un jdd, "
			+ "lorsque son application wso a souscrit à l'api correspondante dans WSO2")
	void testHasSubscribeToJddMediaWithSubscriptionToAPI() throws APIManagerException, AppServiceException {
		val authenticatedUser = getUserWithUsername("username");
		final ClientKey clientKey = new ClientKey();

		UUID globalId = UUID.randomUUID();
		UUID mediaId = UUID.randomUUID();
		APISearchCriteria apiSearchCriteria = new APISearchCriteria().globalId(globalId).mediaUuid(mediaId);
		APIInfo apiInfo = new APIInfo().id(UUID.randomUUID().toString());

		when(aclHelper.getUserByUUID(any())).thenReturn(authenticatedUser);
		when(apIsService.searchAPI(apiSearchCriteria)).thenReturn(new APIList().count(1)._list(List.of(apiInfo)));
		when(applicationService.hasSubscribeAPIToDefaultUserApplication(apiInfo.getId(), authenticatedUser.getLogin()))
				.thenReturn(HasSubscriptionStatus.SUBSCRIBED);

		assertThat(apiManagerHelper.userHasSubscribeToMetadataMedia(globalId, mediaId, authenticatedUser.getUuid())
				.equals(HasSubscriptionStatus.NOT_SUBSCRIBED)).isFalse();
	}

	@Test
	@DisplayName("Test de récupération du username qui peut télécharger un jdd ouvert auquel le user connu a souscrit")
	void getLoginAbleToDownloadNotRestrictedMetadataMediaWhenKnownUsernameHasSubscribed()
			throws APIManagerException, AppServiceException {
		val authenticatedUser = getUserWithUsername("username");
		final ClientKey clientKey = new ClientKey();

		Metadata metadata = new Metadata().globalId(UUID.randomUUID());
		Media media = new MediaFile().mediaId(UUID.randomUUID());

		APISearchCriteria apiSearchCriteria = new APISearchCriteria().globalId(metadata.getGlobalId())
				.mediaUuid(media.getMediaId());
		APIInfo apiInfo = new APIInfo().id(UUID.randomUUID().toString());

		when(aclHelper.getAuthenticatedUser()).thenReturn(authenticatedUser);
		when(aclHelper.getUserByUUID(any())).thenReturn(authenticatedUser);
		when(apIsService.searchAPI(apiSearchCriteria)).thenReturn(new APIList().count(1)._list(List.of(apiInfo)));
		when(applicationService.hasSubscribeAPIToDefaultUserApplication(apiInfo.getId(), authenticatedUser.getLogin()))
				.thenReturn(HasSubscriptionStatus.SUBSCRIBED);
		assertThat(apiManagerHelper.getLoginAbleToDownloadMedia(metadata, media))
				.isEqualTo(authenticatedUser.getLogin());
	}

	@Test
	@DisplayName("Test de récupération du username qui peut télécharger un jdd ouvert auquel le user connu n'a pas souscrit")
	void getLoginAbleToDownloadNotRestrictedMetadataMediaWhenKnownUsernameHasNotSubscribed()
			throws APIManagerException, AppServiceException {
		val authenticatedUser = getUserWithUsername("username");
		final ClientKey clientKey = new ClientKey();

		Metadata metadata = new Metadata().globalId(UUID.randomUUID());
		Media media = new MediaFile().mediaId(UUID.randomUUID());

		APISearchCriteria apiSearchCriteria = new APISearchCriteria().globalId(metadata.getGlobalId())
				.mediaUuid(media.getMediaId());
		APIInfo apiInfo = new APIInfo().id(UUID.randomUUID().toString());

		when(aclHelper.getUserByUUID(any())).thenReturn(authenticatedUser);
		when(aclHelper.getAuthenticatedUser()).thenReturn(authenticatedUser);
		when(apIsService.searchAPI(apiSearchCriteria)).thenReturn(new APIList().count(1)._list(List.of(apiInfo)));
		when(applicationService.hasSubscribeAPIToDefaultUserApplication(apiInfo.getId(), authenticatedUser.getLogin()))
				.thenReturn(HasSubscriptionStatus.NOT_SUBSCRIBED);
		when(metadataDetailsHelper.isRestricted(metadata)).thenReturn(false);

		assertThat(apiManagerHelper.getLoginAbleToDownloadMedia(metadata, media)).isEqualTo(anonymousUsername);
	}

	@Test
	@DisplayName("Test de récupération du username qui peut télécharger un jdd restreint auquel le user connu a souscrit")
	void getLoginAbleToDownloadRestrictedMetadataMediaWhenKnownUsernameHasSubscribed()
			throws APIManagerException, AppServiceException {
		val authenticatedUser = getUserWithUsername("username");
		final ClientKey clientKey = new ClientKey();

		Metadata metadata = new Metadata().globalId(UUID.randomUUID());
		Media media = new MediaFile().mediaId(UUID.randomUUID());

		APISearchCriteria apiSearchCriteria = new APISearchCriteria().globalId(metadata.getGlobalId())
				.mediaUuid(media.getMediaId());
		APIInfo apiInfo = new APIInfo().id(UUID.randomUUID().toString());

		when(aclHelper.getAuthenticatedUser()).thenReturn(authenticatedUser);
		when(aclHelper.getUserByUUID(any())).thenReturn(authenticatedUser);
		when(apIsService.searchAPI(apiSearchCriteria)).thenReturn(new APIList().count(1)._list(List.of(apiInfo)));
		when(applicationService.hasSubscribeAPIToDefaultUserApplication(apiInfo.getId(), authenticatedUser.getLogin()))
				.thenReturn(HasSubscriptionStatus.SUBSCRIBED);

		assertThat(apiManagerHelper.getLoginAbleToDownloadMedia(metadata, media))
				.isEqualTo(authenticatedUser.getLogin());
	}

	@Test
	@DisplayName("Test de récupération du username qui peut télécharger un jdd restreint auquel le user connu n'a pas souscrit")
	void getLoginAbleToDownloadRestrictedMetadataMediaWhenKnownUsernameHasNotSubscribed()
			throws APIManagerException, AppServiceException {
		val authenticatedUser = getUserWithUsername("mpokora");
		final ClientKey clientKey = new ClientKey();

		Metadata metadata = new Metadata().globalId(UUID.fromString("92569f8a-2885-44d0-9fd6-f97d05f05b80"));
		Media media = new MediaFile().mediaId(UUID.fromString("51ce9dfd-3d84-48d8-848e-6094b9de1e5b"));

		APISearchCriteria apiSearchCriteria = new APISearchCriteria().globalId(metadata.getGlobalId())
				.mediaUuid(media.getMediaId());
		APIInfo apiInfo = new APIInfo().id(UUID.randomUUID().toString());

		when(aclHelper.getUserByUUID(any())).thenReturn(authenticatedUser);
		when(aclHelper.getAuthenticatedUser()).thenReturn(authenticatedUser);
		when(apIsService.searchAPI(apiSearchCriteria)).thenReturn(new APIList().count(1)._list(List.of(apiInfo)));
		when(applicationService.hasSubscribeAPIToDefaultUserApplication(apiInfo.getId(), authenticatedUser.getLogin()))
				.thenReturn(HasSubscriptionStatus.NOT_SUBSCRIBED);
		when(metadataDetailsHelper.isRestricted(metadata)).thenReturn(true);

		assertThatThrownBy(() -> apiManagerHelper.getLoginAbleToDownloadMedia(metadata, media))
				.isInstanceOf(AccessDeniedMetadataMediaException.class).hasMessage(
						"L'utilisateur connecté ne peut pas accéder au média media_id = 51ce9dfd-3d84-48d8-848e-6094b9de1e5b du jeu de données global_id = 92569f8a-2885-44d0-9fd6-f97d05f05b80");
	}

	@Test
	void getGlobalIdFromMediaId_apiInfoNotFound() throws APIManagerException {
		final UUID mediaId = UUID.fromString("ac27b14c-4b9e-4ee1-9436-0d603dd05137");

		final APIList apiList = new APIList();
		when(apIsService.searchAPI(any())).thenReturn(apiList);

		assertThatThrownBy(() -> apiManagerHelper.getGlobalIdFromMediaId(mediaId))
				.as("Une exception est lancée si on ne retrouve pas les infos sur l'API")
				.isInstanceOf(APINotFoundException.class)
				.hasMessage("Aucune API ne correspond à l'information mediaId = ac27b14c-4b9e-4ee1-9436-0d603dd05137");
	}

	@Test
	void getGlobalIdFromMediaId_apiNotFound() throws APIManagerException {
		final UUID mediaId = UUID.fromString("ac27b14c-4b9e-4ee1-9436-0d603dd05137");

		final var apiId = "05b0b019-ae02-4d09-917e-29f2dc91a6c9";
		final APIInfo apiInfo = new APIInfo().id(apiId);
		final APIList apiList = new APIList().count(1)._list(Collections.singletonList(apiInfo));
		when(apIsService.searchAPI(any())).thenReturn(apiList);

		final var httpNotFoundException = new ResponseStatusException(HttpStatus.NOT_FOUND);
		when(apIsService.getAPI(apiId)).thenThrow(new APIsOperationWithIdException(apiId, httpNotFoundException));

		assertThatThrownBy(() -> apiManagerHelper.getGlobalIdFromMediaId(mediaId))
				.as("Une exception est lancée si on ne retrouve pas les détails de l'API")
				.isInstanceOf(APIsOperationWithIdException.class)
				.hasMessage("API operation failed for apiId = 05b0b019-ae02-4d09-917e-29f2dc91a6c9")
				.hasCauseInstanceOf(ResponseStatusException.class);
	}

	@Test
	void getGlobalIdFromMediaId_apiNotUnique() throws APIManagerException {
		final UUID mediaId = UUID.fromString("ac27b14c-4b9e-4ee1-9436-0d603dd05137");

		final APIInfo apiInfo1 = new APIInfo().id("05b0b019-ae02-4d09-917e-29f2dc91a6c9");
		final APIInfo apiInfo2 = new APIInfo().id("eef6832f-6a06-4f65-8f95-a533ac8926a7");
		final APIList apiList = new APIList().count(2)._list(Arrays.asList(apiInfo1, apiInfo2));
		when(apIsService.searchAPI(any())).thenReturn(apiList);

		assertThatThrownBy(() -> apiManagerHelper.getGlobalIdFromMediaId(mediaId))
				.as("Une exception est lancée si on ne retrouve pas les détails de l'API")
				.isInstanceOf(APINotUniqueException.class).hasMessage(
						"Il y a 2 API qui correspondent aux informations globalId = null et mediaId = ac27b14c-4b9e-4ee1-9436-0d603dd05137. Veuillez vérifier la cohérence des API créées dans l'API Manager.");
	}

	@Test
	void getGlobalIdFromMediaId_apiWithoutProperties() throws APIManagerException {
		final UUID mediaId = UUID.fromString("ac27b14c-4b9e-4ee1-9436-0d603dd05137");

		final var apiId = "05b0b019-ae02-4d09-917e-29f2dc91a6c9";
		final APIInfo apiInfo = new APIInfo().id(apiId);
		final APIList apiList = new APIList().count(1)._list(Collections.singletonList(apiInfo));
		when(apIsService.searchAPI(any())).thenReturn(apiList);

		final API api = new API().id(apiId);
		when(apIsService.getAPI(apiId)).thenReturn(api);

		assertThatThrownBy(() -> apiManagerHelper.getGlobalIdFromMediaId(mediaId))
				.as("Une exception est lancée si on ne retrouve pas les propriétés de l'API")
				.isInstanceOf(MissingAPIPropertiesException.class)
				.hasMessage("L'API 05b0b019-ae02-4d09-917e-29f2dc91a6c9 ne possède aucune propriétés");
	}

	@Test
	void getGlobalIdFromMediaId_apiWithoutGlobalIdProperty() throws APIManagerException {
		final UUID mediaId = UUID.fromString("ac27b14c-4b9e-4ee1-9436-0d603dd05137");

		final var apiId = "05b0b019-ae02-4d09-917e-29f2dc91a6c9";
		final APIInfo apiInfo = new APIInfo().id(apiId);
		final APIList apiList = new APIList().count(1)._list(Collections.singletonList(apiInfo));
		when(apIsService.searchAPI(any())).thenReturn(apiList);

		final Map<String, String> apiProperties = new HashMap<>();
		apiProperties.put("test", "test");
		final API api = new API().id(apiId)
				.additionalProperties(additionalPropertiesHelper.getAdditionalPropertiesMapAsList(apiProperties));
		when(apIsService.getAPI(apiId)).thenReturn(api);

		assertThatThrownBy(() -> apiManagerHelper.getGlobalIdFromMediaId(mediaId))
				.as("Une exception est lancée si on ne retrouve pas les propriétés de l'API")
				.isInstanceOf(MissingAPIPropertyException.class)
				.hasMessage("L'API 05b0b019-ae02-4d09-917e-29f2dc91a6c9 ne possède pas la propriété global_id");
	}

	@Test
	void getGlobalIdFromMediaId_apiWithGlobalIdProperty() throws APIManagerException {
		final UUID globalId = UUID.fromString("76b52ccb-6297-42a6-8c08-8305d6bc6dee");
		final UUID mediaId = UUID.fromString("ac27b14c-4b9e-4ee1-9436-0d603dd05137");

		final var apiId = "05b0b019-ae02-4d09-917e-29f2dc91a6c9";
		final APIInfo apiInfo = new APIInfo().id(apiId);
		final APIList apiList = new APIList().count(1)._list(Collections.singletonList(apiInfo));
		when(apIsService.searchAPI(any())).thenReturn(apiList);

		final Map<String, String> apiProperties = new HashMap<>();
		apiProperties.put(APISearchPropertyKey.GLOBAL_ID, globalId.toString());
		final API api = new API().id(apiId)
				.additionalProperties(additionalPropertiesHelper.getAdditionalPropertiesMapAsList(apiProperties));
		when(apIsService.getAPI(apiId)).thenReturn(api);

		assertThat(apiManagerHelper.getGlobalIdFromMediaId(mediaId))
				.as("On retrouve le global_id dans les propriétés de l'API").isEqualTo(globalId);
	}

}
