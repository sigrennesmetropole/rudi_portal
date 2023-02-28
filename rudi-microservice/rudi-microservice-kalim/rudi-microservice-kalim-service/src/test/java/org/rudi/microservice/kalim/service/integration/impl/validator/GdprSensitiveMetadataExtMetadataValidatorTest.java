package org.rudi.microservice.kalim.service.integration.impl.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rudi.facet.dataset.bean.InterfaceContract;
import org.rudi.facet.kaccess.bean.Connector;
import org.rudi.facet.kaccess.bean.Media;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.MetadataAccessCondition;
import org.rudi.facet.kaccess.bean.MetadataAccessConditionConfidentiality;
import org.rudi.facet.kaccess.bean.MetadataExtMetadata;
import org.rudi.facet.kaccess.bean.MetadataExtMetadataExtSelfdata;
import org.rudi.facet.kaccess.bean.SelfdataContent;
import org.rudi.facet.kaccess.helper.selfdata.SelfdataMediaHelper;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.rudi.microservice.kalim.service.IntegrationError.ERR_107;
import static org.rudi.microservice.kalim.service.IntegrationError.ERR_305;

@ExtendWith(MockitoExtension.class)
public class GdprSensitiveMetadataExtMetadataValidatorTest {

	private GdprSensitiveMetadataExtMetadataValidator validator;

	@BeforeEach
	void setUp() {
		SelfdataMediaHelper selfdataMediaHelper = new SelfdataMediaHelper();
		validator = new GdprSensitiveMetadataExtMetadataValidator(selfdataMediaHelper);
	}

	@Test
	@DisplayName("Le validateur est Ok avec un JDD pas selfdata")
	void testValidateOkWithNotSelfdataDataset() {
		Metadata metadata = createNotSelfdataDataset();
		Set<IntegrationRequestErrorEntity> integrationRequestErrorEntities = validator.validate(metadata);
		assertThat(integrationRequestErrorEntities.size()).isEqualTo(0);
	}

	@Test
	@DisplayName("Le validateur est KO avec un JDD selfdata sans contenu")
	void testValidateKoWithSelfdataDatasetWithoutContent() {
		Metadata metadata = createSelfdataDatasetWithoutContent();
		Set<IntegrationRequestErrorEntity> integrationRequestErrorEntities = validator.validate(metadata);
		assertThat(integrationRequestErrorEntities.size()).isEqualTo(1);
		assertThat(
				integrationRequestErrorEntities.stream().filter(
						integrationRequestErrorEntity -> integrationRequestErrorEntity.getCode().equals(ERR_107.getCode())
				).findFirst().orElse(null)
		).isNotNull();
	}

	@Test
	@DisplayName("Le validateur est OK avec un JDD selfdata en mode OUT")
	void testValidateOkWithSelfdataDatasetOut() {
		Metadata metadata = createSelfdataDatasetWithoutAutomaticTreatment();
		Set<IntegrationRequestErrorEntity> integrationRequestErrorEntities = validator.validate(metadata);
		assertThat(integrationRequestErrorEntities.size()).isEqualTo(0);
	}

	@Test
	@DisplayName("Le validateur est KO avec un JDD selfdata en mode API mais sans APIs")
	void testValidateKoWithSelfdataDatasetApiMissingApis() {
		Metadata metadata = createSelfdataDatasetWithAutomaticTreatmentMissingApis();
		Set<IntegrationRequestErrorEntity> integrationRequestErrorEntities = validator.validate(metadata);
		assertThat(integrationRequestErrorEntities.size()).isEqualTo(1);
		assertThat(
				integrationRequestErrorEntities.stream().filter(
						integrationRequestErrorEntity -> integrationRequestErrorEntity.getCode().equals(ERR_305.getCode())
				).findFirst().orElse(null)
		).isNotNull();
	}

	@Test
	@DisplayName("Le validateur est KO avec un JDD selfdata en mode API mais avec les mauvaises APIs")
	void testValidateKoWithSelfdataDatasetApiWrongApis() {
		Metadata metadata = createSelfdataDatasetWithAutomaticTreatmentWrongApis();
		Set<IntegrationRequestErrorEntity> integrationRequestErrorEntities = validator.validate(metadata);
		assertThat(integrationRequestErrorEntities.size()).isEqualTo(1);
		assertThat(
				integrationRequestErrorEntities.stream().filter(
						integrationRequestErrorEntity -> integrationRequestErrorEntity.getCode().equals(ERR_305.getCode())
				).findFirst().orElse(null)
		).isNotNull();
	}

	@Test
	@DisplayName("Le validateur est KO avec un JDD selfdata en mode API mais avec pas assez d'APIs")
	void testValidateKoWithSelfdataDatasetApiNotEnoughApis() {
		Metadata metadata = createSelfdataDatasetWithAutomaticTreatmentNotEnoughApis();
		Set<IntegrationRequestErrorEntity> integrationRequestErrorEntities = validator.validate(metadata);
		assertThat(integrationRequestErrorEntities.size()).isEqualTo(1);
		assertThat(
				integrationRequestErrorEntities.stream().filter(
						integrationRequestErrorEntity -> integrationRequestErrorEntity.getCode().equals(ERR_305.getCode())
				).findFirst().orElse(null)
		).isNotNull();
	}

