package org.rudi.facet.kaccess.service.dataset;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.common.test.RudiAssertions;
import org.rudi.common.test.UUIDUtils;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.dataverse.api.exceptions.DataverseMappingException;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlock;
import org.rudi.facet.dataverse.utils.MessageUtils;
import org.rudi.facet.kaccess.KaccessSpringBootTest;
import org.rudi.facet.kaccess.bean.DatasetSearchCriteria;
import org.rudi.facet.kaccess.bean.Media;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.MetadataFacets;
import org.rudi.facet.kaccess.bean.MetadataList;
import org.rudi.facet.kaccess.bean.MetadataListFacets;
import org.rudi.facet.kaccess.bean.ReferenceDates;
import org.rudi.facet.kaccess.exceptions.DatasetAlreadyExists;
import org.rudi.facet.kaccess.helper.dataset.metadatablock.MetadataBlockHelper;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.rudi.common.core.util.DateTimeUtils.toUTC;
import static org.rudi.facet.kaccess.constant.ConstantMetadata.DOI_REGEX;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.DATASET_DATES_UPDATED;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO_DATES_UPDATED;

@KaccessSpringBootTest
@Slf4j
class DatasetServiceTest {

	private final JsonResourceReader jsonResourceReader = new JsonResourceReader();
	private final List<Metadata> createdDatasets = new ArrayList<>();
	@Autowired
	private DatasetService datasetService;
	@Autowired
	private MetadataBlockHelper metadataBLockHelper;

	/**
	 * Pour éviter des erreurs avec Dataverse et WSO2, on remplace certains UUID par des UUID aléatoires
	 *
	 * @param metadata JDD à traiter
	 */
	private static void randomizeUuids(Metadata metadata) {
		if (metadata.getGlobalId() != null) {
			metadata.setGlobalId(UUID.randomUUID());
		}
		metadata.getAvailableFormats().forEach(media -> media.setMediaId(UUID.randomUUID()));
	}

	@AfterEach
	void tearDown() {
		createdDatasets.forEach(createdDataset -> {
			final UUID globalId = createdDataset.getGlobalId();
			try {
				datasetService.deleteDataset(globalId);
			} catch (DataverseAPIException e) {
				log.error("Error when deleting Dataset with globalId " + globalId, e);
			}
		});
		createdDatasets.clear();
	}

