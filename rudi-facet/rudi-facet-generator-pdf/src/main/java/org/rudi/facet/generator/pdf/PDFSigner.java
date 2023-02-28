/**
 * RUDI Portail
 */
package org.rudi.facet.generator.pdf;

import java.io.IOException;

import org.rudi.common.core.DocumentContent;
import org.rudi.facet.generator.pdf.exception.SignerException;
import org.rudi.facet.generator.pdf.model.SignatureDescription;

/**
 * @author FNI18300
 *
 */
public interface PDFSigner {

	DocumentContent sign(DocumentContent input, SignatureDescription signatureDescription)
			throws SignerException, IOException;

}
