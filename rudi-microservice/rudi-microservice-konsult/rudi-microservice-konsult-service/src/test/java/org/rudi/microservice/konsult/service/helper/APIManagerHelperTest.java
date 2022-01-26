package org.rudi.microservice.konsult.service.helper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.core.security.UserType;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.acl.bean.ClientKey;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.apimaccess.bean.APIInfo;
import org.rudi.facet.apimaccess.bean.APIList;
import org.rudi.facet.apimaccess.bean.APISearchCriteria;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.apimaccess.helper.rest.CustomClientRegistrationRepository;
import org.rudi.facet.apimaccess.service.APIsService;
import org.rudi.facet.apimaccess.service.ApplicationService;
import org.rudi.facet.kaccess.bean.Media;
import org.rudi.facet.kaccess.bean.MediaFile;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.helper.dataset.metadatadetails.MetadataDetailsHelper;
import org.rudi.microservice.konsult.service.exception.AccessDeniedMetadataMedia;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class APIManagerHelperTest {

	private static String anonymousUsername;
	private APIManagerHelper apiManagerHelper;
	@Mock
	private APIsService apIsService;
	@Mock
	private ApplicationService applicationService;
	@Mock
	private MetadataDetailsHelper metadataDetailsHelper;
	@Mock
	private CustomClientRegistrationRepository customClientRegistrationRepository;
	@Mock
	private UtilContextHelper utilContextHelper;
	@Mock
	private ACLHelper aclHelper;

	@BeforeAll
	public static void beforeAll() {
		anonymousUsername = "anonymous";
	}

	@BeforeEach
	public void beforeEachTest() {
		apiManagerHelper = new APIManagerHelper(apIsService, applicationService, metadataDetailsHelper, anonymousUsername, customClientRegistrationRepository, utilContextHelper, aclHelper);
	}

	@Test
	@DisplayName("Test pour savoir si l'utilisateur a souscrit à un jdd alors que l'api n'existe pas")
	public void TestUserHasSubscribeToMetadataMedia() throws APIManagerException {
		final AuthenticatedUser authenticatedUser = new AuthenticatedUser("username", UserType.PERSON);
		final ClientKey clientKey = new ClientKey();

		when(utilContextHelper.getAuthenticatedUser()).thenReturn(authenticatedUser);
		when(aclHelper.getClientKeyByLogin(authenticatedUser.getLogin())).thenReturn(clientKey);
		when(apIsService.searchAPI(any())).thenReturn(new APIList().count(0).list(Collections.emptyList()));

		UUID globalId = UUID.randomUUID();
		UUID mediaId = UUID.randomUUID();
		assertThatThrownBy(() -> apiManagerHelper.userHasSubscribeToMetadataMedia(globalId, mediaId))
				.isInstanceOf(AppServiceException.class)
				.hasMessage("Aucune API retrouvé pour le global_id = %s et media_id = %s", globalId, mediaId);
	}

	@Test
	@DisplayName("Test pour savoir si l'utilisateur a souscrit à un media d'un jdd, " +
			"lorsque son application wso n'a pas souscrit à l'api correspondante dans WSO2")
	public void testHasSubscribeToJddMediaWithNoSubscriptionToAPI() throws APIManagerException, AppServiceException {
		final AuthenticatedUser authenticatedUser = new AuthenticatedUser("username", UserType.PERSON);
		final ClientKey clientKey = new ClientKey();

		UUID globalId = UUID.randomUUID();
		UUID mediaId = UUID.randomUUID();
		APISearchCriteria apiSearchCriteria = new APISearchCriteria().globalId(globalId).mediaUuid(mediaId);
		APIInfo apiInfo = new APIInfo().id(UUID.randomUUID().toString());

		when(utilContextHelper.getAuthenticatedUser()).thenReturn(authenticatedUser);
		when(aclHelper.getClientKeyByLogin(authenticatedUser.getLogin())).thenReturn(clientKey);
		when(apIsService.searchAPI(apiSearchCriteria)).thenReturn(new APIList().count(1).list(List.of(apiInfo)));
		when(applicationService.hasSubscribeAPIToDefaultUserApplication(apiInfo.getId(), authenticatedUser.getLogin())).thenReturn(false);

		assertThat(apiManagerHelper.userHasSubscribeToMetadataMedia(globalId, mediaId)).isFalse();
	}

	@Test
	@DisplayName("Test pour savoir si l'utilisateur a souscrit à un media d'un jdd, " +
			"lorsque son application wso a souscrit à l'api correspondante dans WSO2")
	public void testHasSubscribeToJddMediaWithSubscriptionToAPI() throws APIManagerException, AppServiceException {
		final AuthenticatedUser authenticatedUser = new AuthenticatedUser("username", UserType.PERSON);
		final ClientKey clientKey = new ClientKey();

		UUID globalId = UUID.randomUUID();
		UUID mediaId = UUID.randomUUID();
		APISearchCriteria apiSearchCriteria = new APISearchCriteria().globalId(globalId).mediaUuid(mediaId);
		APIInfo apiInfo = new APIInfo().id(UUID.randomUUID().toString());

		when(utilContextHelper.getAuthenticatedUser()).thenReturn(authenticatedUser);
		when(aclHelper.getClientKeyByLogin(authenticatedUser.getLogin())).thenReturn(clientKey);
		when(apIsService.searchAPI(apiSearchCriteria)).thenReturn(new APIList().count(1).list(List.of(apiInfo)));
		when(applicationService.hasSubscribeAPIToDefaultUserApplication(apiInfo.getId(), authenticatedUser.getLogin())).thenReturn(true);

		assertThat(apiManagerHelper.userHasSubscribeToMetadataMedia(globalId, mediaId)).isTrue();
	}

	@Test
	@DisplayName("Test de récupération du username qui peut télécharger un jdd ouvert auquel le user connu a souscrit")
	public void testCheckUsernameAbleToDownloadNotRestrictedMetadataMediaWhenKnownUsernameHasSubscribed()
			throws APIManagerException, AppServiceException {
		final AuthenticatedUser authenticatedUser = new AuthenticatedUser("username", UserType.PERSON);
		final ClientKey clientKey = new ClientKey();

		Metadata metadata = new Metadata().globalId(UUID.randomUUID());
		Media media = new MediaFile().mediaId(UUID.randomUUID());

		APISearchCriteria apiSearchCriteria = new APISearchCriteria().globalId(metadata.getGlobalId()).mediaUuid(media.getMediaId());
		APIInfo apiInfo = new APIInfo().id(UUID.randomUUID().toString());

		when(utilContextHelper.getAuthenticatedUser()).thenReturn(authenticatedUser);
		when(aclHelper.getClientKeyByLogin(authenticatedUser.getLogin())).thenReturn(clientKey);
		when(apIsService.searchAPI(apiSearchCriteria)).thenReturn(new APIList().count(1).list(List.of(apiInfo)));
		when(applicationService.hasSubscribeAPIToDefaultUserApplication(apiInfo.getId(), authenticatedUser.getLogin())).thenReturn(true);

		assertThat(apiManagerHelper.checkUsernameAbleToDownloadMedia(metadata, media))
				.isEqualTo(authenticatedUser.getLogin());
	}

	@Test
	@DisplayName("Test de récupération du username qui peut télécharger un jdd ouvert auquel le user connu n'a pas souscrit")
	public void testCheckUsernameAbleToDownloadNotRestrictedMetadataMediaWhenKnownUsernameHasNotSubscribed()
			throws APIManagerException, AppServiceException {
		final AuthenticatedUser authenticatedUser = new AuthenticatedUser("username", UserType.PERSON);
		final ClientKey clientKey = new ClientKey();

		Metadata metadata = new Metadata().globalId(UUID.randomUUID());
		Media media = new MediaFile().mediaId(UUID.randomUUID());

		APISearchCriteria apiSearchCriteria = new APISearchCriteria().globalId(metadata.getGlobalId()).mediaUuid(media.getMediaId());
		APIInfo apiInfo = new APIInfo().id(UUID.randomUUID().toString());

		when(utilContextHelper.getAuthenticatedUser()).thenReturn(authenticatedUser);
		when(aclHelper.getClientKeyByLogin(authenticatedUser.getLogin())).thenReturn(clientKey);
		when(apIsService.searchAPI(apiSearchCriteria)).thenReturn(new APIList().count(1).list(List.of(apiInfo)));
		when(applicationService.hasSubscribeAPIToDefaultUserApplication(apiInfo.getId(), authenticatedUser.getLogin()))
				.thenReturn(false);
		when(metadataDetailsHelper.isRestricted(metadata)).thenReturn(false);

		assertThat(apiManagerHelper.checkUsernameAbleToDownloadMedia(metadata, media))
				.isEqualTo(anonymousUsername);
	}

	@Test
	@DisplayName("Test de récupération du username qui peut télécharger un jdd restreint auquel le user connu a souscrit")
	public void testCheckUsernameAbleToDownloadRestrictedMetadataMediaWhenKnownUsernameHasSubscribed()
			throws APIManagerException, AppServiceException {
		final AuthenticatedUser authenticatedUser = new AuthenticatedUser("username", UserType.PERSON);
		final ClientKey clientKey = new ClientKey();

		Metadata metadata = new Metadata().globalId(UUID.randomUUID());
		Media media = new MediaFile().mediaId(UUID.randomUUID());

		APISearchCriteria apiSearchCriteria = new APISearchCriteria().globalId(metadata.getGlobalId()).mediaUuid(media.getMediaId());
		APIInfo apiInfo = new APIInfo().id(UUID.randomUUID().toString());

		when(utilContextHelper.getAuthenticatedUser()).thenReturn(authenticatedUser);
		when(aclHelper.getClientKeyByLogin(authenticatedUser.getLogin())).thenReturn(clientKey);
		when(apIsService.searchAPI(apiSearchCriteria)).thenReturn(new APIList().count(1).list(List.of(apiInfo)));
		when(applicationService.hasSubscribeAPIToDefaultUserApplication(apiInfo.getId(), authenticatedUser.getLogin()))
				.thenReturn(true);

		assertThat(apiManagerHelper.checkUsernameAbleToDownloadMedia(metadata, media))
				.isEqualTo(authenticatedUser.getLogin());
	}

	@Test
	@DisplayName("Test de récupération du username qui peut télécharger un jdd restreint auquel le user connu n'a pas souscrit")
	public void testCheckUsernameAbleToDownloadRestrictedMetadataMediaWhenKnownUsernameHasNotSubscribed()
			throws APIManagerException {
		final AuthenticatedUser authenticatedUser = new AuthenticatedUser("username", UserType.PERSON);
		final ClientKey clientKey = new ClientKey();

		Metadata metadata = new Metadata().globalId(UUID.randomUUID());
		Media media = new MediaFile().mediaId(UUID.randomUUID());

		APISearchCriteria apiSearchCriteria = new APISearchCriteria().globalId(metadata.getGlobalId()).mediaUuid(media.getMediaId());
		APIInfo apiInfo = new APIInfo().id(UUID.randomUUID().toString());

		when(utilContextHelper.getAuthenticatedUser()).thenReturn(authenticatedUser);
		when(aclHelper.getClientKeyByLogin(authenticatedUser.getLogin())).thenReturn(clientKey);
		when(apIsService.searchAPI(apiSearchCriteria)).thenReturn(new APIList().count(1).list(List.of(apiInfo)));
		when(applicationService.hasSubscribeAPIToDefaultUserApplication(apiInfo.getId(), authenticatedUser.getLogin()))
				.thenReturn(false);
		when(metadataDetailsHelper.isRestricted(metadata)).thenReturn(true);

		assertThatThrownBy(() -> apiManagerHelper.checkUsernameAbleToDownloadMedia(metadata, media))
				.isInstanceOf(AccessDeniedMetadataMedia.class)
				.hasMessage("L'utilisateur ne peut pas accéder au média media_id = %s du jeu de données global_id = %s",
						metadata.getGlobalId(), media.getMediaId());
	}
}