	@Test
	void testCreateDataset() throws DataverseAPIException, IOException, JSONException {

		final Metadata metadata = readMetadata("agri");
		final String dataverseDoi = createDataset(metadata);

		Assertions.assertTrue(dataverseDoi.matches(DOI_REGEX));

		Metadata metadataGetted = datasetService.getDataset(metadata.getGlobalId());

		// plus tard on fera juste Assertions.assertEquals(metadata, metadataGetted)
		// mais actuellement toutes les propriétés ne sont pas encore bien gérées
		Assertions.assertEquals(metadata.getGlobalId(), metadataGetted.getGlobalId());
		Assertions.assertEquals(metadata.getLocalId(), metadataGetted.getLocalId());
		Assertions.assertEquals(metadata.getDoi(), metadataGetted.getDoi());
		Assertions.assertEquals(metadata.getResourceTitle(), metadataGetted.getResourceTitle());
		Assertions.assertTrue(CollectionUtils.isEqualCollection(metadata.getSummary(), metadataGetted.getSummary()));
		Assertions.assertTrue(CollectionUtils.isEqualCollection(metadata.getSynopsis(), metadataGetted.getSynopsis()));
		Assertions.assertEquals(metadata.getTheme(), metadataGetted.getTheme());
		Assertions.assertTrue(CollectionUtils.isEqualCollection(metadata.getKeywords(), metadataGetted.getKeywords()));
		Assertions.assertEquals(metadata.getProducer(), metadataGetted.getProducer());
		Assertions.assertTrue(CollectionUtils.isEqualCollection(metadata.getContacts(), metadataGetted.getContacts()));
		RudiAssertions.assertThat(metadataGetted.getAvailableFormats()).isJsonEqualTo(metadata.getAvailableFormats());
		Assertions.assertTrue(CollectionUtils.isEqualCollection(metadata.getResourceLanguages(),
				metadataGetted.getResourceLanguages()));
		Assertions.assertEquals(metadata.getTemporalSpread(), metadataGetted.getTemporalSpread());
		Assertions.assertEquals(metadata.getGeography(), metadataGetted.getGeography());
		Assertions.assertEquals(metadata.getDatasetSize(), metadataGetted.getDatasetSize());
		Assertions.assertEquals(metadata.getDatasetDates(), metadataGetted.getDatasetDates());
		Assertions.assertEquals(metadata.getStorageStatus(), metadataGetted.getStorageStatus());
		Assertions.assertEquals(metadata.getMetadataInfo(), metadataGetted.getMetadataInfo());

		// création du jeu de données transports
		final Metadata metadataTransport = readMetadata("transport");
		createDataset(metadataTransport);

		// recherche tous critères renseignés
		// -----------------------------------
		MetadataList metadataList1 = datasetService.searchDatasets(new DatasetSearchCriteria().offset(0).limit(100)
						.globalIds(Collections.singletonList(metadata.getGlobalId())).freeText("exploitations").addThemesItem("environment")
						.addKeywordsItem("agriculture").addKeywordsItem("biogaz").addProducerNamesItem("Producteur rudi")
						.dateDebut(toUTC(LocalDateTime.of(2021, Month.JANUARY, 1, 0, 0, 0)))
						.dateFin(OffsetDateTime.now().plusYears(1)),
				Collections.emptyList()).getMetadataList();

		Assertions.assertTrue(metadataList1.getTotal() > 0);
		Assertions.assertTrue(metadataList1.getItems().stream()
				.anyMatch(metadataItem -> metadataItem.getGlobalId().equals(metadata.getGlobalId())));

		// recherche texte libre
		// ---------------------
		// groupe de mots, présent uniquement dans le titre
		MetadataList metadataList2 = datasetService.searchDatasets(
				new DatasetSearchCriteria().offset(0).limit(100).freeText("Réseau de transport intermodal"),
				Collections.emptyList()).getMetadataList();
		Assertions.assertTrue(metadataList2.getTotal() > 0);
		Assertions.assertTrue(metadataList2.getItems().stream()
				.anyMatch(metadataItem -> metadataItem.getGlobalId().equals(metadataTransport.getGlobalId())));

		// mot seul complet, présent uniquement dans le synopsis
		MetadataList metadataList3 = datasetService
				.searchDatasets(new DatasetSearchCriteria().offset(0).limit(100).freeText("Synopsis"),
						Collections.emptyList())
				.getMetadataList();
		Assertions.assertTrue(metadataList3.getTotal() > 0);
		Assertions.assertTrue(metadataList3.getItems().stream()
				.anyMatch(metadataItem -> metadataItem.getGlobalId().equals(metadataTransport.getGlobalId())));
		// mot seul incomplet, présent dans le titre et dans le synopsis
		MetadataList metadataList4 = datasetService
				.searchDatasets(new DatasetSearchCriteria().offset(0).limit(100).freeText("transpo"),
						Collections.emptyList())
				.getMetadataList();
		Assertions.assertTrue(metadataList4.getTotal() > 0);
		Assertions.assertTrue(metadataList4.getItems().stream()
				.anyMatch(metadataItem -> metadataItem.getGlobalId().equals(metadataTransport.getGlobalId())));

		// recherche sur global id
		// -----------------------
		MetadataList metadataList5 = datasetService.searchDatasets(
				new DatasetSearchCriteria().offset(0).limit(100).globalIds(Collections.singletonList(metadataTransport.getGlobalId())),
				Collections.emptyList()).getMetadataList();
		Assertions.assertTrue(metadataList5.getTotal() > 0);
		Assertions.assertTrue(metadataList5.getItems().stream()
				.anyMatch(metadataItem -> metadataItem.getGlobalId().equals(metadataTransport.getGlobalId())));

		// recherche sur keywords
		// ----------------------
		// on doit récupérer les JDD qui ont transport OU tram OU wrongkeyword dans leur liste de mots clés
		// bus et tram en font partie, mais pas wrongKeyword
		MetadataList metadataList6 = datasetService
				.searchDatasets(new DatasetSearchCriteria().offset(0).limit(100).addKeywordsItem("bus")
						.addKeywordsItem("tram").addKeywordsItem("wrongKeyword"), Collections.emptyList())
				.getMetadataList();
		Assertions.assertTrue(metadataList6.getTotal() > 0);
		Assertions.assertTrue(metadataList6.getItems().stream()
				.anyMatch(metadataItem -> metadataItem.getGlobalId().equals(metadataTransport.getGlobalId())));

		MetadataList metadataList7 = datasetService
				.searchDatasets(new DatasetSearchCriteria().offset(0).limit(100).addKeywordsItem("wrongKeyword"),
						Collections.emptyList())
				.getMetadataList();
		Assertions.assertEquals(0, metadataList7.getTotal());

		// recherche sur themes
		// ---------------------
		MetadataList metadataList8 = datasetService.searchDatasets(
				new DatasetSearchCriteria().offset(0).limit(100).addThemesItem("transport").addThemesItem("wrongTheme"),
				Collections.emptyList()).getMetadataList();
		Assertions.assertTrue(metadataList8.getTotal() > 0);
		Assertions.assertTrue(metadataList8.getItems().stream()
				.anyMatch(metadataItem -> metadataItem.getGlobalId().equals(metadataTransport.getGlobalId())));

		//
		// recherche sur ProducerNames
		// ---------------------------
		MetadataList metadataList9 = datasetService
				.searchDatasets(new DatasetSearchCriteria().offset(0).limit(100).addProducerNamesItem("Metropole"),
						Collections.emptyList())
				.getMetadataList();
		Assertions.assertTrue(metadataList9.getTotal() > 0);
		Assertions.assertTrue(metadataList9.getItems().stream()
				.anyMatch(metadataItem -> metadataItem.getGlobalId().equals(metadataTransport.getGlobalId())));

		// ----------------------------------------------------------
		// Recherche pour s'assurer que seuls les jdd correspondant aux valeurs exactes sont remontés
		// ----------------------------------------------------------
		final Metadata metadataKeolisStarAgriculture = readMetadata("jdd_producer_keolis-star_theme_agriculture");
		createDataset(metadataKeolisStarAgriculture);
		final Metadata metadataStarAgricultureBiologique = readMetadata("jdd_producer_star_theme_agriculture_biologique");
		createDataset(metadataStarAgricultureBiologique);

		MetadataList metadataList10 = datasetService.searchDatasets(new DatasetSearchCriteria().offset(0).limit(100)
				.addProducerNamesItem("star"), Collections.emptyList()).getMetadataList();
		Assertions.assertTrue(metadataList10.getTotal() > 0);
		// on s'assure que les jdd avec producer_name = Keolis star ne sont pas remontés
		Assertions.assertTrue(metadataList10.getItems().stream()
				.noneMatch(metadataItem -> metadataItem.getProducer().getOrganizationName().equals("Keolis star")));

		MetadataList metadataList11 = datasetService.searchDatasets(new DatasetSearchCriteria().offset(0).limit(100)
				.addThemesItem("agriculture"), Collections.emptyList()).getMetadataList();
		Assertions.assertTrue(metadataList11.getTotal() > 0);
		// on s'assure que les jdd avec theme = agriculture biologique ne sont pas remontés
		Assertions.assertTrue(metadataList11.getItems().stream()
				.noneMatch(metadataItem -> metadataItem.getTheme().equals("agriculture biologique")));

		// ----------------------------------------------------------
		// Recherche pour s'assurer que les facets sont bien remontés
		// ----------------------------------------------------------
		MetadataListFacets metadataListFacets1 = datasetService.searchDatasets(
				new DatasetSearchCriteria().offset(0).limit(1),
				Arrays.asList("producer_organization_name", "keywords"));
		MetadataFacets metadataFacets1 = metadataListFacets1.getFacets();
		Assertions.assertNotNull(metadataFacets1);
		Assertions.assertTrue(CollectionUtils.isNotEmpty(metadataFacets1.getItems()));
		Assertions.assertTrue(metadataFacets1.getItems().stream()
				.anyMatch(metadataFacet -> metadataFacet.getPropertyName().equals("producer_organization_name")
						&& CollectionUtils.isNotEmpty(metadataFacet.getValues())));
		Assertions.assertTrue(metadataFacets1.getItems().stream()
				.anyMatch(metadataFacet -> metadataFacet.getPropertyName().equals("keywords")
						&& CollectionUtils.isNotEmpty(metadataFacet.getValues())));

	}

