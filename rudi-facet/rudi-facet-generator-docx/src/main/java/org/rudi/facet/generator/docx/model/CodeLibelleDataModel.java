/**
 * 
 */
package org.rudi.facet.generator.docx.model;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author FNI18300
 *
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CodeLibelleDataModel {

	private String code;

	private String libelle;

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code != null ? code : StringUtils.EMPTY;
	}

	/**
	 * @param libelle the libelle to set
	 */
	public void setLibelle(String libelle) {
		this.libelle = libelle != null ? libelle : StringUtils.EMPTY;
	}

}
