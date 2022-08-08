package org.rudi.common.core.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

public class DefaultJackson2ObjectMapperBuilder extends Jackson2ObjectMapperBuilder {
	public DefaultJackson2ObjectMapperBuilder() {
		this
				.modules(
						new Jdk8Module(),
						new JavaTimeModule(),
						new ParameterNamesModule(),
						new JsonNullableModule()
				)
				// On évite de se retrouver avec des Dates sous forme de tableaux
				.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) // src : https://stackoverflow.com/a/60570542
				.featuresToDisable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE) // src : https://juplo.de/how-to-keep-the-time-zone-when-deserializing-a-zoneddatetime-with-jackson/
				.serializationInclusion(JsonInclude.Include.NON_EMPTY) // On ignore les objects vides
		;
	}
}
