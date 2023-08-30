package org.rudi.microservice.konsult.service.metadata.impl;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.apimaccess.service.APIsService;
import org.rudi.facet.apimaccess.service.ApplicationService;
import org.rudi.facet.dataverse.api.exceptions.DatasetNotFoundException;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.bean.Connector;
import org.rudi.facet.kaccess.bean.Media;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.microservice.konsult.service.exception.APIManagerExternalServiceException;
import org.rudi.microservice.konsult.service.exception.DataverseExternalServiceException;
import org.rudi.microservice.konsult.service.exception.MetadataNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MetadataServiceImplIT {
	@InjectMocks
	private MetadataServiceImpl service;
	@Mock
	private DatasetService datasetService;
	@Mock
	private ApplicationService applicationService;
	@Mock
	private APIsService apIsService;

	@Test
	void getMetadataByIdRewrittenMediaUrlOk() throws AppServiceException, DataverseAPIException, APIManagerException {
		final Media originalMedia = new Media()
				.connector(new Connector()
						.url("https://:@public.sig.rennesmetropole.fr/geonetwork/srv/fre/csw?service=CSW&SERVICE=CSW&outputSchema=http%3A%2F%2Fwww.isotc211.org%2F2005%2Fgmd&request=GetRecordById&namespace=xmlns%28gmd%3Dhttp%3A%2F%2Fwww.isotc211.org%2F2005%2Fgmd%29&resultType=results&VERSION=2.0.2&version=2.0.2&ElementSetName=full&id=5fa3811a-7bb8-4899-8af1-4cda386f26b0"));
		final Metadata originalMetadata = new Metadata()
				.globalId(UUID.randomUUID())
				.availableFormats(Collections.singletonList(originalMedia));

		when(apIsService.existsApi(any(), any())).thenReturn(true);
		when(datasetService.getDataset(originalMetadata.getGlobalId()))
				.thenReturn(originalMetadata);
		when(applicationService.buildAPIAccessUrl(originalMetadata.getGlobalId(), originalMedia.getMediaId()))
				.thenReturn("https://wso2.open-dev.com:8243/datasets/4ff87569-dafc-45ad-ae5b-fac9a5ccbbb1/dwnl/1.0.0");

		final Metadata metadataById = service.getMetadataById(originalMetadata.getGlobalId());

		final List<Media> medias = metadataById.getAvailableFormats();
		assertThat(medias).hasSize(1);
		final Media media = medias.get(0);
		assertThat(media)
				.hasFieldOrPropertyWithValue("connector.url", "/medias/4ff87569-dafc-45ad-ae5b-fac9a5ccbbb1/dwnl/1.0.0");
	}

	@Test
	void getMetadataByIdRewrittenMediaUrlMalformedURL() throws AppServiceException, DataverseAPIException, APIManagerException {
		final Media originalMedia = new Media()
				.connector(new Connector()
						.url("https://:@public.sig.rennesmetropole.fr/geonetwork/srv/fre/csw?service=CSW&SERVICE=CSW&outputSchema=http%3A%2F%2Fwww.isotc211.org%2F2005%2Fgmd&request=GetRecordById&namespace=xmlns%28gmd%3Dhttp%3A%2F%2Fwww.isotc211.org%2F2005%2Fgmd%29&resultType=results&VERSION=2.0.2&version=2.0.2&ElementSetName=full&id=5fa3811a-7bb8-4899-8af1-4cda386f26b0"));
		final Metadata originalMetadata = new Metadata()
				.globalId(UUID.randomUUID())
				.availableFormats(Collections.singletonList(originalMedia));

		when(apIsService.existsApi(any(), any())).thenReturn(true);
		when(datasetService.getDataset(originalMetadata.getGlobalId()))
				.thenReturn(originalMetadata);
		when(applicationService.buildAPIAccessUrl(originalMetadata.getGlobalId(), originalMedia.getMediaId()))
				.thenReturn("BAD_URL//wso2.open-dev.com:8243/datasets/4ff87569-dafc-45ad-ae5b-fac9a5ccbbb1/dwnl/1.0.0");

		final Metadata metadataById = service.getMetadataById(originalMetadata.getGlobalId());

		final List<Media> medias = metadataById.getAvailableFormats();
		assertThat(medias).hasSize(1);
		final Media media = medias.get(0);
		assertThat(media)
				.hasFieldOrPropertyWithValue("connector.url", "");
	}

	@Test
	void getMetadataByIdWithoutAnExistingApi() throws DataverseAPIException, APIManagerException, AppServiceException {

		// On crée des metadata qu'on va get avec notre test
		final Media originalMedia = new Media()
				.connector(new Connector()
						.url("https://:@public.sig.rennesmetropole.fr/geonetwork/srv/fre/csw?service=CSW&SERVICE=CSW&outputSchema=http%3A%2F%2Fwww.isotc211.org%2F2005%2Fgmd&request=GetRecordById&namespace=xmlns%28gmd%3Dhttp%3A%2F%2Fwww.isotc211.org%2F2005%2Fgmd%29&resultType=results&VERSION=2.0.2&version=2.0.2&ElementSetName=full&id=5fa3811a-7bb8-4899-8af1-4cda386f26b0"));

		final Metadata originalMetadata = new Metadata()
				.globalId(UUID.randomUUID())
				.availableFormats(Collections.singletonList(originalMedia));

		when(datasetService.getDataset(originalMetadata.getGlobalId()))
				.thenReturn(originalMetadata);

		// Et on fait en sorte que quand on lui cherche une API ben il retourne un truc vide
		when(apIsService.existsApi(any(), any())).thenReturn(false);

		// On vérifie qu'on arrive bien à get les metadata
		assertThat(service.getMetadataById(originalMetadata.getGlobalId())).isNotNull();
	}

	@Test
	void getMetadataByIdRewrittenMediaUrl() throws DataverseAPIException, APIManagerException {
		final Media originalMedia = new Media()
				.connector(new Connector()
						.url("https://:@public.sig.rennesmetropole.fr/geonetwork/srv/fre/csw?service=CSW&SERVICE=CSW&outputSchema=http%3A%2F%2Fwww.isotc211.org%2F2005%2Fgmd&request=GetRecordById&namespace=xmlns%28gmd%3Dhttp%3A%2F%2Fwww.isotc211.org%2F2005%2Fgmd%29&resultType=results&VERSION=2.0.2&version=2.0.2&ElementSetName=full&id=5fa3811a-7bb8-4899-8af1-4cda386f26b0"));
		final Metadata originalMetadata = new Metadata()
				.globalId(UUID.randomUUID())
				.availableFormats(Collections.singletonList(originalMedia));

		when(apIsService.existsApi(any(), any())).thenReturn(true);
		when(datasetService.getDataset(originalMetadata.getGlobalId()))
				.thenReturn(originalMetadata);
		final APIManagerException apiManagerException = new APIManagerException("media not found");
		when(applicationService.buildAPIAccessUrl(originalMetadata.getGlobalId(), originalMedia.getMediaId()))
				.thenThrow(apiManagerException);

		assertThatThrownBy(() -> service.getMetadataById(originalMetadata.getGlobalId()))
				.isInstanceOf(APIManagerExternalServiceException.class)
				.hasCause(apiManagerException)
				.hasMessage("Erreur reçue du service externe API Manager");
	}

	@Test
	void getMetadataByIdDatasetNotFoundException() throws DataverseAPIException {
		final Media originalMedia = new Media()
				.connector(new Connector()
						.url("https://:@public.sig.rennesmetropole.fr/geonetwork/srv/fre/csw?service=CSW&SERVICE=CSW&outputSchema=http%3A%2F%2Fwww.isotc211.org%2F2005%2Fgmd&request=GetRecordById&namespace=xmlns%28gmd%3Dhttp%3A%2F%2Fwww.isotc211.org%2F2005%2Fgmd%29&resultType=results&VERSION=2.0.2&version=2.0.2&ElementSetName=full&id=5fa3811a-7bb8-4899-8af1-4cda386f26b0"));
		final Metadata originalMetadata = new Metadata()
				.globalId(UUID.fromString("72121fbc-9ab6-4cf5-83b5-b4621c97c737"))
				.availableFormats(Collections.singletonList(originalMedia));

		final DatasetNotFoundException datasetNotFoundException = DatasetNotFoundException.fromGlobalId(originalMetadata.getGlobalId());
		when(datasetService.getDataset(originalMetadata.getGlobalId()))
				.thenThrow(datasetNotFoundException);

		assertThatThrownBy(() -> service.getMetadataById(originalMetadata.getGlobalId()))
				.isInstanceOf(MetadataNotFoundException.class)
				.hasMessage("Le Dataset de globalId=\"72121fbc-9ab6-4cf5-83b5-b4621c97c737\" est introuvable");
	}

	@Test
	void getMetadataByIdDataverseAPIException() throws DataverseAPIException {
		final Media originalMedia = new Media()
				.connector(new Connector()
						.url("https://:@public.sig.rennesmetropole.fr/geonetwork/srv/fre/csw?service=CSW&SERVICE=CSW&outputSchema=http%3A%2F%2Fwww.isotc211.org%2F2005%2Fgmd&request=GetRecordById&namespace=xmlns%28gmd%3Dhttp%3A%2F%2Fwww.isotc211.org%2F2005%2Fgmd%29&resultType=results&VERSION=2.0.2&version=2.0.2&ElementSetName=full&id=5fa3811a-7bb8-4899-8af1-4cda386f26b0"));
		final Metadata originalMetadata = new Metadata()
				.globalId(UUID.fromString("72121fbc-9ab6-4cf5-83b5-b4621c97c737"))
				.availableFormats(Collections.singletonList(originalMedia));

		final DataverseAPIException datasetNotFoundException = new DataverseAPIException("Erreur interne Dataverse");
		when(datasetService.getDataset(originalMetadata.getGlobalId()))
				.thenThrow(datasetNotFoundException);

		assertThatThrownBy(() -> service.getMetadataById(originalMetadata.getGlobalId()))
				.isInstanceOf(DataverseExternalServiceException.class)
				.hasCause(datasetNotFoundException)
				.hasMessage("Erreur reçue du service externe Dataverse");
	}

}
