/**
 * 
 */
package org.rudi.facet.generator;

import java.io.IOException;

import org.rudi.common.core.DocumentContent;
import org.rudi.facet.generator.exception.GenerationException;
import org.rudi.facet.generator.exception.GenerationModelNotFoundException;

/**
 * Interface pour la génération de document
 * 
 * @author fni18300
 *
 */
public interface Generator<T> {

	/**
	 * génère un document à partir d'un modele et des données du modèle
	 * 
	 * @param dataModel le data model contenant le données à insérer et le nom du
	 *                  modèle à utiliser
	 * @throws GenerationModelNotFoundException si le modèle de document nécessaire à
	 *                                        la génération n'a pas été trouvé
	 * @throws GenerationException    si une erreur se produit a la
	 *                                        génération
	 * @throws IOException
	 */
	DocumentContent generateDocument(T dataModel)
			throws GenerationModelNotFoundException, GenerationException, IOException;
}
