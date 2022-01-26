package org.rudi.microservice.kos.service.skos;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.microservice.kos.core.bean.Language;
import org.rudi.microservice.kos.core.bean.SimpleSkosConcept;
import org.rudi.microservice.kos.core.bean.SkosConcept;
import org.rudi.microservice.kos.core.bean.SkosConceptLabel;
import org.rudi.microservice.kos.core.bean.SkosConceptSearchCriteria;
import org.rudi.microservice.kos.core.bean.SkosRelationType;
import org.rudi.microservice.kos.core.bean.SkosScheme;
import org.rudi.microservice.kos.core.bean.SkosSchemeSearchCriteria;
import org.rudi.microservice.kos.service.SpringBootTestApplication;
import org.rudi.microservice.kos.storage.dao.skos.SkosConceptDao;
import org.rudi.microservice.kos.storage.dao.skos.SkosSchemeDao;
import org.rudi.microservice.kos.storage.entity.skos.SkosConceptEntity;
import org.rudi.microservice.kos.storage.entity.skos.SkosSchemeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Class de test de la couche service de domaina
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { SpringBootTestApplication.class })
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SkosSchemeServiceTest {

	@Autowired
	private SkosSchemeService skosSchemeService;

	@Autowired
	private SkosConceptService skosConceptService;

	@Autowired
	private SkosSchemeDao skosSchemeDao;

	@Autowired
	private SkosConceptDao skosConceptDao;

	private static SkosScheme skosSchemeLicence;
	private static SkosScheme skosSchemeKeyword;
	private static SkosConcept skosConcept;

	private static final String SKOS_SCHEME_LICENCE_CODE = "scheme-licence";
	private static final String SKOS_SCHEME_KEYWORD_CODE = "scheme-keyword";

	@BeforeAll
	static void initData(@Autowired JsonResourceReader jsonResourceReader) throws IOException {
		skosSchemeLicence = jsonResourceReader.read("json/test/skosScheme.json", SkosScheme.class);
		skosSchemeKeyword = jsonResourceReader.read("json/skosScheme_keyword.json", SkosScheme.class);
		skosConcept = jsonResourceReader.read("json/test/skosConcept.json", SkosConcept.class);
	}

	@Test
	@Order(1)
	public void testCreateSkosScheme() {
		// création du schema licence avec toutes la grappe
		skosSchemeLicence = skosSchemeService.createSkosScheme(skosSchemeLicence);
		assertNotNull(skosSchemeLicence.getSchemeId());

		// création du schema keyword avec toutes la grappe
		LocalDateTime localDateTime = LocalDateTime.now().minusDays(1);
		skosSchemeKeyword.setOpeningDate(LocalDateTime.now().minusDays(1));
		skosSchemeKeyword.setClosingDate(LocalDateTime.now().plusDays(1));
		skosSchemeKeyword = skosSchemeService.createSkosScheme(skosSchemeKeyword);
		assertNotNull(skosSchemeKeyword.getSchemeId());
	}

	@Test
	@Order(2)
	@Transactional
	public void testGetSkosScheme() {
		skosSchemeLicence = skosSchemeService.getSkosScheme(skosSchemeLicence.getSchemeId());

		// vérification des scheme labels
		assertTrue(CollectionUtils.isNotEmpty(skosSchemeLicence.getSchemeLabels()));

		SkosSchemeEntity skosSchemeEntity = skosSchemeDao.findByUUID(skosSchemeLicence.getSchemeId());

		// vérification des tops concepts
		assertTrue(CollectionUtils.isNotEmpty(skosSchemeEntity.getTopConcepts()));
		assertEquals(2, skosSchemeEntity.getTopConcepts().size());
		SkosConceptEntity skosConceptEntity = skosConceptDao.findByCodeAndOfScheme_Uuid("free-software-licence", skosSchemeLicence.getSchemeId());
		assertNotNull(skosConceptEntity);
		assertTrue(CollectionUtils.isNotEmpty(skosConceptEntity.getRelationConcepts()));
	}

	@Test
	@Order(3)
	public void testSearchSkosScheme() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<SkosScheme> skosSchemePage = skosSchemeService.searchSkosSchemes(SkosSchemeSearchCriteria.builder().active(true).build(), pageable);
		assertThat(skosSchemePage.getContent()).isNotEmpty();
		assertThat(skosSchemePage.getContent()).anyMatch(skosScheme -> skosScheme.getSchemeCode().equals(SKOS_SCHEME_KEYWORD_CODE));

		Page<SkosScheme> skosSchemePage2 = skosSchemeService.searchSkosSchemes(SkosSchemeSearchCriteria.builder().active(false).build(), pageable);
		assertThat(skosSchemePage2.getContent()).isNotEmpty();
		assertThat(skosSchemePage2.getContent()).anyMatch(skosScheme -> skosScheme.getSchemeCode().equals(SKOS_SCHEME_LICENCE_CODE));
	}

	@Test
	@Order(3)
	public void testSearchSkosConcept() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<SimpleSkosConcept> simpleSkosConceptPage = skosConceptService.searchSkosConcepts(SkosConceptSearchCriteria.builder()
				.lang(Language.FR)
				.text("Domaine Public")
				.labels(Collections.singletonList(SkosConceptLabel.PREFERRED))
				.codesScheme(Collections.singletonList("scheme-licence"))
				.types(Arrays.asList(SkosRelationType.NARROWING, SkosRelationType.SIBLING))
				.build(), pageable);
		assertTrue(CollectionUtils.isNotEmpty(simpleSkosConceptPage.getContent()));
		assertEquals(4, simpleSkosConceptPage.getTotalElements());
		assertTrue(simpleSkosConceptPage.getContent().stream().anyMatch(simpleSkosConcept -> simpleSkosConcept.getConceptCode().equals("licence_MIT")));
		assertTrue(simpleSkosConceptPage.getContent().stream().anyMatch(simpleSkosConcept -> simpleSkosConcept.getConceptCode().equals("licence_pub-domain-CC0_narw_1")));
		assertTrue(simpleSkosConceptPage.getContent().stream().anyMatch(simpleSkosConcept -> simpleSkosConcept.getConceptCode().equals("licence_pub-domain-CC0_narw_2")));
		assertTrue(simpleSkosConceptPage.getContent().stream().anyMatch(simpleSkosConcept -> simpleSkosConcept.getConceptCode().equals("licence_public-domain-CC0")));


		Page<SimpleSkosConcept> simpleSkosConceptPage2 = skosConceptService.searchSkosConcepts(SkosConceptSearchCriteria.builder()
				.lang(Language.FR)
				.text("Licence Apache v2")
				.labels(Collections.singletonList(SkosConceptLabel.PREFERRED))
				.codesScheme(Collections.singletonList("scheme-licence"))
				.types(Collections.singletonList(SkosRelationType.BROADER))
				.build(), pageable);
		assertTrue(CollectionUtils.isNotEmpty(simpleSkosConceptPage2.getContent()));
		assertEquals(2, simpleSkosConceptPage2.getTotalElements());
		assertTrue(simpleSkosConceptPage2.getContent().stream().anyMatch(simpleSkosConcept -> simpleSkosConcept.getConceptCode().equals("licence_Apache-2.0")));
		assertTrue(simpleSkosConceptPage2.getContent().stream().anyMatch(simpleSkosConcept -> simpleSkosConcept.getConceptCode().equals("open-source-licence")));


		Page<SimpleSkosConcept> simpleSkosConceptPage3 = skosConceptService.searchSkosConcepts(SkosConceptSearchCriteria.builder()
				.lang(Language.FR)
				.text("GPL*")
				.labels(Collections.singletonList(SkosConceptLabel.ALTERNATE))
				.codesScheme(Collections.singletonList("scheme-licence"))
				.build(), pageable);
		assertTrue(CollectionUtils.isNotEmpty(simpleSkosConceptPage3.getContent()));
		assertEquals(1, simpleSkosConceptPage3.getTotalElements());


	}

	@Test
	@Order(3)
	@DisplayName("recherche avec la langue en_US, on a le résultat avec traduction langue en, car il n'y a pas de traduction " +
			"avec la langue en_US")
	public void testSearchSkosConceptWithCompoundLanguageEnUS() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<SimpleSkosConcept> simpleSkosConceptPage = skosConceptService.searchSkosConcepts(SkosConceptSearchCriteria.builder()
				.lang(Language.EN_US)
				.codes(Collections.singletonList("open-source-licence"))
				.labels(Collections.singletonList(SkosConceptLabel.PREFERRED))
				.codesScheme(Collections.singletonList("scheme-licence"))
				.build(), pageable);
		assertTrue(CollectionUtils.isNotEmpty(simpleSkosConceptPage.getContent()));
		assertEquals(1, simpleSkosConceptPage.getTotalElements());
		assertEquals("Open-Source Licence", simpleSkosConceptPage.getContent().get(0).getText());
	}

	@Test
	@Order(3)
	@DisplayName("recherche avec la langue it_IT, on a le résultat avec traduction en langue par défaut, car il n'y a pas de " +
			"traduction avec les langue it_IT et it")
	public void testSearchSkosConceptWithCompoundLanguageItIT() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<SimpleSkosConcept> simpleSkosConceptPage = skosConceptService.searchSkosConcepts(SkosConceptSearchCriteria.builder()
				.lang(Language.IT_IT)
				.codes(Collections.singletonList("open-source-licence"))
				.labels(Collections.singletonList(SkosConceptLabel.PREFERRED))
				.codesScheme(Collections.singletonList("scheme-licence"))
				.build(), pageable);
		assertTrue(CollectionUtils.isNotEmpty(simpleSkosConceptPage.getContent()));
		assertEquals(1, simpleSkosConceptPage.getTotalElements());
		assertEquals("Licence Open-Source", simpleSkosConceptPage.getContent().get(0).getText());
	}

	@Test
	@Order(4)
	public void testCreateSkosConcept() {
		skosConcept = skosSchemeService.createSkosConcept(skosSchemeLicence.getSchemeId(), skosConcept, true);

		SkosConcept skosConceptGet = skosSchemeService.getSkosConcept(skosSchemeLicence.getSchemeId(), skosConcept.getConceptId());
		assertTrue(CollectionUtils.isNotEmpty(skosConceptGet.getNarrowerConcepts()));
		assertEquals(2, skosConceptGet.getNarrowerConcepts().size());
	}

	@Test
	@Order(5)
	@Transactional
	public void testSkosSchemeTopConceptAdded() {
		SkosSchemeEntity skosSchemeEntity = skosSchemeDao.findByUUID(skosSchemeLicence.getSchemeId());
		assertEquals(3, skosSchemeEntity.getTopConcepts().size());
	}

	@Test
	@Order(5)
	public void testSearchSkosConceptOfSkosScheme() {
		List<SkosConcept> skosConcepts = skosSchemeService.getTopConcepts(skosSchemeLicence.getSchemeId());
		assertTrue(CollectionUtils.isNotEmpty(skosConcepts));
		assertEquals(3, skosConcepts.size());
	}

	@Test
	@Order(6)
	public void testDeleteSkosConcept() {
		long nbSkosConcept = skosConceptDao.count();
		skosSchemeService.deleteSkosConcept(skosSchemeLicence.getSchemeId(), skosConcept.getConceptId());
		assertEquals(nbSkosConcept - 1, skosConceptDao.count());
	}

	@Test
	@Order(7)
	public void testDeleteSkosScheme() {
		long nbSkosScheme = skosSchemeDao.count();
		skosSchemeService.deleteSkosScheme(skosSchemeLicence.getSchemeId());
		skosSchemeService.deleteSkosScheme(skosSchemeKeyword.getSchemeId());

		assertEquals(nbSkosScheme - 2, skosSchemeDao.count());
	}
}