	private Metadata readMetadata(String name) throws IOException {
		final Metadata metadata = jsonResourceReader.read("metadata/" + name + ".json", Metadata.class);
		randomizeUuids(metadata);
		return metadata;
	}

	private String createDataset(Metadata metadata) throws DataverseAPIException {
		final UUID globalId = metadata.getGlobalId();

		if (createdDatasets.stream().anyMatch(createdDataset -> createdDataset.getGlobalId().equals(globalId))) {
			throw new DatasetAlreadyExists(globalId);
		}
		datasetService.deleteDataset(globalId);

		final String dataverseDoi = datasetService.createDataset(metadata);
		assertThat(dataverseDoi).isNotEmpty();

		metadata.setDataverseDoi(dataverseDoi);

		createdDatasets.add(metadata);

		return dataverseDoi;
	}

	@Test
	void testCreateDatasetWithMandatoryPropertiesOnly() throws DataverseAPIException, IOException {
		// test si la création avec les données minimales marche bien
		final Metadata metadataMandatoryProperties = readMetadata("metadata_only_mandatory_properties");
		final String dataverseDoi = createDataset(metadataMandatoryProperties);

		// test de la récupération de métadonnées avec les informations minimales
		Metadata metadataGetted = datasetService.getDataset(dataverseDoi);
		Assertions.assertEquals(metadataMandatoryProperties.getGlobalId(), metadataGetted.getGlobalId());
		Assertions.assertEquals(metadataMandatoryProperties.getResourceTitle(), metadataGetted.getResourceTitle());
	}

