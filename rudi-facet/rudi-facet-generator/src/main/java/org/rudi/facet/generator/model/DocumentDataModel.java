/**
 * 
 */
package org.rudi.facet.generator.model;

import java.util.Locale;
import java.util.Map;

import org.rudi.facet.generator.exception.GenerationException;

/**
 * @author FNI18300
 *
 */
public interface DocumentDataModel extends DataModel {
	/**
	 * 
	 * @return la liste des données portées par le modèle
	 * @throws GenerationException
	 */
	Map<String, Object> getDataModel() throws GenerationException;

	/**
	 * 
	 * @return la locale du modèle
	 */
	Locale getLocale();

	/**
	 * 
	 * @return le modèle - soit un fichier soit un template inline
	 */
	String getModel();

	/**
	 * 
	 * @return vrai si c'est un modèle inline
	 */
	boolean isInlineModel();

	/**
	 * 
	 * @return vrai si c'est un fichier
	 */
	boolean isFileModel();

}
