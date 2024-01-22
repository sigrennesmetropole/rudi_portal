package org.rudi.facet.apimaccess.api;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.rudi.common.test.RudiAssertions.assertThat;

class ContentTypeUtilsUT {

	@Test
	void TestOneParameter(){
		String mediaTypeValue = "application/json;charset=\"utf-8\"";
		MediaType mediaType = ContentTypeUtils.normalize(mediaTypeValue);
		MediaType awaitedMediaType = MediaType.APPLICATION_JSON_UTF8;
		assertThat(mediaType).as("Le résultat doit être un Json en charset UTF-8").isEqualTo(awaitedMediaType);
	}

	@Test
	void TestNoParameter(){
		String mediaTypeValue = "image/png";
		MediaType mediaType = ContentTypeUtils.normalize(mediaTypeValue);
		MediaType awaitedMediaType = MediaType.IMAGE_PNG;
		assertThat(mediaType).as("Le résultat doit être une image en PNG").isEqualTo(awaitedMediaType);
	}

	@Test
	void TestOneParameterTypeWithPlusSign(){
		String mediaTypeValue = "application/problem+json;charset=\"UTF-8\"";
		MediaType mediaType = ContentTypeUtils.normalize(mediaTypeValue);
		MediaType awaitedMediaType = MediaType.APPLICATION_PROBLEM_JSON_UTF8;
		assertThat(mediaType).as("Le résultat doit être un JSON en UTF-8").isEqualTo(awaitedMediaType);
	}

}
