/**
 * 
 */
package org.rudi.facet.bpmn.entity.form;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.rudi.common.storage.entity.AbstractLongIdEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author FNI18300
 *
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "section_definition")
public class SectionDefinitionEntity extends AbstractLongIdEntity {

	private static final long serialVersionUID = -7987865702934823269L;

	@Column(name = "name", nullable = false, length = 100)
	private String name;

	@Column(name = "label", nullable = false, length = 150)
	private String label;

	/**
	 * Contient un flux json constitué par une liste de FieldDefinition
	 */
	@Column(name = "definition", nullable = false, columnDefinition = "text", length = 4086)
	private String definition;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof SectionDefinitionEntity)) {
			return false;
		}
		SectionDefinitionEntity other = (SectionDefinitionEntity) obj;
		if (getName() == null) {
			if (other.getName() != null) {
				return false;
			}
		} else if (!getName().equals(other.getName())) {
			return false;
		}
		return true;
	}

}
