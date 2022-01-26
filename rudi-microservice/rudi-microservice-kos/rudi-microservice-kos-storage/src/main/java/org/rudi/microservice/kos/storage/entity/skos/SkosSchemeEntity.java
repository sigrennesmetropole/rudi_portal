package org.rudi.microservice.kos.storage.entity.skos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;
import org.rudi.common.storage.entity.AbstractStampedEntity;
import org.rudi.microservice.kos.core.common.SchemaConstants;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Skos entity
 */
@Entity
@Table(name = "skos_scheme", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
@ToString
public class SkosSchemeEntity extends AbstractStampedEntity {

	private static final long serialVersionUID = -6508639499690690560L;

	@Column(name = "uri", length = 255)
	private String uri;

	@Column(name = "role", length = 50)
	private String role;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "skos_scheme_fk")
	private Set<SkosSchemeTranslationEntity> schemeLabels;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "skos_scheme_top_concept", schema = SchemaConstants.DATA_SCHEMA, joinColumns = @JoinColumn(name = "skos_scheme_fk"), inverseJoinColumns = @JoinColumn(name = "top_concept_fk"))
	private Set<SkosConceptEntity> topConcepts;

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof SkosSchemeEntity)) {
			return false;
		}
		return super.equals(obj);
	}

	public SkosConceptEntity lookupSkosConcept(UUID uuid) {
		SkosConceptEntity result = null;
		if (CollectionUtils.isNotEmpty(getTopConcepts())) {
			result = getTopConcepts().stream().filter(skosConceptEntity -> skosConceptEntity.getUuid().equals(uuid)).findAny()
					.orElse(null);
		}
		return result;
	}

	public void addTopConcept(SkosConceptEntity skosConceptEntity) {
		if (getTopConcepts() == null) {
			setTopConcepts(new HashSet<>());
		}
		getTopConcepts().add(skosConceptEntity);
	}

	/**
	 * Supprime un skosConcept dans le skosScheme
	 *
	 * @param skosConceptUuid		identifiant du skosConcept à retirer
	 */
	public void removeTopConcept(UUID skosConceptUuid) {
		if (CollectionUtils.isNotEmpty(getTopConcepts())) {
			getTopConcepts().removeIf(skosConceptEntity -> skosConceptEntity.getUuid().equals(skosConceptUuid));
		}
	}

}