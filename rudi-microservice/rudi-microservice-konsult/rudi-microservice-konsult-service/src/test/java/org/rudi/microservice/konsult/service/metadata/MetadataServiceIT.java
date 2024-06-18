package org.rudi.microservice.konsult.service.metadata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.acl.bean.ClientKey;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.apimaccess.service.ApplicationService;
import org.rudi.facet.dataverse.api.dataset.DatasetOperationAPI;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.bean.DatasetSearchCriteria;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.MetadataList;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.microservice.konsult.service.KonsultSpringBootTest;
import org.rudi.microservice.konsult.service.helper.APIManagerHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

/**
 * Class de test de MetadataService
 */
@KonsultSpringBootTest
class MetadataServiceIT {

	private static final JsonResourceReader JSON_RESOURCE_READER = new JsonResourceReader();
	private static final UUID firstProducerUUID = UUID.fromString("acdccf43-566b-4134-b39e-ddf46c801242");
	private static final UUID secondProducerUUID = UUID.fromString("70933baf-ae35-4c84-9efe-2447388f51ba");
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
	void testSearchMetadata_searchByProducerUUID() throws DataverseAPIException, IOException {
		String username = "rudi";
		mockUserData(username);
		DatasetSearchCriteria searchCriteria = new DatasetSearchCriteria().producerUuids(List.of(firstProducerUUID));
		MetadataList oldMetadataList = metadataService.searchMetadatas(searchCriteria);


		List<Metadata> listOK = new ArrayList<>(oldMetadataList.getItems());

		// Bon producer UUID
		final Metadata firstJddOuvert = creerJddOuvert(true);
		listOK.add(firstJddOuvert);
		final Metadata jddRestreint = creerJddRestreint();
		listOK.add(jddRestreint); // On le rajoute à la liste des jeux de données censée être remontée par le search

		// Mauvais producer UUID
		final Metadata secondJddOuvert = creerJddOuvert(false);
		MetadataList metadataList = metadataService.searchMetadatas(searchCriteria);

		assertThat(metadataList.getItems()).as("Vérifier que la liste retournée n'est pas nulle").isNotNull();
		assertThat(metadataList.getItems()).as("Vérifier que la liste retournée n'est pas vide").isNotEmpty();

		assertThat(metadataList.getItems().containsAll(listOK))
				.as("Vérifier que la liste retournée contient bien les deux bons JDDs").isTrue();
		assertThat(metadataList.getItems().size())
				.as("Vérifier que la liste retournée contient bien seulement les deux bon JDDs")
				.isEqualTo(listOK.size());

		assertThat(metadataList.getItems().contains(secondJddOuvert))
				.as("Vérifier que la liste ne contient pas le JDD ne correspondant pas au filtre.").isFalse();

		searchCriteria = new DatasetSearchCriteria().producerUuids(List.of(secondProducerUUID));
		metadataList = metadataService.searchMetadatas(searchCriteria);

		assertThat(metadataList.getItems()).as("Vérifier que la liste retournée n'est pas nulle").isNotNull();
		assertThat(metadataList.getItems()).as("Vérifier que la liste retournée n'est pas vide").isNotEmpty();

		metadataList.getItems().forEach(m -> assertEquals(secondProducerUUID, m.getProducer().getOrganizationId()));
	}

	private void supprimerJdd(Metadata jddASupprimer) throws DataverseAPIException {

		datasetOperationAPI.deleteDataset(jddASupprimer.getDataverseDoi());

	}

	private Metadata creerJddOuvert(boolean firstJdd) throws IOException, DataverseAPIException {
		String path = firstJdd ? "metadata/jdd_ouvert.json" : "metadata/jdd_ouvert_autre_organisation.json";
		final Metadata jddOuvertACreer = JSON_RESOURCE_READER.read(path, Metadata.class);
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
