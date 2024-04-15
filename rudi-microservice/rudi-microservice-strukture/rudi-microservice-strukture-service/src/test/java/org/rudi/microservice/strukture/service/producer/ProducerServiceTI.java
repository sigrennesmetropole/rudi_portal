package org.rudi.microservice.strukture.service.producer;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rudi.common.core.DocumentContent;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.facet.kmedia.bean.KindOfData;
import org.rudi.microservice.strukture.core.bean.Producer;
import org.rudi.microservice.strukture.service.StruktureSpringBootTest;
import org.rudi.microservice.strukture.service.helper.StruktureAuthorisationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@StruktureSpringBootTest
class ProducerServiceTI {

	@Autowired
	private ProducerService producerService;

	@MockBean
	StruktureAuthorisationHelper struktureAuthorisationHelper;


	private final ClassPathResource logo = new ClassPathResource("logos/logo-node.png");


	@BeforeEach
	void init(){
		when(struktureAuthorisationHelper.isAccessGrantedByRole(any())).thenReturn(false);
		when(struktureAuthorisationHelper.isAccessGrantedForUserOnOrganization(any())).thenReturn(true);
	}

	@Test
	void uploadAndDownloadProducerMedia() throws IOException, AppServiceException { // NOSONAR
		final Producer producer = new Producer(UUID.randomUUID());


		try {
			uploadLogo(producer);
			downloadLogo(producer);
		} finally {
			producerService.deleteMedia(producer.getUuid(), KindOfData.LOGO);
		}
	}

	private void uploadLogo(Producer producer) throws AppServiceException, IOException {
		DocumentContent documentContent = DocumentContent.fromResource(logo, false);
		producerService.uploadMedia(producer.getUuid(), KindOfData.LOGO, documentContent);
	}

	private void downloadLogo(Producer producer) throws IOException, AppServiceException {
		final DocumentContent documentContent = producerService.downloadMedia(producer.getUuid(), KindOfData.LOGO);

		final File file = logo.getFile();
		assertThat(documentContent).isNotNull().as("Le document téléchargé a les propriétés attendues")
				.hasFieldOrPropertyWithValue("contentType", "image/png").hasFieldOrPropertyWithValue("fileSize", 2950L)
				.hasNoNullFieldsOrPropertiesExcept("url", "fileStream");
		assertThat(documentContent.getFile()).as("Le fichier téléchargé a le même contenu que celui téléversé")
				.hasSameBinaryContentAs(file);
	}

}
