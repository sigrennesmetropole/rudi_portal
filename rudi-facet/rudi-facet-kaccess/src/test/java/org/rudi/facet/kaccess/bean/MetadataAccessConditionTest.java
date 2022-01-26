package org.rudi.facet.kaccess.bean;

import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rudi.common.core.json.JsonResourceReader;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MetadataAccessConditionTest {

	private final JsonResourceReader jsonResourceReader = new JsonResourceReader();
	private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Test
	void licence_standard_licence() throws IOException {
		final MetadataAccessCondition metadataAccessCondition = jsonResourceReader.read("metadata/accessCondition/min-standard_licence.json", MetadataAccessCondition.class);

		assertThat(metadataAccessCondition)
				.hasFieldOrPropertyWithValue("licence.licenceLabel", "SkosConcept.concept_code");
	}

	@Test
	void licence_custom_licence() throws IOException {
		final MetadataAccessCondition metadataAccessCondition = jsonResourceReader.read("metadata/accessCondition/min-custom_licence.json", MetadataAccessCondition.class);

		assertThat(metadataAccessCondition)
				.hasFieldOrPropertyWithValue("licence.customLicenceUri", "https://www.example.com/animals")
				.hasFieldOrPropertyWithValue("licence.customLicenceLabel",
						Arrays.asList(new DictionaryEntry().lang(Language.FR_FR).text("Animaux"),
								new DictionaryEntry().lang(Language.EN_US).text("Animals")));
	}

	@Test
	void access_condition_complet_standard_licence() throws IOException {
		final MetadataAccessCondition metadataAccessCondition = jsonResourceReader.read("metadata/accessCondition/max-standard_licence.json", MetadataAccessCondition.class);

		assertThat(metadataAccessCondition)
				.hasFieldOrPropertyWithValue("licence.licenceLabel", "SkosConcept.concept_code");
	}

	@Test
	void access_condition_complet_custom_licence() throws IOException {
		final MetadataAccessCondition metadataAccessCondition = jsonResourceReader.read("metadata/accessCondition/max-custom_licence.json", MetadataAccessCondition.class);

		assertThat(metadataAccessCondition)
				.hasFieldOrPropertyWithValue("licence.customLicenceUri", "https://www.example.com/animals")
				.hasFieldOrPropertyWithValue("licence.customLicenceLabel",
						Arrays.asList(new DictionaryEntry().lang(Language.FR_FR).text("Animaux"),
								new DictionaryEntry().lang(Language.EN_US).text("Animals")));
	}

	@Test
	@DisplayName("Test de la propriété customLicenceUri avec une url invalide")
	void access_condition_custom_licence_uri_validation() throws IOException {
		final MetadataAccessCondition metadataAccessCondition = jsonResourceReader.read("metadata/accessCondition/invalid_custom_licence_uri.json", MetadataAccessCondition.class);
		Set<ConstraintViolation<MetadataAccessCondition>> violations = validator.validate(metadataAccessCondition);
		assertThat(violations.size()).isEqualTo(1);
		assertTrue(violations.stream().anyMatch(violation -> violation.getPropertyPath() instanceof PathImpl
				&& ((PathImpl) (violation.getPropertyPath())).getLeafNode().asString().equals("customLicenceUri") // vérification que l'erreur concerne bien l'attribut customLicenceUri
				&& violation.getConstraintDescriptor().getAnnotation().annotationType().isAssignableFrom(org.hibernate.validator.constraints.URL.class))); // vérification que l'erreur est du à la validation @URL
	}

	@Test
	@DisplayName("customLicenceLabel manquant")
	void access_condition_missing_custom_licence_label() throws IOException {
		final MetadataAccessCondition metadataAccessCondition = jsonResourceReader.read("metadata/accessCondition/custom_licence-without-label.json", MetadataAccessCondition.class);

		Set<ConstraintViolation<MetadataAccessCondition>> violations = validator.validate(metadataAccessCondition);
		assertThat(violations).hasSize(1);
		assertThat(violations)
				.extracting(ConstraintViolation::getPropertyPath)
				.extracting(Path::toString)
				.containsExactly("licence.customLicenceLabel");
	}

	@Test
	@DisplayName("customLicenceLabel vide")
	void access_condition_empty_custom_licence_label() throws IOException {
		final MetadataAccessCondition metadataAccessCondition = jsonResourceReader.read("metadata/accessCondition/custom_licence-empty-label.json", MetadataAccessCondition.class);

		Set<ConstraintViolation<MetadataAccessCondition>> violations = validator.validate(metadataAccessCondition);
		assertThat(violations).hasSize(1);
		assertThat(violations)
				.extracting(ConstraintViolation::getPropertyPath)
				.extracting(Path::toString)
				.containsExactly("licence.customLicenceLabel");
	}
}
