/**
 * 
 */
package org.rudi.facet.bpmn.entity.form;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.apache.commons.collections4.CollectionUtils;
import org.rudi.common.storage.entity.AbstractLongIdEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Décrit un formulaire composé de sections
 * 
 * @author FNI18300
 *
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "form_definition")
public class FormDefinitionEntity extends AbstractLongIdEntity {

	private static final long serialVersionUID = -810557531798018770L;

	@Column(name = "name", nullable = false, length = 100)
	private String name;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "form_definition_fk")
	@OrderBy("order_ ASC")
	private Set<FormSectionDefinitionEntity> formSectionDefinitions;

	/**
	 * Ajout d'une section
	 * 
	 * @param formSectionDefinitionEntity
	 */
	public void addFormSectionDefinition(FormSectionDefinitionEntity formSectionDefinitionEntity) {
		if (formSectionDefinitions == null) {
			formSectionDefinitions = new HashSet<>();
		}
		formSectionDefinitions.add(formSectionDefinitionEntity);
	}

	public FormSectionDefinitionEntity lookupFormSectionDefinition(UUID uuid) {
		FormSectionDefinitionEntity result = null;
		if (CollectionUtils.isNotEmpty(getFormSectionDefinitions())) {
			result = getFormSectionDefinitions().stream().filter(f -> f.getUuid().equals(uuid)).findFirst()
					.orElse(null);
		}
		return result;
	}

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
		if (!(obj instanceof FormDefinitionEntity)) {
			return false;
		}
		FormDefinitionEntity other = (FormDefinitionEntity) obj;
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
