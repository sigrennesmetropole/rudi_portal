package org.rudi.facet.generator.docx.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.rudi.common.core.DocumentContent;
import org.rudi.facet.generator.docx.DocxMerger;
import org.rudi.facet.generator.docx.SpringBootTestApplication;
import org.rudi.facet.generator.model.GenerationFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { SpringBootTestApplication.class })
class DocxMergerUT {

	@Autowired
	private DocxMerger docxMerger;

	@Test
	void generate() {

		List<DocumentContent> documents = new ArrayList<>();

		URL url1 = Thread.currentThread().getContextClassLoader().getResource("generator/DocxTest2.docx");
		File f1 = new File(url1.getFile());

		URL url2 = Thread.currentThread().getContextClassLoader().getResource("generator/DocxTest3.docx");
		File f2 = new File(url2.getFile());

		documents.add(new DocumentContent("titi.docx", GenerationFormat.DOCX.getMimeType(), f1));
		documents.add(new DocumentContent("tata.docx", GenerationFormat.DOCX.getMimeType(), f2));

		try {
			DocumentContent output = docxMerger.merge("toto", documents);
			assertEquals(output.getContentType(), GenerationFormat.DOCX.getMimeType());
			assertEquals(output.getFileName(), GenerationFormat.DOCX.generateFileName("toto"));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}

}
