/**
 * 
 */
package org.rudi.facet.generator.text.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.rudi.common.core.DocumentContent;
import org.rudi.facet.generator.exception.GenerationException;
import org.rudi.facet.generator.exception.GenerationModelNotFoundException;
import org.rudi.facet.generator.text.StarterSpringBootTestApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author fni18300
 *
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { StarterSpringBootTestApplication.class })
class TemplateGeneratorTest {

	@Autowired
	private TemplateGeneratorImpl templateGenerator;

	@Test
	void testStringTemplate() {
		TemplateStringDataModel t = new TemplateStringDataModel();
		try {
			DocumentContent d = templateGenerator.generateDocument(t);
			Assert.assertNotNull(d);
			Assert.assertNotNull(d.getFile());
			Assert.assertEquals("test.txt", d.getFileName());
			Assert.assertEquals("text/plain", d.getContentType());
			String a = FileUtils.readFileToString(d.getFile(), Charset.forName("UTF8"));
			Assert.assertEquals("rudi - 1", a);

		} catch (GenerationModelNotFoundException | GenerationException | IOException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	void testFileTemplate() {
		TemplateFileDataModel t = new TemplateFileDataModel();
		URL testResultURL = Thread.currentThread().getContextClassLoader().getResource("test-result.html");
		try {
			DocumentContent d = templateGenerator.generateDocument(t);
			Assert.assertNotNull(d);
			Assert.assertNotNull(d.getFile());
			Assert.assertEquals("test.html", d.getFileName());
			Assert.assertEquals("text/html", d.getContentType());
			Assertions.assertThat(d.getFile()).hasSameTextualContentAs(new File(testResultURL.getFile()));
		} catch (GenerationModelNotFoundException | GenerationException | IOException e) {
			Assert.fail(e.getMessage());
		}
	}

}