	@Test
	void testCreateDatasetWithoutUpdatedReferenceDates() throws IOException {
		// test de la valeur dataset_dates.updated
		final Metadata metadataWithoutUpdatedDatasetDates = readMetadata("metadata_without_reference_dates_updated");
		assertThatThrownBy(() -> createDataset(metadataWithoutUpdatedDatasetDates))
				.isInstanceOf(NullPointerException.class)
				.hasMessage(MessageUtils.buildErrorMessageRequiredMandatoryAttributes(DATASET_DATES_UPDATED));

		// test de la valeur metadata_info.metadata_dates.updated
		metadataWithoutUpdatedDatasetDates.getDatasetDates().setUpdated(OffsetDateTime.now());
		metadataWithoutUpdatedDatasetDates.getMetadataInfo().setMetadataDates(new ReferenceDates().created(OffsetDateTime.now()));
		assertThatThrownBy(() -> createDataset(metadataWithoutUpdatedDatasetDates))
				.isInstanceOf(NullPointerException.class)
				.hasMessage(MessageUtils.buildErrorMessageRequiredMandatoryAttributes(METADATA_INFO_DATES_UPDATED));
	}

	@Test
	void testMapping() throws JSONException, IOException, DataverseMappingException {
		final Metadata metadata = readMetadata("agri");

		// conversion Metadata -> DatasetMetadataBlock
		DatasetMetadataBlock datasetMetadataBlock = metadataBLockHelper.metadataToDatasetMetadataBlock(metadata);
		Assertions.assertNotNull(datasetMetadataBlock);

		// conversion DatasetMetadataBlock -> Metadata
		Metadata metadataGetted = metadataBLockHelper.datasetMetadataBlockToMetadata(datasetMetadataBlock,
				metadata.getDataverseDoi());
		Assertions.assertNotNull(metadataGetted);

		RudiAssertions.assertThat(metadataGetted).isJsonEqualTo(metadata);
	}

