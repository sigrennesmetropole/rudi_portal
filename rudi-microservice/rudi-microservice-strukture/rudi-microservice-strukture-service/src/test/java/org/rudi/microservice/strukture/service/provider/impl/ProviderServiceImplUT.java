package org.rudi.microservice.strukture.service.provider.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.helper.ResourceHelper;
import org.rudi.facet.dataverse.api.exceptions.CannotReplaceUnpublishedFileException;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.dataverse.model.ApiResponseInfo;
import org.rudi.facet.kmedia.bean.KindOfData;
import org.rudi.facet.kmedia.bean.MediaDataset;
import org.rudi.facet.kmedia.bean.MediaDatasetList;
import org.rudi.facet.kmedia.bean.MediaOrigin;
import org.rudi.facet.kmedia.service.MediaService;
import org.rudi.microservice.strukture.core.bean.Provider;
import org.rudi.microservice.strukture.service.mapper.ProviderMapper;
import org.rudi.microservice.strukture.storage.dao.provider.ProviderDao;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProviderServiceImplUT {

	@InjectMocks
	private ProviderServiceImpl providerService;
	@Mock
	private MediaService mediaService;
	@Mock
	private ResourceHelper resourceHelper;
	@Mock
	private ProviderMapper providerMapper;
	@Mock
	private ProviderDao providerDao;

	@Test
	void uploadProviderMedia_providerInconnu() throws DataverseAPIException, AppServiceException, IOException {
		final UUID providerUuid = UUID.randomUUID();
		final Resource media = mock(Resource.class);

		final File tempFile = mock(File.class);
		when(resourceHelper.copyResourceToTempFile(media)).thenReturn(tempFile);

		providerService.uploadMedia(providerUuid, KindOfData.LOGO, media);

		verify(mediaService).setMediaFor(MediaOrigin.PROVIDER, providerUuid, KindOfData.LOGO, tempFile);
	}

	@Test
	void uploadProviderMedia_mediaDatasetInexistant() throws IOException, AppServiceException, DataverseAPIException {
		final Provider provider = new Provider()
				.uuid(UUID.fromString("789fe012-741b-4105-b719-6b2067ad32d9"))
				.label("The Good Provider");

		final Resource media = mock(Resource.class);
		final File tempFile = mock(File.class);

		when(resourceHelper.copyResourceToTempFile(media)).thenReturn(tempFile);

		providerService.uploadMedia(provider.getUuid(), KindOfData.LOGO, media);

		verify(mediaService).setMediaFor(MediaOrigin.PROVIDER, provider.getUuid(), KindOfData.LOGO, tempFile);
	}

	@Test
	void uploadProviderMedia_mediaDatasetDejaExistant() throws IOException, AppServiceException, DataverseAPIException {
		final Provider provider = new Provider()
				.uuid(UUID.fromString("789fe012-741b-4105-b719-6b2067ad32d9"))
				.label("The Good Provider");

		final MediaDatasetList mediaDatasetList = new MediaDatasetList();
		final MediaDataset existingMediaDataset = new MediaDataset()
				.dataverseDoi("doi:10.5072/FK2/1QOX4J")
				.kindOfData(KindOfData.LOGO)
				.authorAffiliation(MediaOrigin.PROVIDER)
				.authorIdentifier(provider.getUuid());
		mediaDatasetList.addItemsItem(existingMediaDataset);

		final Resource media = mock(Resource.class);
		final File tempFile = mock(File.class);

		when(resourceHelper.copyResourceToTempFile(media)).thenReturn(tempFile);

		providerService.uploadMedia(provider.getUuid(), KindOfData.LOGO, media);


		verify(mediaService).setMediaFor(MediaOrigin.PROVIDER, provider.getUuid(), KindOfData.LOGO, tempFile);
	}

	@Test
	void uploadProviderMedia_erreurTempFile() throws IOException {
		final Provider provider = new Provider()
				.uuid(UUID.fromString("789fe012-741b-4105-b719-6b2067ad32d9"))
				.label("The Good Provider");

		final MediaDatasetList mediaDatasetList = new MediaDatasetList();
		final MediaDataset existingMediaDataset = new MediaDataset()
				.dataverseDoi("doi:10.5072/FK2/1QOX4J")
				.kindOfData(KindOfData.LOGO)
				.authorAffiliation(MediaOrigin.PROVIDER)
				.authorIdentifier(provider.getUuid());
		mediaDatasetList.addItemsItem(existingMediaDataset);

		final Resource media = mock(Resource.class);

		final IOException cause = new IOException("Erreur de copie du fichier à uploader");
		when(resourceHelper.copyResourceToTempFile(media)).thenThrow(cause);


		assertThatThrownBy(() -> providerService.uploadMedia(provider.getUuid(), KindOfData.LOGO, media))
				.isInstanceOf(AppServiceException.class)
				.hasMessage("Erreur lors de l'upload du LOGO du fournisseur d'id 789fe012-741b-4105-b719-6b2067ad32d9")
				.hasCause(cause);
	}

	@Test
	void uploadProviderMedia_erreurApiDataverse() throws IOException, DataverseAPIException {
		final Provider provider = new Provider()
				.uuid(UUID.fromString("789fe012-741b-4105-b719-6b2067ad32d9"))
				.label("The Good Provider");

		final MediaDatasetList mediaDatasetList = new MediaDatasetList();
		final MediaDataset existingMediaDataset = new MediaDataset()
				.dataverseDoi("doi:10.5072/FK2/1QOX4J")
				.kindOfData(KindOfData.LOGO)
				.authorAffiliation(MediaOrigin.PROVIDER)
				.authorIdentifier(provider.getUuid());
		mediaDatasetList.addItemsItem(existingMediaDataset);

		final Resource media = mock(Resource.class);
		final File tempFile = mock(File.class);

		when(resourceHelper.copyResourceToTempFile(media)).thenReturn(tempFile);

		final CannotReplaceUnpublishedFileException exception = new CannotReplaceUnpublishedFileException("You cannot replace an unpublished file", mock(ApiResponseInfo.class));
		doThrow(exception).when(mediaService).setMediaFor(MediaOrigin.PROVIDER, provider.getUuid(), KindOfData.LOGO, tempFile);


		assertThatThrownBy(() -> providerService.uploadMedia(provider.getUuid(), KindOfData.LOGO, media))
				.isInstanceOf(AppServiceException.class)
				.hasMessage("Erreur lors de l'upload du LOGO du fournisseur d'id 789fe012-741b-4105-b719-6b2067ad32d9")
				.hasCause(exception);
	}
}
