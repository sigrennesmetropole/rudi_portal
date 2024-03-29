package org.rudi.microservice.kalim.service.helper.apim;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.facet.apimaccess.bean.APILifecycleStatusState;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.apimaccess.service.APIsService;
import org.rudi.facet.kaccess.bean.Media;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.MetadataAccessCondition;
import org.rudi.facet.organization.helper.OrganizationHelper;
import org.rudi.facet.providers.bean.NodeProvider;
import org.rudi.facet.providers.bean.Provider;
import org.rudi.facet.providers.helper.ProviderHelper;
import org.rudi.microservice.kalim.service.KalimSpringBootTest;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import lombok.val;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Test RUDI-1300
 */
@KalimSpringBootTest
class APIManagerHelperUnmockedAPIManagerIT {

	@Autowired
	private APIManagerHelper apiManagerHelper;

	@Autowired
	private APIsService apIsService;

	@Autowired
	private JsonResourceReader jsonResourceReader;

	@MockBean
	private ProviderHelper providerHelper;

	@MockBean
	private OrganizationHelper organizationHelper;

	/**
	 * JDD et medias chargés et potentiellement créés dans WSO2 (donc à nettoyer après chaque test)
	 */
	private List<IntegrationRequestEntity> requestsToTearDown = new ArrayList<>();

	@AfterEach
	void tearDown() throws APIManagerException {
		for (final IntegrationRequestEntity request : requestsToTearDown) {
			apiManagerHelper.deleteAllAPI(request);
		}
		requestsToTearDown = new ArrayList<>();
	}

	@Test
	@Disabled
	void updateAPI_unarchiveAPI() throws IOException, APIManagerException {

		val media1 = jsonResourceReader.read("media/media1.json", Media.class);
		media1.setMediaId(UUID.randomUUID());
		val media2 = jsonResourceReader.read("media/media2.json", Media.class);
		media2.setMediaId(UUID.randomUUID());

		val nodeProvider = new NodeProvider();
		val provider = new Provider()
				.uuid(UUID.randomUUID())
				.code("PROVIDER_TEST_APIM");
		when(providerHelper.requireNodeProviderByUUID(any())).thenReturn(nodeProvider);
		when(providerHelper.requireProviderByNodeProviderUUID(any())).thenReturn(provider);

		// 1. Créer un JDD avec 2 média => 2 API créées dans WSO2
		final IntegrationRequestEntity request = createRequestFrom(nodeProvider);
		val metadataWith2Medias = new Metadata()
				.globalId(UUID.randomUUID())
				.accessCondition(new MetadataAccessCondition())
				.availableFormats(Arrays.asList(media1, media2));
		requestsToTearDown.add(request);
		final var createdApiList = apiManagerHelper.createAPI(request, metadataWith2Medias);

		assertThat(createdApiList)
				.as("On a bien créé les deux API pour ce JDD")
				.hasSize(2);
		final var api1 = createdApiList.get(0);
		final var api2 = createdApiList.get(1);


		// 2. Modifier le JDD pour supprimer le 2e média => la 2e API est archivée (aucun changement sur la 1ère)
		val metadataWith1Media = new Metadata()
				.globalId(metadataWith2Medias.getGlobalId())
				.accessCondition(metadataWith2Medias.getAccessCondition())
				.availableFormats(Collections.singletonList(media1));
		apiManagerHelper.updateAPI(request, metadataWith1Media, metadataWith2Medias);

		assertThat(apIsService.getAPI(api1.getId())).as("La 1ère API n'a pas été modifiée")
				.isEqualToComparingFieldByField(api1);
		assertThat(apIsService.getAPI(api2.getId())).as("La 2e API a été archivée")
				.isEqualToIgnoringGivenFields(api2, "lifeCycleStatus", "lastUpdatedTime")
				.hasFieldOrPropertyWithValue("lifeCycleStatus", APILifecycleStatusState.BLOCKED.getValue());


		// 3. Modifier le JDD pour recréer le 2e média => la 2e API est désarchivée (aucun changement sur la 1ère)
		apiManagerHelper.updateAPI(request, metadataWith2Medias, metadataWith1Media);

		assertThat(apIsService.getAPI(api1.getId())).as("La 1ère API n'a pas été modifiée")
				.isEqualToComparingFieldByField(api1);
		assertThat(apIsService.getAPI(api2.getId())).as("La 2e API a été désarchivée")
				.isEqualToIgnoringGivenFields(api2, "lastUpdatedTime");

	}

	@Nonnull
	private IntegrationRequestEntity createRequestFrom(NodeProvider nodeProvider) {
		val createRequest = new IntegrationRequestEntity();
		createRequest.setNodeProviderId(nodeProvider.getUuid());
		return createRequest;
	}

}