	@Test
	void testUpdateDataset() throws DataverseAPIException, IOException {

		// création du jeu de données à mettre à jour
		final List<Media> medias = jsonResourceReader.readList("metadata/available_format/available_format_for_update.json", Media.class);
		final Metadata metadataToUpdate = readMetadata("jdd_to_update")
				.theme("agriculture biologique")
				.resourceTitle("JDD ouvert mise à jour effectué")
				.addKeywordsItem("riz")
				.availableFormats(medias);
		createDataset(metadataToUpdate);

		// mise à jour
		Metadata metadataUpdated = datasetService.updateDataset(metadataToUpdate);

		Assertions.assertEquals("agriculture biologique", metadataUpdated.getTheme());
		Assertions.assertEquals("JDD ouvert mise à jour effectué", metadataUpdated.getResourceTitle());
		Assertions.assertEquals(2, metadataUpdated.getKeywords().size());
		Assertions.assertTrue(metadataUpdated.getKeywords().contains("riz"));
		Assertions.assertEquals(1, metadataUpdated.getAvailableFormats().size());
		Assertions.assertTrue(metadataUpdated.getAvailableFormats().stream()
				.anyMatch(media -> media.getMediaId().equals(UUID.fromString("7eafd872-5c1e-47c2-ac57-c07926fe482e"))));
	}

	/**
	 * RUDI-961 : Erreur dans la recherche plein texte
	 */
	@ParameterizedTest(name = "{0}")
	@ValueSource(strings = {
			"séparateurs",
			"09/09/2021",
			"2021-09-20",
			"31.91",
	})
	void searchDatasetsFreeTextFieldResourceTitle(final String freeText) throws DataverseAPIException, IOException {
		searchDatasetsFreeText(freeText);
	}

	private void searchDatasetsFreeText(String freeText) throws IOException, DataverseAPIException {

		// On ajoute un autre JDD pour être sûr que seul le JDD souhaité remonte
		createDataset(readMetadata("jdd-sans-mots-communs-avec-jdd-avec-separateurs"));

		final Metadata metadata = readMetadata("jdd-avec-separateurs");
		final String dataverseDoi = createDataset(metadata);

		final DatasetSearchCriteria criteria = new DatasetSearchCriteria().offset(0).limit(100)
				.freeText(freeText);
		final MetadataList metadataList = datasetService.searchDatasets(criteria, Collections.emptyList()).getMetadataList();

		assertThat(metadataList.getItems()).as("On retrouve le JDD attendu").extracting("dataverseDoi").contains(dataverseDoi);
	}

	/**
	 * RUDI-961 : Erreur dans la recherche plein texte
	 */
	@ParameterizedTest(name = "{0}")
	@ValueSource(strings = {
			"Résumé court",
			"français"
	})
	void searchDatasetsFreeTextFieldAbstractTextFr(final String freeText) throws DataverseAPIException, IOException {
		searchDatasetsFreeText(freeText);
	}

	/**
	 * RUDI-961 : Erreur dans la recherche plein texte
	 */
	@ParameterizedTest(name = "{0}")
	@ValueSource(strings = {
			"English short"
	})
	void searchDatasetsFreeTextFieldAbstractTextEn(final String freeText) throws DataverseAPIException, IOException {
		searchDatasetsFreeText(freeText);
	}

	/**
	 * RUDI-961 : Erreur dans la recherche plein texte
	 */
	@ParameterizedTest(name = "{0}")
	@ValueSource(strings = {
			"31/08+",
			"09/09/2021",
	})
	void searchDatasetsFreeTextSpecialCharacters(final String freeText) throws DataverseAPIException, IOException {
		searchDatasetsFreeText(freeText);
	}

	/**
	 * RUDI-961 : Erreur dans la recherche plein texte
	 */
	@ParameterizedTest(name = "{0}")
	@ValueSource(strings = {
			"911992100 | 09/09/2021",
			"911992100 09/09/2021",
	})
	void searchDatasetsFreeTextSeparators(final String freeText) throws DataverseAPIException, IOException {
		searchDatasetsFreeText(freeText);
	}