	@Test
	@DisplayName("Le validateur est KO avec un JDD selfdata en mode API mais avec trop d'APIs")
	void testValidateKoWithSelfdataDatasetApiTooMuchApis() {
		Metadata metadata = createSelfdataDatasetWithAutomaticTreatmentTooMuchApis();
		Set<IntegrationRequestErrorEntity> integrationRequestErrorEntities = validator.validate(metadata);
		assertThat(integrationRequestErrorEntities.size()).isEqualTo(1);
		assertThat(
				integrationRequestErrorEntities.stream().filter(
						integrationRequestErrorEntity -> integrationRequestErrorEntity.getCode().equals(ERR_305.getCode())
				).findFirst().orElse(null)
		).isNotNull();
	}

	@Test
	@DisplayName("Le validateur est OK avec un JDD selfdata en mode API et 1 API TPBC et 1 autre GDATA")
	void testValidateOkWithSelfdataDataset() {
		Metadata metadata = createSelfdataDatasetWithAutomaticTreatmentOk();
		Set<IntegrationRequestErrorEntity> integrationRequestErrorEntities = validator.validate(metadata);
		assertThat(integrationRequestErrorEntities.size()).isEqualTo(0);
	}

	private Metadata createNotSelfdataDataset() {
		return new Metadata().accessCondition(
				new MetadataAccessCondition().confidentiality(
						new MetadataAccessConditionConfidentiality().gdprSensitive(false)
				)
		);
	}

	private Metadata createSelfdataDatasetWithoutContent() {
		Metadata metadata = new Metadata()
				.accessCondition(
						new MetadataAccessCondition().confidentiality(
								new MetadataAccessConditionConfidentiality().gdprSensitive(true)
						)
				);

		MetadataExtMetadataExtSelfdata extSelfdata = new MetadataExtMetadataExtSelfdata();
		MetadataExtMetadata ext = new MetadataExtMetadata().extSelfdata(extSelfdata);
		metadata.setExtMetadata(ext);
		return metadata;
	}

	private Metadata createSelfdataDatasetWithoutAutomaticTreatment() {
		Metadata metadata = createSelfdataDatasetWithoutContent();

		SelfdataContent selfdataContent = new SelfdataContent().selfdataAccess(SelfdataContent.SelfdataAccessEnum.OUT);
		MetadataExtMetadataExtSelfdata extSelfdata = new MetadataExtMetadataExtSelfdata().extSelfdataContent(selfdataContent);
		MetadataExtMetadata ext = new MetadataExtMetadata().extSelfdata(extSelfdata);
		metadata.setExtMetadata(ext);

		return metadata;
	}

	private Metadata createSelfdataDatasetWithAutomaticTreatmentMissingApis() {
		Metadata metadata = createSelfdataDatasetWithoutAutomaticTreatment();
		metadata.getExtMetadata().getExtSelfdata().getExtSelfdataContent().setSelfdataAccess(SelfdataContent.SelfdataAccessEnum.API);
		return metadata;
	}

	private Metadata createSelfdataDatasetWithAutomaticTreatmentWrongApis() {
		Metadata metadata = createSelfdataDatasetWithAutomaticTreatmentMissingApis();
		List<Media> medias = new ArrayList<>();
		Media media1 = createMediaService("test1", "truc", "http://chose.com");
		Media media2 = createMediaService("test2", "chose", "http://chose23.com");
		medias.add(media1);
		medias.add(media2);
		metadata.setAvailableFormats(medias);
		return metadata;
	}

	private Metadata createSelfdataDatasetWithAutomaticTreatmentNotEnoughApis() {
		Metadata metadata = createSelfdataDatasetWithAutomaticTreatmentMissingApis();
		List<Media> medias = new ArrayList<>();
		Media media1 = createMediaService("test1", InterfaceContract.GENERIC_DATA.getCode(), "http://tpbc.com");
		medias.add(media1);
		metadata.setAvailableFormats(medias);
		return metadata;
	}

	private Metadata createSelfdataDatasetWithAutomaticTreatmentTooMuchApis() {
		Metadata metadata = createSelfdataDatasetWithAutomaticTreatmentMissingApis();
		List<Media> medias = new ArrayList<>();
		Media media1 = createMediaService("test1", InterfaceContract.GENERIC_DATA.getCode(), "http://gdata.com");
		Media media2 = createMediaService("test2", InterfaceContract.GENERIC_DATA.getCode(), "http://gdata.com");
		medias.add(media1);
		medias.add(media2);
		metadata.setAvailableFormats(medias);
		return metadata;
	}

	private Metadata createSelfdataDatasetWithAutomaticTreatmentOk() {
		Metadata metadata = createSelfdataDatasetWithAutomaticTreatmentMissingApis();
		List<Media> medias = new ArrayList<>();
		Media media1 = createMediaService("test1", InterfaceContract.TEMPORAL_BAR_CHART.getCode(), "http://tpbc.com");
		Media media2 = createMediaService("test2", InterfaceContract.GENERIC_DATA.getCode(), "http://gdata.com");
		medias.add(media1);
		medias.add(media2);
		metadata.setAvailableFormats(medias);
		return metadata;
	}

	// TODO prendre en pramètre InterfaceContract et pas un String
	private Media createMediaService(String name, String connectorName, String url) {
		return new Media().mediaName(name).mediaType(Media.MediaTypeEnum.SERVICE)
				.connector(new Connector().interfaceContract(connectorName).url(url));
	}
}
