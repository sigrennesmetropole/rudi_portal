package org.rudi.microservice.konsult.service.metadata.impl;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.bean.Connector;
import org.rudi.facet.kaccess.bean.Contact;
import org.rudi.facet.kaccess.bean.DictionaryEntry;
import org.rudi.facet.kaccess.bean.HashAlgorithm;
import org.rudi.facet.kaccess.bean.Language;
import org.rudi.facet.kaccess.bean.Licence;
import org.rudi.facet.kaccess.bean.LicenceStandard;
import org.rudi.facet.kaccess.bean.Media;
import org.rudi.facet.kaccess.bean.MediaFile;
import org.rudi.facet.kaccess.bean.MediaFileAllOfChecksum;
import org.rudi.facet.kaccess.bean.MediaType;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.MetadataAccessCondition;
import org.rudi.facet.kaccess.bean.MetadataMetadataInfo;
import org.rudi.facet.kaccess.bean.ReferenceDates;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.microservice.konsult.service.KonsultSpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.RequiredArgsConstructor;
import static org.assertj.core.api.Assertions.assertThat;

@KonsultSpringBootTest
@RequiredArgsConstructor
class MetadataWithSameThemeFinderIT {

	private final JsonResourceReader jsonResourceReader = new JsonResourceReader();
	private final List<String> createdDatasetDoiList = new ArrayList<>();

	@Autowired
	private MetadataWithSameThemeFinder metadataWithSameThemeFinder;
	@Autowired
	private DatasetService datasetService;

	@AfterEach
	public void cleanData() throws DataverseAPIException {
		for (final var createdMetadataDoi : createdDatasetDoiList) {
			deleteDataset(createdMetadataDoi);
		}
	}

	@Nonnull
	private String createDataset(String basename) throws DataverseAPIException, IOException {
		final var metadata = jsonResourceReader.read("metadataWithSameThemeFinder/" + basename, Metadata.class);
		fillOtherFields(metadata);
		final var doi = datasetService.createDataset(metadata);
		this.createdDatasetDoiList.add(doi);
		return doi;
	}

	private void fillOtherFields(Metadata metadata) {
		final var dates = new ReferenceDates()
				.created(OffsetDateTime.now())
				.validated(OffsetDateTime.now())
				.published(OffsetDateTime.now())
				.updated(OffsetDateTime.now());
		metadata.globalId(UUID.randomUUID())
				.synopsis(Collections.singletonList(new DictionaryEntry()
						.lang(Language.FR_FR)
						.text("Synopsis obligatoire.")
				))
				.datasetDates(dates)
				.storageStatus(Metadata.StorageStatusEnum.ONLINE)
				.metadataInfo(new MetadataMetadataInfo()
						.apiVersion("1.2.0")
						.metadataDates(dates))
				.availableFormats(Collections.singletonList(new MediaFile()
						.fileType(MediaType.TEXT_CSV)
						.fileSize(50L)
						.checksum(new MediaFileAllOfChecksum()
								.algo(HashAlgorithm.MD5)
								.hash("SHA-256")
						)
						.mediaType(Media.MediaTypeEnum.FILE)
						.mediaId(UUID.randomUUID())
						.mediaCaption("On est obligé de mettre au moins un média pour respecter le swagger")
						.connector(new Connector()
								.url("www.connector1.org")
								.interfaceContract("interface contrat 1")
						)
				))
				.contacts(Collections.singletonList(new Contact()
						.contactId(UUID.randomUUID())
						.contactName("Nom du contact obligatoire")
						.email("contact_test@rudi.fr")
				))
				.accessCondition(new MetadataAccessCondition()
						.licence(new LicenceStandard()
								.licenceLabel(LicenceStandard.LicenceLabelEnum.APACHE_2_0)
								.licenceType(Licence.LicenceTypeEnum.STANDARD)
						)
				)
		;
	}

	private void deleteDataset(String doi) throws DataverseAPIException {
		datasetService.deleteDataset(doi);
	}

	@Test
	void find_theme() throws DataverseAPIException, IOException {
		final var baseDatasetDoi = createDataset("base.json");
		final var datasetWithSameThemeDoi = createDataset("same-theme.json");
		final var datasetWithDifferentThemeDoi = createDataset("different-theme.json");

		final var metadataList = metadataWithSameThemeFinder.find(baseDatasetDoi, 3);

		assertThat(metadataList).extracting("dataverseDoi")
				.as("Les jeux de données affichés doivent avoir le même thème que le jeu de donnée de la page")
				.contains(datasetWithSameThemeDoi)
				.doesNotContain(datasetWithDifferentThemeDoi);
	}

