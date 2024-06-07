package org.rudi.microservice.kos.storage.entity.skos;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.rudi.common.storage.entity.AbstractLongIdEntity;
import org.rudi.microservice.kos.core.bean.SkosRelationType;
import org.rudi.microservice.kos.core.common.SchemaConstants;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "skos_relation_concept", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
@ToString
public class SkosRelationConceptEntity extends AbstractLongIdEntity {

	private static final long serialVersionUID = 5513110316765305403L;

	@Column(name = "type", length = 25)
	@Enumerated(EnumType.STRING)
	private SkosRelationType type;

	@ManyToOne
	@JoinColumn(name = "skos_concept_fk")
	private SkosConceptEntity target;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		SkosRelationConceptEntity other = (SkosRelationConceptEntity) obj;
		if (getTarget() == null) {
			if (other.getTarget() != null) {
				return false;
			}
		} else if (!getTarget().equals(other.getTarget())) {
			return false;
		}
		return getType() == other.getType();
	}
}
