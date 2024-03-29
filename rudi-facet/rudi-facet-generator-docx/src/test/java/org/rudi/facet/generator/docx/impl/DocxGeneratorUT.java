package org.rudi.facet.generator.docx.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.rudi.common.core.DocumentContent;
import org.rudi.facet.generator.docx.DocxGenerator;
import org.rudi.facet.generator.docx.SpringBootTestApplication;
import org.rudi.facet.generator.model.GenerationFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { SpringBootTestApplication.class })
class DocxGeneratorUT {

	@Autowired
	private DocxGenerator docxGenerator;

	@Test
	void generate() {
		DocxTestDataModel dataModel = new DocxTestDataModel();
		try {
			DocumentContent output = docxGenerator.generateDocument(dataModel);
			assertEquals(output.getContentType(), GenerationFormat.DOCX.getMimeType());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}

}
