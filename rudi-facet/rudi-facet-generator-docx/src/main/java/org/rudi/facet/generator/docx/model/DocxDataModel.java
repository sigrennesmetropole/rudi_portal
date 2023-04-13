/**
 *
 */
package org.rudi.facet.generator.docx.model;

import java.util.List;
import java.util.Map;

import org.rudi.facet.generator.exception.GenerationException;
import org.rudi.facet.generator.model.DataModel;

/**
 * @author FNI18300
 *
 */
public interface DocxDataModel extends DataModel {
	/**
	 *
	 * @return la liste des données portées par le modèle
	 * @throws GeneratorException
	 */
	Map<String, Object> getDataModel() throws GenerationException;

	/**
	 *
	 * @return le nom du modele
	 */
	String getModelFileName();

	/**
	 *
	 * @return la liste des champs de fusion à ajouter
	 */
	List<MetadataFieldNameDescription> getFieldMetadataNames();

}
