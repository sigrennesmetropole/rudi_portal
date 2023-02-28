package org.rudi.facet.generator.docx;

import java.io.IOException;

import org.rudi.common.core.DocumentContent;
import org.rudi.facet.generator.docx.model.DocxDataModel;
import org.rudi.facet.generator.exception.GenerationException;

/**
 * Service de génération des Docx
 * 
 * @author FNI18300
 *
 */
public interface DocxGenerator {

	/**
	 * @param dataModel le data model contenant le données à insérer et le nom du modèle à utiliser
	 * @return
	 * @throws DocumentGenerationException si une erreur se produit a la génération
	 * @throws IOException
	 */
	DocumentContent generateDocument(DocxDataModel dataModel) throws GenerationException, IOException;

}
