/**
 * 
 */
package org.rudi.microservice.kos.service.skos;

import org.rudi.microservice.kos.core.bean.SkosConcept;
import org.rudi.microservice.kos.core.bean.SkosScheme;
import org.rudi.microservice.kos.core.bean.SkosSchemeSearchCriteria;
import org.rudi.microservice.kos.service.exception.MissingPreferredLabelForDefaultLanguageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * @author FNI18300
 *
 */
public interface SkosSchemeService {

	/**
	 * Récupère un skosScheme sans les concepts
	 *
	 * @param uuid		identifiant du skosScheme
	 * @return			SkosScheme
	 */
	SkosScheme getSkosScheme(UUID uuid);

	/**
	 * Create a SkosScheme
	 * 
	 * @param skosScheme		skosScheme à créer
	 * @return SkosScheme
	 */
	SkosScheme createSkosScheme(SkosScheme skosScheme) throws MissingPreferredLabelForDefaultLanguageException;

	/**
	 * Update a SkosScheme entity
	 * 
	 * @param skosScheme		skosScheme à créer
	 * @return SkosScheme
	 */
	SkosScheme updateSkosScheme(SkosScheme skosScheme) throws MissingPreferredLabelForDefaultLanguageException;

	/**
	 * Delete a SkosScheme entity
	 * 
	 * @param uuid		identifiant du skosScheme
	 */
	void deleteSkosScheme(UUID uuid);

	/**
	 * Récupépation d'un skosConcept à associer à un skosScheme
	 *
	 * @param skosSchemeUuid		identifiant du skosScheme
	 * @param skosConceptUuid		identifiant du skosConcept
	 * @return SkosConcept
	 */
	SkosConcept getSkosConcept(UUID skosSchemeUuid, UUID skosConceptUuid);

	/**
	 * Création d'un skosConcept à associer à un skosScheme
	 *
	 * @param skosSchemeUuid        identifiant du skosScheme
	 * @param skosConcept           skosConcept à créer
	 * @param asTopConcept			permet de savoir si les concepts ajoutés sont des tops concepts
     * @return						SkosConcept
	 */
	SkosConcept createSkosConcept(UUID skosSchemeUuid, SkosConcept skosConcept, Boolean asTopConcept) throws MissingPreferredLabelForDefaultLanguageException;

	/**
	 * Mise à jour d'un skosConcept à associer à un skosScheme
	 *
	 * @param skosSchemeUuid        identifiant du skosScheme
	 * @param skosConcept           skosConcept à mettre à jour
	 * @param asTopConcept			permet de savoir si les concepts ajoutés sont des tops concepts
	 * @return						SkosConcept
	 */
	SkosConcept updateSkosConcept(UUID skosSchemeUuid, SkosConcept skosConcept, Boolean asTopConcept) throws MissingPreferredLabelForDefaultLanguageException;

	/**
	 * Suppression d'un skosConcept à associer à un skosScheme
	 *
	 * @param skosSchemeUuid		identifiant du skosScheme
	 * @param skosConceptUuid		identifiant du skosConcept
	 */
	void deleteSkosConcept(UUID skosSchemeUuid, UUID skosConceptUuid);

	/**
	 * Recherche des skosSchemes
	 *
	 * @param skosSchemeSearchCriteria		critères de recherche
	 * @param pageable						critères de pagination
	 * @return Page<SkosScheme>
	 */
	Page<SkosScheme> searchSkosSchemes(SkosSchemeSearchCriteria skosSchemeSearchCriteria, Pageable pageable);

	/**
	 * Recherche des top concepts d'un scheme
	 *
	 * @param skosSchemeUuid			identifiant du scheme
	 * @return Page<SkosConcept>
	 */
	List<SkosConcept> getTopConcepts(UUID skosSchemeUuid);
}
