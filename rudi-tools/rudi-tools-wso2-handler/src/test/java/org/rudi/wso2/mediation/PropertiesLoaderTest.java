package org.rudi.wso2.mediation;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.lang.IllegalClassException;
import org.junit.jupiter.api.Test;

import lombok.Getter;
import lombok.Setter;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PropertiesLoaderTest {
	@WSO2HandlerProperties(prefix = "sample")
	@Getter
	@Setter
	static class SamplePropertiesWithAnnotation {
		private String notOverloadedProperty = "Default value 1";
		private String overloadedProperty = "Default value 2";
	}

	@Getter
	@Setter
	static class SamplePropertiesWithoutAnnotation {
		private String notOverloadedProperty = "Default value 3";
		private String overloadedProperty = "Default value 4";
	}

	@Test
	void loadProperties() throws IOException {
		final var propertiesLoader = new PropertiesLoader();
		final var path = Path.of("src/test/resources/properties/sample.properties");
		final var properties = propertiesLoader.loadProperties(path, SamplePropertiesWithAnnotation.class);

		assertThat(properties)
				.as("Si une propriété n'est pas surchargée dans le fichier de properties, on récupère la valeur par défaut.")
				.hasFieldOrPropertyWithValue("notOverloadedProperty", "Default value 1")
		;

		assertThat(properties)
				.as("Si une propriété est surchargée dans le fichier de properties, on retrouve sa valeur.")
				.hasFieldOrPropertyWithValue("overloadedProperty", "Overloaded Value 2")
		;
	}

	@Test
	void loadProperties_withoutAnnotation() {
		final var propertiesLoader = new PropertiesLoader();
		final var path = Path.of("src/test/resources/properties/sample.properties");
		assertThatThrownBy(() -> propertiesLoader.loadProperties(path, SamplePropertiesWithoutAnnotation.class)).isInstanceOf(IllegalClassException.class);
	}

}