	@Test
	void find_producer() throws DataverseAPIException, IOException {
		final var baseDatasetDoi = createDataset("base.json");
		final var datasetWithSameProducer = createDataset("producer-rudi.json");
		final var datasetWithDifferentProducer1 = createDataset("producer-ademe-1keyword.json");
		final var datasetWithDifferentProducer2 = createDataset("producer-irisa.json");
		final var datasetWithDifferentProducer3 = createDataset("producer-rm.json");

		final var metadataList = metadataWithSameThemeFinder.find(baseDatasetDoi, 3);

		assertThat(metadataList).extracting("dataverseDoi")
				.as("En priorité, on cherche à afficher des jeux de données d'autre producteur que celui de" +
						" la page : 1 producteur différent pour les 3 jeux de données")
				.contains(datasetWithDifferentProducer1, datasetWithDifferentProducer2, datasetWithDifferentProducer3)
				.doesNotContain(datasetWithSameProducer);
	}

	@Test
	void find_producer_keywords() throws DataverseAPIException, IOException {
		final var baseDatasetDoi = createDataset("base.json");
		final var datasetProducerAWithCommonKeywords = createDataset("producer-ademe.json");
		final var datasetProducerAWithoutKeywords = createDataset("producer-ademe-0keyword.json");
		final var datasetProducerBWithCommonKeywords = createDataset("producer-irisa.json");
		final var datasetProducerBWithoutKeywords = createDataset("producer-irisa-0keyword.json");
		final var datasetProducerCWithCommonKeywords = createDataset("producer-rm.json");
		final var datasetProducerCWithoutKeywords = createDataset("producer-rm-0keyword.json");

		final var metadataList = metadataWithSameThemeFinder.find(baseDatasetDoi, 3);

		assertThat(metadataList).extracting("dataverseDoi")
				.as("Si pour un même producteur, plusieurs jeux de données ont le thème recherché, " +
						"on affiche le jeu de données qui a le plus de mots clefs en commun avec le jeu de données " +
						"de la page. S'il y a égalité dans le nombre de mots clefs, on affiche le jdd renvoyé en " +
						"premier par la requête (sans pré-sélection spécifique, de manière aléatoire)")
				.contains(datasetProducerAWithCommonKeywords, datasetProducerBWithCommonKeywords, datasetProducerCWithCommonKeywords)
				.doesNotContain(datasetProducerAWithoutKeywords, datasetProducerBWithoutKeywords, datasetProducerCWithoutKeywords);
	}

	@Test
	void find_keywords() throws DataverseAPIException, IOException {
		final var baseDatasetDoi = createDataset("base.json");
		final var datasetWith0Keyword = createDataset("producer-ademe-0keyword.json");
		final var datasetWith1Keyword = createDataset("producer-ademe-1keyword.json");
		final var datasetWith2Keywords = createDataset("producer-ademe-2keywords.json");
		final var datasetWith3Keywords = createDataset("producer-ademe-3keywords.json");

		final var metadataList = metadataWithSameThemeFinder.find(baseDatasetDoi, 3);

		assertThat(metadataList).extracting("dataverseDoi")
				.as("S'il n'existe pas 3 jeux de données de producteur différent alors on affiche des jeux " +
						"de données d'un même producteur : on affiche les jdd qui ont le plus de mots clefs en " +
						"commun avec le jdd de la page")
				.contains(datasetWith3Keywords)
				.doesNotContain(datasetWith0Keyword, datasetWith1Keyword, datasetWith2Keywords);
	}

	@Test
	void find_minimal() throws DataverseAPIException, IOException {
		final var baseDatasetDoi = createDataset("base.json");
		final var datasetWith0Keyword = createDataset("producer-ademe-0keyword.json");

		final var metadataList = metadataWithSameThemeFinder.find(baseDatasetDoi, 3);

		assertThat(metadataList).extracting("dataverseDoi")
				.as("On prend en compte les JDD qui n'ont aucun mot-clé en commun, pour avoir un maximum " +
						"de JDD parmis ceux disponibles")
				.contains(datasetWith0Keyword)
				.as("On doit quand même exclure le JDD de base")
				.doesNotContain(baseDatasetDoi);
	}

}
