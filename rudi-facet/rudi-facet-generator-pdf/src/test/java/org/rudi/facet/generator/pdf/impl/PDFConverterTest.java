package org.rudi.facet.generator.pdf.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.net.URL;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.rudi.common.core.DocumentContent;
import org.rudi.facet.generator.model.GenerationFormat;
import org.rudi.facet.generator.pdf.PDFConvertor;
import org.rudi.facet.generator.pdf.StarterSpringBootTestApplication;
import org.rudi.facet.generator.pdf.model.ValidationResult;
import org.rudi.facet.generator.pdf.model.ValidationResultItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { StarterSpringBootTestApplication.class })
class PDFConverterTest {

	@Autowired
	private PDFConvertor pdfConvertor;

	@Test
	void convertDocx2PDF() {
		try {
			URL url = Thread.currentThread().getContextClassLoader().getResource("generator/DocxTest.docx");

			DocumentContent input = new DocumentContent("DocxTest.docx", GenerationFormat.DOCX.getMimeType(),
					new File(url.getFile()));
			DocumentContent output = pdfConvertor.convertDocx2PDF(input);
			assertEquals(output.getContentType(), GenerationFormat.PDF.getMimeType());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void convertPDF2PDFA() {
		try {
			URL url = Thread.currentThread().getContextClassLoader().getResource("generator/PDFASource.docx");

			DocumentContent docx = new DocumentContent("PDFASource.docx", GenerationFormat.DOCX.getMimeType(),
					new File(url.getFile()));
			DocumentContent pdf = pdfConvertor.convertDocx2PDF(docx);

			DocumentContent pdfa = pdfConvertor.convertPDF2PDFA(pdf);
			assertEquals(pdfa.getContentType(), GenerationFormat.PDF.getMimeType());

			ValidationResult result = pdfConvertor.validatePDFA(pdfa);
			System.out.println("Valid: " + result.isValid());
			if (CollectionUtils.isNotEmpty(result.getItems())) {
				for (ValidationResultItem item : result.getItems()) {
					System.out.println("Item: " + item.toString());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
