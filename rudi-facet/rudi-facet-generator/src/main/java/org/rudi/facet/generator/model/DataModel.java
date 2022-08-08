/**
 * 
 */
package org.rudi.facet.generator.model;

/**
 * @author FNI18300
 *
 */
public interface DataModel {

	/**
	 * @return le nom du fichier de sortie
	 */
	String getOutputFileName();

	/**
	 * 
	 * @return le format du fichier généré
	 */
	GenerationFormat getFormat();

}
