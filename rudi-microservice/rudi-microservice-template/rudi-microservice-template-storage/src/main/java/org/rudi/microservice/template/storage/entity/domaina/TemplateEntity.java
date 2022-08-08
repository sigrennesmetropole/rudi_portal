package org.rudi.microservice.template.storage.entity.domaina;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.rudi.common.storage.entity.AbstractLongIdEntity;
import org.rudi.microservice.template.core.common.SchemaConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Template entity
 */
@Entity
@Table(name = "template", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
@ToString
public class TemplateEntity extends AbstractLongIdEntity {

	private static final long serialVersionUID = -6508639499690690560L;

	@Column(name = "comment", length = 30)
	private String comment;

	/**
	 * Child entity example
	 */
	@ManyToOne
	@JoinColumn(name = "child_entity_fk", nullable = false)
	private ChildEntity childEntity;

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof TemplateEntity)) {
			return false;
		}
		return super.equals(obj);
	}

}
