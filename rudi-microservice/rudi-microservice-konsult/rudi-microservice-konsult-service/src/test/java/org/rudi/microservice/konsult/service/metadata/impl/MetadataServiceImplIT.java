package org.rudi.microservice.konsult.service.metadata.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

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
import org.rudi.facet.dataverse.api.exceptions.DatasetNotFoundException;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.bean.Connector;
import org.rudi.facet.kaccess.bean.Media;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.microservice.konsult.service.exception.DataverseExternalServiceException;
import org.rudi.microservice.konsult.service.exception.MetadataNotFoundException;

@ExtendWith(MockitoExtension.class)
class MetadataServiceImplIT {
	@InjectMocks
	private MetadataServiceImpl service;
	@Mock
	private DatasetService datasetService;

	@Test
	void getMetadataByIdRewrittenMediaUrlOk() throws AppServiceException, DataverseAPIException, APIManagerException {
		UUID mediaId = UUID.fromString("8ec31763-a4a4-44d7-8624-801632d29382");
		UUID globalId = UUID.fromString("d6dcaed6-c8a9-4a35-b87c-7802acf3ebb2");
		final Media originalMedia = new Media().mediaId(mediaId).connector(new Connector().url(
				"https://:@public.sig.rennesmetropole.fr/geonetwork/srv/fre/csw?service=CSW&SERVICE=CSW&outputSchema=http%3A%2F%2Fwww.isotc211.org%2F2005%2Fgmd&request=GetRecordById&namespace=xmlns%28gmd%3Dhttp%3A%2F%2Fwww.isotc211.org%2F2005%2Fgmd%29&resultType=results&VERSION=2.0.2&version=2.0.2&ElementSetName=full&id=5fa3811a-7bb8-4899-8af1-4cda386f26b0")
				.interfaceContract("dwnl"));
		final Metadata originalMetadata = new Metadata().globalId(globalId)
				.availableFormats(Collections.singletonList(originalMedia));

		when(datasetService.getDataset(originalMetadata.getGlobalId())).thenReturn(originalMetadata);

		final Metadata metadataById = service.getMetadataById(originalMetadata.getGlobalId());

		final List<Media> medias = metadataById.getAvailableFormats();
		assertThat(medias).hasSize(1);
		final Media media = medias.get(0);
		assertThat(media).hasFieldOrPropertyWithValue("connector.url",
				"/medias/d6dcaed6-c8a9-4a35-b87c-7802acf3ebb2/8ec31763-a4a4-44d7-8624-801632d29382/dwnl");
	}

	@Test
	void getMetadataByIdDatasetNotFoundException() throws DataverseAPIException {
		final Media originalMedia = new Media().mediaId(UUID.randomUUID()).connector(new Connector().url(
				"https://:@public.sig.rennesmetropole.fr/geonetwork/srv/fre/csw?service=CSW&SERVICE=CSW&outputSchema=http%3A%2F%2Fwww.isotc211.org%2F2005%2Fgmd&request=GetRecordById&namespace=xmlns%28gmd%3Dhttp%3A%2F%2Fwww.isotc211.org%2F2005%2Fgmd%29&resultType=results&VERSION=2.0.2&version=2.0.2&ElementSetName=full&id=5fa3811a-7bb8-4899-8af1-4cda386f26b0")
				.interfaceContract("dwnl"));
		final Metadata originalMetadata = new Metadata()
				.globalId(UUID.fromString("72121fbc-9ab6-4cf5-83b5-b4621c97c737"))
				.availableFormats(Collections.singletonList(originalMedia));

		final DatasetNotFoundException datasetNotFoundException = DatasetNotFoundException
				.fromGlobalId(originalMetadata.getGlobalId());
		when(datasetService.getDataset(originalMetadata.getGlobalId())).thenThrow(datasetNotFoundException);

		assertThatThrownBy(() -> service.getMetadataById(originalMetadata.getGlobalId()))
				.isInstanceOf(MetadataNotFoundException.class)
				.hasMessage("Le Dataset de globalId=\"72121fbc-9ab6-4cf5-83b5-b4621c97c737\" est introuvable");
	}

	@Test
	void getMetadataByIdDataverseAPIException() throws DataverseAPIException {
		final Media originalMedia = new Media().mediaId(UUID.randomUUID()).connector(new Connector().url(
				"https://:@public.sig.rennesmetropole.fr/geonetwork/srv/fre/csw?service=CSW&SERVICE=CSW&outputSchema=http%3A%2F%2Fwww.isotc211.org%2F2005%2Fgmd&request=GetRecordById&namespace=xmlns%28gmd%3Dhttp%3A%2F%2Fwww.isotc211.org%2F2005%2Fgmd%29&resultType=results&VERSION=2.0.2&version=2.0.2&ElementSetName=full&id=5fa3811a-7bb8-4899-8af1-4cda386f26b0")
				.interfaceContract("dwnl"));
		final Metadata originalMetadata = new Metadata()
				.globalId(UUID.fromString("72121fbc-9ab6-4cf5-83b5-b4621c97c737"))
				.availableFormats(Collections.singletonList(originalMedia));

		final DataverseAPIException datasetInternalException = new DataverseAPIException("Erreur interne Dataverse");
		when(datasetService.getDataset(originalMetadata.getGlobalId())).thenThrow(datasetInternalException);

		assertThatThrownBy(() -> service.getMetadataById(originalMetadata.getGlobalId()))
				.isInstanceOf(DataverseExternalServiceException.class).hasCause(datasetInternalException)
				.hasMessage("Erreur reçue du service externe Dataverse");
	}

}
