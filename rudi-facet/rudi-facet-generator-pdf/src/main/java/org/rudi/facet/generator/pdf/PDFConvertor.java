/**
 * 
 */
package org.rudi.facet.generator.pdf;

import java.io.IOException;

import org.rudi.common.core.DocumentContent;
import org.rudi.facet.generator.pdf.exception.ConvertorException;
import org.rudi.facet.generator.pdf.exception.ValidationException;
import org.rudi.facet.generator.pdf.model.ValidationResult;

/**
 * Service de conversion en PDF
 * 
 * @author FNI18300
 *
 */
public interface PDFConvertor {

	/**
	 * @param input documentContent au format docx
	 * @return documentContent au format pdf
	 * @throws ConvertorException
	 * @throws IOException
	 */
	DocumentContent convertDocx2PDF(DocumentContent input) throws ConvertorException, IOException;

	DocumentContent convertPDF2PDFA(DocumentContent input) throws ConvertorException, IOException;

	ValidationResult validatePDFA(DocumentContent document) throws ValidationException, IOException;
}
