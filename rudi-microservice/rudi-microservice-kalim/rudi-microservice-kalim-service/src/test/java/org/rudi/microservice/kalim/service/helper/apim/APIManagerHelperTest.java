package org.rudi.microservice.kalim.service.helper.apim;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rudi.facet.apimaccess.bean.API;
import org.rudi.facet.apimaccess.bean.APIInfo;
import org.rudi.facet.apimaccess.bean.APIList;
import org.rudi.facet.apimaccess.bean.APIWorkflowResponse;
import org.rudi.facet.apimaccess.bean.ApplicationAPISubscription;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.apimaccess.service.APIsService;
import org.rudi.facet.apimaccess.service.ApplicationService;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.providers.bean.NodeProvider;
import org.rudi.facet.providers.bean.Provider;
import org.rudi.facet.providers.helper.ProviderHelper;
import org.rudi.microservice.kalim.service.SpringBootTestApplication;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.rudi.microservice.kalim.service.KalimTestConfigurer.initMetadataWithName;

@SpringBootTest(classes = { SpringBootTestApplication.class })
class APIManagerHelperTest {

	@Autowired
	private APIManagerHelper apiManagerHelper;

	@MockBean
	private APIsService apIsService;

	@MockBean
	private ApplicationService applicationService;

	@MockBean
	private ProviderHelper providerHelper;

	// Le JDD de base
	private Metadata initialMetadata;

	@BeforeEach
	void initTestData() throws IOException {

		// Init des données pour les tests ATTENTION le JDD de départ qui est "modifié" est basé sur le fichier
		// "create-ok.json" s'il change les TU doivent être MAJ
		initialMetadata = initMetadataWithName("create-ok.json");

		// Mock du node provider
		when(providerHelper.getProviderByNodeProviderUUID(any())).thenReturn(createMockedProvider());
		when(providerHelper.getNodeProviderByUUID(any())).thenReturn(createMockedNodeProvider());
	}

	/**
	 * Mock la création de l'API dans WSO2
	 * @return l'API sensée être créée
	 */
	private API createMockedApiWsO2() {
		API api = new API();
		api.setId("Well created");
		api.setName("Well named");
		api.setDescription("Well described");
		return api;
	}

	/**
	 * Mock la souscription de l'API dans WSO2
	 * @return l'API sensée être créée
	 */
	private ApplicationAPISubscription subscribeToMockedApiWsO2() {
		return new ApplicationAPISubscription();
	}

	/**
	 * Mock de l'appel vers le changement de statut de l'API
	 * @return objet métier
	 */
	private APIWorkflowResponse changeApiLifeCycleMockWsO2() {
		return new APIWorkflowResponse();
	}

	/**
	 * Mock de l'appel vers l'update de l'API
	 * @return objet métier
	 */
	private API updateApiMockWso2() {
		return new API();
	}

	/**
	 * Mock d'un node provider
	 * @return un node provider mocké
	 */
	private NodeProvider createMockedNodeProvider() {
		return new NodeProvider();
	}

	/**
	 * Mock d'un provider
	 * @return un provider mocké
	 */
	private Provider createMockedProvider() {
		return new Provider();
	}

	/**
	 * Mock de la liste des APIs cherchées
	 * @return un APIList mocké
	 */
	private APIList createMockedApiList() {
		List<APIInfo> infos = new ArrayList<>();
		infos.add(new APIInfo());
		APIList list = new APIList();
		list.setList(infos);
		return list;
	}

	@Test
	void test_updateApi_createApi_OK() throws IOException, APIManagerException {

		// On veut que quand on demande a créer une API dans WSO2 ça marche
		when(apIsService.createAPI(any())).thenReturn(createMockedApiWsO2());

		// On veut que quand on demande à souscrire à une API dans WSO2 ça marche
		when(applicationService.subscribeAPIToDefaultUserApplication(any(), any())).thenReturn(subscribeToMockedApiWsO2());

		// On crée la nouvelle version du JDD
		Metadata newMetadata = initMetadataWithName("update-media-add-media-ok.json");

		// On fait la modif
		apiManagerHelper.updateAPI(new IntegrationRequestEntity(), newMetadata, initialMetadata);

		// On veut vérifier que l'action de création est bien demandée dans WSO2
		verify(apIsService, atLeast(1)).createAPI(any());

		// On veut vérifier que l'action de souscription à l'API est bien demandée dans WSO2
		verify(applicationService, atLeast(1)).subscribeAPIToDefaultUserApplication(any(), any());
	}

	@Test
	void test_updateApi_deleteApi_OK() throws IOException, APIManagerException {

		// On veut que quand on demande a "supprimer" CAD désactiver une API dans WSO2 ça marche
		when(apIsService.updateAPILifecycleStatus(any(), any())).thenReturn(changeApiLifeCycleMockWsO2());

		// On veut que quand on cherche les infos sur l'API a supprimer dans WSO2 ça réponde
		when(apIsService.searchAPI(any())).thenReturn(createMockedApiList());

		// On crée la nouvelle version du JDD
		Metadata newMetadata = initMetadataWithName("update-media-delete-media-ok.json");

		// On fait la modif
		apiManagerHelper.updateAPI(new IntegrationRequestEntity(), newMetadata, initialMetadata);

		// On veut vérifier que l'action de "suppression" est bien demandée dans WSO2
		verify(apIsService, atLeast(1)).updateAPILifecycleStatus(any(), any());
	}

	@Test
	void test_updateApi_updatingApi_OK() throws IOException, APIManagerException {

		// On veut que quand on demande a "MAJ" dans WSO2 ça marche
		when(apIsService.updateAPIByName(any())).thenReturn(updateApiMockWso2());

		// On crée la nouvelle version du JDD
		Metadata newMetadata = initMetadataWithName("update-media-update-media-ok.json");

		// On fait la modif
		apiManagerHelper.updateAPI(new IntegrationRequestEntity(), newMetadata, initialMetadata);

		// On veut vérifier que l'action de "modification" est bien demandée dans WSO2
		verify(apIsService, atLeast(1)).updateAPIByName(any());
	}
}
