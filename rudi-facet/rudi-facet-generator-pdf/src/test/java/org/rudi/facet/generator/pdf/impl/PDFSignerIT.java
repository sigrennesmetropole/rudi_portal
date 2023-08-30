package org.rudi.facet.generator.pdf.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.awt.Rectangle;
import java.io.File;
import java.net.URL;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.rudi.common.core.DocumentContent;
import org.rudi.facet.generator.model.GenerationFormat;
import org.rudi.facet.generator.pdf.PDFSigner;
import org.rudi.facet.generator.pdf.StarterSpringBootTestApplication;
import org.rudi.facet.generator.pdf.model.SignatureDescription;
import org.rudi.facet.generator.pdf.model.VisibleSignatureDescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { StarterSpringBootTestApplication.class })
class PDFSignerIT {

	@Autowired
	private PDFSigner pdfSigner;

	@Test
	void signSimple() {
		try {
			URL urlFile = Thread.currentThread().getContextClassLoader().getResource("generator/PDFTest.pdf");

			DocumentContent input = new DocumentContent("PDFTest.pdf", GenerationFormat.PDF.getMimeType(),
					new File(urlFile.getFile()));

			DocumentContent output = pdfSigner.sign(input,
					SignatureDescription.builder().name("Rudi").location("Rennes").reason("Consent").build());

			assertEquals(output.getContentType(), GenerationFormat.PDF.getMimeType());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}

	@Test
	void signWithImage() {
		try {
			URL urlFile = Thread.currentThread().getContextClassLoader().getResource("generator/PDFTest.pdf");

			DocumentContent input = new DocumentContent("PDFTest.pdf", GenerationFormat.PDF.getMimeType(),
					new File(urlFile.getFile()));

			URL urlImage = Thread.currentThread().getContextClassLoader().getResource("signature.png");
			DocumentContent image = new DocumentContent("signature.png", GenerationFormat.PNG.getMimeType(),
					new File(urlImage.getFile()));

			DocumentContent output = pdfSigner.sign(input,
					SignatureDescription.builder().name("Rudi").location("Rennes").reason("Consent")
							.visibleSignatureDescription(VisibleSignatureDescription.builder()
									.rectangle(new Rectangle(20, 20, 200, 100)).image(image).build())
							.build());

			assertEquals(output.getContentType(), GenerationFormat.PDF.getMimeType());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}

}
