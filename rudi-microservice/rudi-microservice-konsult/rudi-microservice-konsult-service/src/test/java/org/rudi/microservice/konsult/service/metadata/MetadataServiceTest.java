package org.rudi.microservice.konsult.service.metadata;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.rudi.common.core.DocumentContent;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.acl.bean.ClientKey;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.apimaccess.service.ApplicationService;
import org.rudi.facet.dataverse.api.dataset.DatasetOperationAPI;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.bean.Media;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.microservice.konsult.service.KonsultSpringBootTest;
import org.rudi.microservice.konsult.service.exception.AccessDeniedMetadataMediaException;
import org.rudi.microservice.konsult.service.helper.APIManagerHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


/**
 * Class de test de MetadataService
 */
@KonsultSpringBootTest
public class MetadataServiceTest {

	private static final JsonResourceReader JSON_RESOURCE_READER = new JsonResourceReader();
	private final List<Metadata> jddCrees = new ArrayList<>();
	@Autowired
	private MetadataService metadataService;
	@Autowired
	private DatasetService datasetService;
	@Autowired
	private DatasetOperationAPI datasetOperationAPI;
	@MockBean
	private ApplicationService applicationService;
	@MockBean
	private APIManagerHelper apiManagerHelper;
	@MockBean
	private UtilContextHelper utilContextHelper;
	@MockBean
	private ACLHelper aclHelper;
	@Value("${apimanager.oauth2.client.anonymous.username}")
	private String anonymousUsername;

	public void mockUserData(String username) {
		AuthenticatedUser authenticatedUser = new AuthenticatedUser();
		authenticatedUser.setLogin(username);
		when(utilContextHelper.getAuthenticatedUser()).thenReturn(authenticatedUser);
		when(aclHelper.getClientKeyByLogin(username)).thenReturn(new ClientKey().clientId("xxx").clientSecret("yyy"));
	}

	@AfterEach
	public void cleanData() {
		jddCrees.forEach(jdd -> {
			try {
				supprimerJdd(jdd);
			} catch (DataverseAPIException e) {
				fail("Impossible de supprimer le JDD " + jdd.getGlobalId());
			}
		});
	}

	@Test
	public void testDownloadJddOuvert() throws AppServiceException, IOException, DataverseAPIException, APIManagerException {

		mockUserData(anonymousUsername);

		Metadata jddOuvert = creerJddOuvert();
		Media media = getFirstMedia(jddOuvert);

		mockBuildAPIAccessUrls(jddOuvert);

		when(apiManagerHelper.getLoginAbleToDownloadMedia(argThatAsSameGlobalIdAs(jddOuvert), argThatAsSameMediaIdAs(media)))
				.thenReturn(anonymousUsername);

		DocumentContent mediaTelechargeJdd = new DocumentContent("test", "mediaType", new File("null"));

		when(applicationService.downloadAPIContent(eq(jddOuvert.getGlobalId()), eq(media.getMediaId()), anyString()))
				.thenReturn(mediaTelechargeJdd);

		DocumentContent mediaTelechargeJddOuvert = metadataService.downloadMetadataMedia(jddOuvert.getGlobalId(),
				media.getMediaId());

		assertThat(mediaTelechargeJddOuvert).as("vérifier contenu ne soit pas vide").isNotNull();
		assertThat(mediaTelechargeJddOuvert.getFileName()).as("vérifier nom fichier n'est pas vide")
				.isNotBlank();
	}

	private Metadata argThatAsSameGlobalIdAs(Metadata expectedMetadata) {
		return argThat(metadata -> metadata.getGlobalId().equals(expectedMetadata.getGlobalId()));
	}

	private Media argThatAsSameMediaIdAs(Media expectedMedia) {
		return argThat(media -> media.getMediaId().equals(expectedMedia.getMediaId()));
	}

	private void mockBuildAPIAccessUrls(Metadata jdd) throws APIManagerException {
		for (Media media : jdd.getAvailableFormats()) {
			when(applicationService.buildAPIAccessUrl(jdd.getGlobalId(), media.getMediaId()))
					.thenReturn(media.getConnector().getUrl());
		}
	}

