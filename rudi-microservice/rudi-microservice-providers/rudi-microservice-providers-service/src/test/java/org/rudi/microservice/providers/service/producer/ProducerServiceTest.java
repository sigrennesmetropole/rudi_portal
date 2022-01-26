package org.rudi.microservice.providers.service.producer;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.rudi.common.core.DocumentContent;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.facet.kmedia.bean.KindOfData;
import org.rudi.microservice.providers.core.bean.Producer;
import org.rudi.microservice.providers.service.SpringBootTestApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = { SpringBootTestApplication.class })
class ProducerServiceTest {
	@Autowired
	private ProducerService producerService;
	private final ClassPathResource logo = new ClassPathResource("logos/logo-node.png");

	@Test
	void uploadAndDownloadProducerMedia() throws IOException, AppServiceException { //NOSONAR
		final Producer producer = new Producer(UUID.randomUUID());

		try {
			uploadLogo(producer);
			downloadLogo(producer);
		} finally {
			producerService.deleteMedia(producer.getUuid(), KindOfData.LOGO);
		}
	}

	private void uploadLogo(Producer producer) throws AppServiceException {
		producerService.uploadMedia(producer.getUuid(), KindOfData.LOGO, logo);
	}

	private void downloadLogo(Producer producer) throws IOException, AppServiceException {
		final DocumentContent documentContent = producerService.downloadMedia(producer.getUuid(), KindOfData.LOGO);

		final File file = logo.getFile();
		assertThat(documentContent)
				.isNotNull()
				.as("Le document téléchargé a les propriétés attendues")
				.hasFieldOrPropertyWithValue("contentType", "image/png")
				.hasFieldOrPropertyWithValue("fileSize", 2950L)
				.hasNoNullFieldsOrPropertiesExcept("url", "fileStream")
		;
		assertThat(documentContent.getFile())
				.as("Le fichier téléchargé a le même contenu que celui téléversé")
				.hasSameBinaryContentAs(file);
	}

}