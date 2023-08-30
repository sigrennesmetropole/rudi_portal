/**
 * 
 */
package org.rudi.facet.bpmn.entity.form;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.rudi.common.core.Ordered;
import org.rudi.common.storage.entity.AbstractLongIdEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Défini l'association entre un formulaire et une section avec l'ordre d'affichage et si la section est en lecture seule ou non
 * 
 * @author FNI18300
 *
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "form_section_definition")
public class FormSectionDefinitionEntity extends AbstractLongIdEntity implements Ordered {

	private static final long serialVersionUID = 247855276477236506L;

	@Column(name = "read_only", nullable = false)
	private boolean readOnly;

	@Column(name = "order_", nullable = false)
	private int order;

	@ManyToOne
	@JoinColumn(name = "section_definition_fk")
	private SectionDefinitionEntity sectionDefinition;

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof FormSectionDefinitionEntity)) {
			return false;
		}
		FormSectionDefinitionEntity other = (FormSectionDefinitionEntity) obj;
		return getOrder() == other.getOrder();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + getOrder();
		return result;
	}

}