	@Test
	public void testDownloadJddWhenMediaIsNotFile() throws DataverseAPIException,
			IOException, AppServiceException, APIManagerException {

		mockUserData(anonymousUsername);

		final Metadata jddOuvert = creerJddOuvert();
		final Media media = getSecondMedia(jddOuvert);

		mockBuildAPIAccessUrls(jddOuvert);

		when(apiManagerHelper.getLoginAbleToDownloadMedia(argThatAsSameGlobalIdAs(jddOuvert), argThatAsSameMediaIdAs(media)))
				.thenReturn(anonymousUsername);

		assertThatThrownBy(() -> metadataService.downloadMetadataMedia(jddOuvert.getGlobalId(), media.getMediaId()))
				.isInstanceOf(AppServiceException.class)
				.hasMessage("Type de média %s non pris en charge",
						media.getMediaType());
	}

	@Test
	public void testDownloadJddRestreintWhenUserHasNoAccess() throws DataverseAPIException,
			IOException, AppServiceException, APIManagerException {

		mockUserData(anonymousUsername);

		final Metadata jddRestreint = creerJddRestreint();
		final Media media = getFirstMedia(jddRestreint);

		mockBuildAPIAccessUrls(jddRestreint);

		when(apiManagerHelper.getLoginAbleToDownloadMedia(argThatAsSameGlobalIdAs(jddRestreint), argThatAsSameMediaIdAs(media)))
				.thenThrow(AccessDeniedMetadataMediaException.class);

		assertThatThrownBy(() -> metadataService.downloadMetadataMedia(jddRestreint.getGlobalId(), media.getMediaId()))
				.isInstanceOf(AccessDeniedMetadataMediaException.class);
	}

	@Test
	public void testDownloadJddRestreintWhenUserHasAccess() throws APIManagerException, DataverseAPIException,
			IOException, AppServiceException {

		String username = "rudi";
		mockUserData(username);

		final Metadata jddRestreint = creerJddRestreint();
		final Media media = getFirstMedia(jddRestreint);

		mockBuildAPIAccessUrls(jddRestreint);

		when(apiManagerHelper.getLoginAbleToDownloadMedia(argThatAsSameGlobalIdAs(jddRestreint), argThatAsSameMediaIdAs(media)))
				.thenReturn(username);

		DocumentContent mediaTelechargeJdd = new DocumentContent("test", "mediaType", new File("null"));

		when(applicationService.downloadAPIContent(eq(jddRestreint.getGlobalId()), eq(media.getMediaId()), anyString()))
				.thenReturn(mediaTelechargeJdd);

		DocumentContent mediaTelechargeJddRestreint = metadataService.downloadMetadataMedia(jddRestreint.getGlobalId(),
				media.getMediaId());

		assertThat(mediaTelechargeJddRestreint).as("vérifier contenu ne soit pas vide").isNotNull();
		assertThat(mediaTelechargeJddRestreint.getFileName()).as("vérifier nom fichier n'est pas vide")
				.isNotBlank();
	}

	private void supprimerJdd(Metadata jddASupprimer) throws DataverseAPIException {

		datasetOperationAPI.deleteDataset(jddASupprimer.getDataverseDoi());

	}

	private Media getFirstMedia(Metadata jddOuvert) {
		return jddOuvert.getAvailableFormats().get(0);
	}

	private Media getSecondMedia(Metadata jddOuvert) {
		return jddOuvert.getAvailableFormats().get(1);
	}

	private Metadata creerJddOuvert() throws IOException, DataverseAPIException {
		final Metadata jddOuvertACreer = JSON_RESOURCE_READER.read("metadata/jdd_ouvert.json", Metadata.class);
		return creerJdd(jddOuvertACreer);
	}

	private Metadata creerJddRestreint() throws IOException, DataverseAPIException {
		final Metadata jddRestreintACreer = JSON_RESOURCE_READER.read("metadata/jdd_restreint.json", Metadata.class);
		return creerJdd(jddRestreintACreer);
	}

	@Nonnull
	private Metadata creerJdd(Metadata jddACreer) throws DataverseAPIException {
		String doi = datasetService.createDataset(jddACreer);
		Metadata jddCree = datasetService.getDataset(doi);

		jddCrees.add(jddCree);
		return jddCree;
	}

}