	@Test
	void searchDatasets_doiProches() throws DataverseAPIException, IOException {

		val existingDoi = "10.1594/PANGAEA.726855";
		val nonExistingDoi = "10.1594/PANGAEA.726900"; // seuls les 3 derniers chiffres changent

		final Metadata metadata = readMetadata("existing_dataset");
		createDataset(metadata);

		final DatasetSearchCriteria existingCriteria = new DatasetSearchCriteria();
		existingCriteria.doi(existingDoi);
		final MetadataListFacets existingResult = datasetService.searchDatasets(existingCriteria, Collections.emptyList());

		assertThat(existingResult.getMetadataList().getTotal()).isEqualTo(1);
		assertThat(existingResult.getMetadataList().getItems())
				.extracting(Metadata::getDataverseDoi)
				.containsExactly(metadata.getDataverseDoi());


		final DatasetSearchCriteria nonExistingDoiCriteria = new DatasetSearchCriteria();
		nonExistingDoiCriteria.doi(nonExistingDoi);
		final MetadataListFacets nonExistingResult = datasetService.searchDatasets(nonExistingDoiCriteria, Collections.emptyList());

		assertThat(nonExistingResult.getMetadataList().getTotal()).isZero();
		assertThat(nonExistingResult.getMetadataList().getItems()).isEmpty();

	}

	@Test
	void searchDatasets_localIdProches() throws DataverseAPIException, IOException {

		val existingLocalId = "2020.11-Laennec-AQMO-air quality sensors measures";
		val nonExistingLocalId = "2020.11-Laennec-AQMO-air quality sensors measure"; // seul un caractère change

		final Metadata metadata = readMetadata("existing_dataset");
		createDataset(metadata);

		final DatasetSearchCriteria existingCriteria = new DatasetSearchCriteria();
		existingCriteria.localId(existingLocalId);
		final MetadataListFacets existingResult = datasetService.searchDatasets(existingCriteria, Collections.emptyList());

		assertThat(existingResult.getMetadataList().getTotal()).isEqualTo(1);
		assertThat(existingResult.getMetadataList().getItems())
				.extracting(Metadata::getDataverseDoi)
				.containsExactly(metadata.getDataverseDoi());


		final DatasetSearchCriteria nonExistingDoiCriteria = new DatasetSearchCriteria();
		nonExistingDoiCriteria.localId(nonExistingLocalId);
		final MetadataListFacets nonExistingResult = datasetService.searchDatasets(nonExistingDoiCriteria, Collections.emptyList());

		assertThat(nonExistingResult.getMetadataList().getTotal()).isZero();
		assertThat(nonExistingResult.getMetadataList().getItems()).isEmpty();

	}

	@Test
	void searchDatasets_globalIdProches() throws DataverseAPIException, IOException {

		final Metadata metadata = readMetadata("existing_dataset");
		createDataset(metadata);

		val existingGlobalId = metadata.getGlobalId();
		val nonExistingGlobalId = UUIDUtils.eraseOnlyUUIDSegment(1, existingGlobalId); // seul un groupe de chiffres change

		final DatasetSearchCriteria existingCriteria = new DatasetSearchCriteria();
		existingCriteria.globalIds(Collections.singletonList(existingGlobalId));
		final MetadataListFacets existingResult = datasetService.searchDatasets(existingCriteria, Collections.emptyList());

		assertThat(existingResult.getMetadataList().getTotal()).isEqualTo(1);
		assertThat(existingResult.getMetadataList().getItems())
				.extracting(Metadata::getDataverseDoi)
				.containsExactly(metadata.getDataverseDoi());


		final DatasetSearchCriteria nonExistingCriteria = new DatasetSearchCriteria();
		nonExistingCriteria.globalIds(Collections.singletonList(nonExistingGlobalId));
		final MetadataListFacets nonExistingResult = datasetService.searchDatasets(nonExistingCriteria, Collections.emptyList());

		assertThat(nonExistingResult.getMetadataList().getTotal()).isZero();
		assertThat(nonExistingResult.getMetadataList().getItems()).isEmpty();

	}


}
