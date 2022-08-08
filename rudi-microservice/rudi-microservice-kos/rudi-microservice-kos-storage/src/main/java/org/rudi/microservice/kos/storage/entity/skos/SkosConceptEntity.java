package org.rudi.microservice.kos.storage.entity.skos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.rudi.common.storage.entity.AbstractStampedEntity;
import org.rudi.common.storage.entity.SkosConceptCodeColumn;
import org.rudi.microservice.kos.core.common.SchemaConstants;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = SkosConceptCodeColumn.TABLE_NAME, schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
@ToString
public class SkosConceptEntity extends AbstractStampedEntity {

	private static final long serialVersionUID = -1810252538296673429L;

	@Column(name = "concept_icon", nullable = false)
	private String conceptIcon;

	@Column(name = "concept_uri", nullable = false)
	private String conceptUri;

	@Column(name = "concept_role", nullable = false)
	private String conceptRole;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "skos_concept_prefered_label", schema = SchemaConstants.DATA_SCHEMA, joinColumns = @JoinColumn(name = "skos_concept_fk"), inverseJoinColumns = @JoinColumn(name = "skos_concept_translation_fk"))
	private Set<SkosConceptTranslationEntity> preferedLabels;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "skos_concept_alternate_label", schema = SchemaConstants.DATA_SCHEMA, joinColumns = @JoinColumn(name = "skos_concept_fk"), inverseJoinColumns = @JoinColumn(name = "skos_concept_translation_fk"))
	private Set<SkosConceptTranslationEntity> alternateLabels;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "skos_concept_hidden_label", schema = SchemaConstants.DATA_SCHEMA, joinColumns = @JoinColumn(name = "skos_concept_fk"), inverseJoinColumns = @JoinColumn(name = "skos_concept_translation_fk"))
	private Set<SkosConceptTranslationEntity> hiddenLabels;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "skos_concept_note", schema = SchemaConstants.DATA_SCHEMA, joinColumns = @JoinColumn(name = "skos_concept_fk"), inverseJoinColumns = @JoinColumn(name = "skos_concept_translation_fk"))
	private Set<SkosConceptTranslationEntity> scopeNote;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "skos_concept_definition", schema = SchemaConstants.DATA_SCHEMA, joinColumns = @JoinColumn(name = "skos_concept_fk"), inverseJoinColumns = @JoinColumn(name = "skos_concept_translation_fk"))
	private Set<SkosConceptTranslationEntity> conceptDefinition;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "skos_concept_example", schema = SchemaConstants.DATA_SCHEMA, joinColumns = @JoinColumn(name = "skos_concept_fk"), inverseJoinColumns = @JoinColumn(name = "skos_concept_translation_fk"))
	private Set<SkosConceptTranslationEntity> conceptExample;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "relation_concept_fk")
	private Set<SkosRelationConceptEntity> relationConcepts;

	@ManyToOne
	@JoinColumn(name = "of_scheme_fk")
	private SkosSchemeEntity ofScheme;

	public void addSkosRelation(SkosRelationConceptEntity skosRelationConceptEntity) {
		if (getRelationConcepts() == null) {
			setRelationConcepts(new HashSet<>());
		}
		getRelationConcepts().add(skosRelationConceptEntity);
	}

	public void clearSkosRelations() {
		if (getRelationConcepts() == null) {
			setRelationConcepts(new HashSet<>());
		}
		else {
			getRelationConcepts().clear();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((conceptRole == null) ? 0 : conceptRole.hashCode());
		result = prime * result + ((conceptUri == null) ? 0 : conceptUri.hashCode());
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
		SkosConceptEntity other = (SkosConceptEntity) obj;
		if (getConceptRole() == null) {
			if (other.getConceptRole() != null) {
				return false;
			}
		} else if (!getConceptRole().equals(other.getConceptRole())) {
			return false;
		}
		if (getConceptUri() == null) {
			if (other.getConceptUri() != null) {
				return false;
			}
		} else if (!getConceptUri().equals(other.getConceptUri())) {
			return false;
		}
		return true;
	}

}
