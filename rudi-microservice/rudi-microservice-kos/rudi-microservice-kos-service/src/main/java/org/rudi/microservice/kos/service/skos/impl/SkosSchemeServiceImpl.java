package org.rudi.microservice.kos.service.skos.impl;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.microservice.kos.core.bean.SkosConcept;
import org.rudi.microservice.kos.core.bean.SkosRelationType;
import org.rudi.microservice.kos.core.bean.SkosScheme;
import org.rudi.microservice.kos.core.bean.SkosSchemeSearchCriteria;
import org.rudi.microservice.kos.service.exception.MissingPreferredLabelForDefaultLanguageException;
import org.rudi.microservice.kos.service.mapper.SkosConceptFullMapper;
import org.rudi.microservice.kos.service.mapper.SkosConceptMapper;
import org.rudi.microservice.kos.service.mapper.SkosSchemeMapper;
import org.rudi.microservice.kos.service.skos.SkosSchemeService;
import org.rudi.microservice.kos.storage.dao.skos.SkosConceptDao;
import org.rudi.microservice.kos.storage.dao.skos.SkosSchemeCustomDao;
import org.rudi.microservice.kos.storage.dao.skos.SkosSchemeDao;
import org.rudi.microservice.kos.storage.entity.skos.SkosConceptEntity;
import org.rudi.microservice.kos.storage.entity.skos.SkosRelationConceptEntity;
import org.rudi.microservice.kos.storage.entity.skos.SkosSchemeEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * @author FNI18300
 *
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SkosSchemeServiceImpl implements SkosSchemeService {

	private static final String UUID_SKOS_SCHEME_MISSING_MESSAGE = "UUID skos scheme missing";
	private static final String SKOS_SCHEME_MISSING_MESSAGE = "SkosScheme missing";
	private static final String SKOS_CONCEPT_MISSING_MESSAGE = "SkosConcept missing";
	private static final String UUID_SKOS_CONCEPT_MISSING_MESSAGE = "UUID SkosConcept missing";
	private static final String SKOS_SCHEME_UNKNOWN_MESSAGE = "SkosScheme unknown: ";
	private static final String SKOS_CONCEPT_WITH_SKOS_SCHEME_UNKNOWN = "Le skosConcept %s = %s associé au skosScheme uuid = %s est introuvable";

	private final SkosSchemeDao skosSchemeDao;
	private final SkosSchemeCustomDao skosSchemeCustomDao;
	private final SkosConceptDao skosConceptDao;
	private final SkosSchemeMapper skosSchemeMapper;
	private final SkosConceptMapper skosConceptMapper;
	private final SkosConceptFullMapper skosConceptFullMapper;

	@Value("${kos.translation.default.language.value:fr}")
	private String defaultLanguageValue;

	@Override
	public SkosScheme getSkosScheme(UUID uuid) {
		if (uuid == null) {
			throw new IllegalArgumentException(UUID_SKOS_SCHEME_MISSING_MESSAGE);
		}
		return skosSchemeMapper.entityToDto(skosSchemeDao.findByUUID(uuid));
	}

	@Override
	@Transactional
	public SkosScheme createSkosScheme(SkosScheme skosScheme) throws MissingPreferredLabelForDefaultLanguageException {
		if (skosScheme == null) {
			throw new IllegalArgumentException(SKOS_SCHEME_MISSING_MESSAGE);
		}

		SkosSchemeEntity skosSchemeEntity = skosSchemeMapper.dtoToEntity(skosScheme);
		validEntity(skosSchemeEntity);
		skosSchemeEntity.setUuid(UUID.randomUUID());

		skosSchemeDao.save(skosSchemeEntity);

		saveSkosSchemeTopConcepts(skosScheme.getTopConcepts(), skosSchemeEntity);

		return skosSchemeMapper.entityToDto(skosSchemeEntity);
	}

	@Override
	@Transactional
	public SkosScheme updateSkosScheme(SkosScheme skosScheme) throws MissingPreferredLabelForDefaultLanguageException {

		if (skosScheme == null) {
			throw new IllegalArgumentException(SKOS_SCHEME_MISSING_MESSAGE);
		}
		if (skosScheme.getSchemeId() == null) {
			throw new IllegalArgumentException(UUID_SKOS_SCHEME_MISSING_MESSAGE);
		}

		SkosSchemeEntity skosSchemeEntity = skosSchemeDao.findByUUID(skosScheme.getSchemeId());
		if (skosSchemeEntity == null) {
			throw new IllegalArgumentException(SKOS_SCHEME_UNKNOWN_MESSAGE + skosScheme.getSchemeId());
		}
		validEntity(skosSchemeEntity);
		skosSchemeMapper.dtoToEntity(skosScheme, skosSchemeEntity);
		skosSchemeEntity.setTopConcepts(new HashSet<>());

		skosSchemeDao.save(skosSchemeEntity);

		List<SkosConceptEntity> skosConceptEntities = skosConceptDao.findAllByOfSchemeUuid(skosSchemeEntity.getUuid());
		if (CollectionUtils.isNotEmpty(skosConceptEntities)) {
			skosConceptDao.deleteAll(skosConceptEntities);
		}

		saveSkosSchemeTopConcepts(skosScheme.getTopConcepts(), skosSchemeEntity);

		return skosSchemeMapper.entityToDto(skosSchemeEntity);
	}

	@Override
	@Transactional
	public void deleteSkosScheme(UUID skosSchemeUuid) {
		if (skosSchemeUuid == null) {
			throw new IllegalArgumentException(UUID_SKOS_SCHEME_MISSING_MESSAGE);
		}
		SkosSchemeEntity skosSchemeEntity = skosSchemeDao.findByUUID(skosSchemeUuid);
		if (skosSchemeEntity == null) {
			throw new IllegalArgumentException(SKOS_SCHEME_UNKNOWN_MESSAGE + skosSchemeUuid);
		}
		List<SkosConceptEntity> skosConceptEntities = skosConceptDao.findAllByOfSchemeUuid(skosSchemeUuid);
		if (CollectionUtils.isNotEmpty(skosConceptEntities)) {
			skosConceptDao.deleteAll(skosConceptEntities);
		}

		skosSchemeDao.delete(skosSchemeEntity);
	}

	@Override
	public SkosConcept getSkosConcept(UUID skosSchemeUuid, UUID skosConceptUuid) {
		if (skosSchemeUuid == null) {
			throw new IllegalArgumentException(UUID_SKOS_SCHEME_MISSING_MESSAGE);
		}
		if (skosConceptUuid == null) {
			throw new IllegalArgumentException(UUID_SKOS_CONCEPT_MISSING_MESSAGE);
		}

		return skosConceptFullMapper
				.entityToDto(skosConceptDao.findByUuidAndOfSchemeUuid(skosConceptUuid, skosSchemeUuid));
	}

	@Override
	@Transactional
	public SkosConcept createSkosConcept(UUID skosSchemeUuid, SkosConcept skosConcept, Boolean asTopConcept)
			throws MissingPreferredLabelForDefaultLanguageException {
		if (skosSchemeUuid == null) {
			throw new IllegalArgumentException(UUID_SKOS_SCHEME_MISSING_MESSAGE);
		}
		if (skosConcept == null) {
			throw new IllegalArgumentException(SKOS_CONCEPT_MISSING_MESSAGE);
		}

		SkosSchemeEntity skosSchemeEntity = skosSchemeDao.findByUUID(skosSchemeUuid);
		if (skosSchemeEntity == null) {
			throw new IllegalArgumentException(SKOS_SCHEME_UNKNOWN_MESSAGE + skosSchemeUuid);
		}

		SkosConceptEntity skosConceptEntity = skosConceptFullMapper.dtoToEntity(skosConcept);
		skosConceptEntity.setUuid(UUID.randomUUID());
		skosConceptEntity.setOfScheme(skosSchemeEntity);
		skosConceptEntity.setRelationConcepts(new HashSet<>());
		if (skosConceptEntity.getOpeningDate() == null) {
			skosConceptEntity.setOpeningDate(LocalDateTime.now());
		}
		validEntity(skosConceptEntity);
		setSkosConceptEntityRelations(skosConceptEntity, skosConcept, skosSchemeEntity);
		skosConceptDao.save(skosConceptEntity);

		if (BooleanUtils.isTrue(asTopConcept)) {
			skosSchemeEntity.addTopConcept(skosConceptEntity);
			skosSchemeDao.save(skosSchemeEntity);
		}

		return skosConceptMapper.entityToDto(skosConceptEntity);
	}

	@Override
	@Transactional
	public SkosConcept updateSkosConcept(UUID skosSchemeUuid, @Valid SkosConcept skosConcept, Boolean asTopConcept)
			throws MissingPreferredLabelForDefaultLanguageException {
		if (skosSchemeUuid == null) {
			throw new IllegalArgumentException(UUID_SKOS_SCHEME_MISSING_MESSAGE);
		}
		if (skosConcept == null || StringUtils.isEmpty(skosConcept.getConceptCode())) {
			throw new IllegalArgumentException(SKOS_CONCEPT_MISSING_MESSAGE);
		}
		SkosSchemeEntity skosSchemeEntity = skosSchemeDao.findByUUID(skosSchemeUuid);
		if (skosSchemeEntity == null) {
			throw new IllegalArgumentException(SKOS_SCHEME_UNKNOWN_MESSAGE + skosSchemeUuid);
		}

		SkosConceptEntity skosConceptEntity = skosConceptDao.findByCodeAndOfSchemeUuid(skosConcept.getConceptCode(),
				skosSchemeUuid);
		if (skosConceptEntity == null) {
			throw new IllegalArgumentException(String.format(SKOS_CONCEPT_WITH_SKOS_SCHEME_UNKNOWN, "uuid",
					skosConcept.getConceptId(), skosSchemeUuid));
		}

		skosConceptFullMapper.dtoToEntity(skosConcept, skosConceptEntity);
		skosConceptEntity.clearSkosRelations();
		if (skosConceptEntity.getOpeningDate() == null) {
			skosConceptEntity.setOpeningDate(LocalDateTime.now());
		}
		validEntity(skosConceptEntity);
		setSkosConceptEntityRelations(skosConceptEntity, skosConcept, skosSchemeEntity);
		skosConceptDao.save(skosConceptEntity);

		if (BooleanUtils.isTrue(asTopConcept)) {
			if (skosSchemeEntity.lookupSkosConcept(skosConceptEntity.getUuid()) == null) {
				skosSchemeEntity.addTopConcept(skosConceptEntity);
			}
		} else {
			skosSchemeEntity.removeTopConcept(skosConceptEntity.getUuid());
		}

		skosSchemeDao.save(skosSchemeEntity);

		return skosConceptMapper.entityToDto(skosConceptEntity);
	}

	@Override
	@Transactional
	public void deleteSkosConcept(UUID skosSchemeUuid, UUID skosConceptUuid) {
		if (skosSchemeUuid == null) {
			throw new IllegalArgumentException(UUID_SKOS_SCHEME_MISSING_MESSAGE);
		}
		if (skosConceptUuid == null) {
			throw new IllegalArgumentException(UUID_SKOS_CONCEPT_MISSING_MESSAGE);
		}

		SkosConceptEntity skosConceptEntity = skosConceptDao.findByUuidAndOfSchemeUuid(skosConceptUuid, skosSchemeUuid);
		if (skosConceptEntity == null) {
			throw new IllegalArgumentException(
					String.format(SKOS_CONCEPT_WITH_SKOS_SCHEME_UNKNOWN, "uuid", skosConceptUuid, skosSchemeUuid));
		}
		SkosSchemeEntity skosSchemeEntity = skosSchemeDao.findByUUID(skosSchemeUuid);
		if (skosSchemeEntity == null) {
			throw new IllegalArgumentException(SKOS_SCHEME_UNKNOWN_MESSAGE + skosSchemeUuid);
		}
		skosSchemeEntity.removeTopConcept(skosConceptUuid);
		skosSchemeDao.save(skosSchemeEntity);

		skosConceptDao.delete(skosConceptEntity);
	}

	@Override
	public Page<SkosScheme> searchSkosSchemes(SkosSchemeSearchCriteria skosSchemeSearchCriteria, Pageable pageable) {
		if (skosSchemeSearchCriteria == null) {
			skosSchemeSearchCriteria = SkosSchemeSearchCriteria.builder().build();
		}
		return skosSchemeMapper.entitiesToDto(skosSchemeCustomDao.searchSkosSchemes(skosSchemeSearchCriteria, pageable),
				pageable);
	}

	@Override
	public List<SkosConcept> getTopConcepts(UUID skosSchemeUuid) {
		if (skosSchemeUuid == null) {
			throw new IllegalArgumentException(UUID_SKOS_SCHEME_MISSING_MESSAGE);
		}
		SkosSchemeEntity skosSchemeEntity = skosSchemeDao.findByUUID(skosSchemeUuid);
		if (skosSchemeEntity == null) {
			throw new IllegalArgumentException(SKOS_SCHEME_UNKNOWN_MESSAGE + skosSchemeUuid);
		}
		return skosConceptMapper.entitiesToDto(skosSchemeEntity.getTopConcepts());
	}

	private void validEntity(SkosSchemeEntity entity) {
		if (StringUtils.isEmpty(entity.getCode())) {
			throw new IllegalArgumentException("Invalid empty comment:" + entity);
		}
	}

	private void validEntity(SkosConceptEntity entity) throws MissingPreferredLabelForDefaultLanguageException {
		if (StringUtils.isEmpty(entity.getCode())) {
			throw new IllegalArgumentException("Invalid empty code:" + entity);
		}

		if (CollectionUtils.isEmpty(entity.getPreferedLabels())) {
			throw new IllegalArgumentException("Invalid empty preferedLabels:" + entity);
		}

		// Concept must have at least one preferred label for default language
		entity.getPreferedLabels().stream()
				.filter(preferedLabel -> preferedLabel.getLang().equals(defaultLanguageValue)).findAny()
				.orElseThrow(() -> new MissingPreferredLabelForDefaultLanguageException(entity.getCode(),
						defaultLanguageValue));
	}

	/**
	 * Sauvegarde des skosConcepts d'un skosScheme
	 *
	 * @param topConcepts      liste des tops concepts
	 * @param skosSchemeEntity skosScheme
	 */
	private void saveSkosSchemeTopConcepts(List<SkosConcept> topConcepts, SkosSchemeEntity skosSchemeEntity)
			throws MissingPreferredLabelForDefaultLanguageException {
		if (CollectionUtils.isNotEmpty(topConcepts)) {
			for (SkosConcept topConcept : topConcepts) {
				SkosConceptEntity skosConceptEntity = skosConceptFullMapper.dtoToEntity(topConcept);
				skosConceptEntity.setUuid(UUID.randomUUID());
				saveSkosConceptEntitiesFromRootTopConcept(skosConceptEntity, topConcept, skosSchemeEntity);

				skosSchemeEntity.addTopConcept(skosConceptEntity);
			}
			skosSchemeDao.save(skosSchemeEntity);
		}
	}

	/**
	 * Sauvegarde d'un top concept, création ou récupération des autres concepts
	 * 
	 * @param skosConceptEntity skosConceptEntity
	 * @param skosConcept       skosConcept
	 * @param skosSchemeEntity  skosSchemeEntity
	 */
	private void saveSkosConceptEntitiesFromRootTopConcept(SkosConceptEntity skosConceptEntity, SkosConcept skosConcept,
			SkosSchemeEntity skosSchemeEntity) throws MissingPreferredLabelForDefaultLanguageException {
		validEntity(skosConceptEntity);
		skosConceptEntity.setOfScheme(skosSchemeEntity);
		if (CollectionUtils.isNotEmpty(skosConcept.getNarrowerConcepts())) {
			for (SkosConcept narrowerSkosConcept : skosConcept.getNarrowerConcepts()) {
				SkosConceptEntity generatedEntity = getOrGenerateSkosConceptEntity(narrowerSkosConcept,
						skosSchemeEntity);
				saveSkosConceptEntitiesFromRootTopConcept(generatedEntity, narrowerSkosConcept, skosSchemeEntity);

				SkosRelationConceptEntity skosRelationConceptEntity = new SkosRelationConceptEntity();
				skosRelationConceptEntity.setTarget(generatedEntity);
				skosRelationConceptEntity.setUuid(UUID.randomUUID());
				skosRelationConceptEntity.setType(SkosRelationType.NARROWING);

				skosConceptEntity.addSkosRelation(skosRelationConceptEntity);
			}
		}
		if (CollectionUtils.isNotEmpty(skosConcept.getSiblingConcepts())) {
			for (SkosConcept siblingSkosConcept : skosConcept.getSiblingConcepts()) {
				SkosConceptEntity generatedEntity = getOrGenerateSkosConceptEntity(siblingSkosConcept,
						skosSchemeEntity);
				saveSkosConceptEntitiesFromRootTopConcept(generatedEntity, siblingSkosConcept, skosSchemeEntity);

				SkosRelationConceptEntity skosRelationConceptEntity = new SkosRelationConceptEntity();
				skosRelationConceptEntity.setTarget(generatedEntity);
				skosRelationConceptEntity.setUuid(UUID.randomUUID());
				skosRelationConceptEntity.setType(SkosRelationType.SIBLING);

				skosConceptEntity.addSkosRelation(skosRelationConceptEntity);
			}
		}
		if (CollectionUtils.isNotEmpty(skosConcept.getRelativeConcepts())) {
			for (SkosConcept relativeSkosConcept : skosConcept.getRelativeConcepts()) {
				SkosConceptEntity generatedEntity = getOrGenerateSkosConceptEntity(relativeSkosConcept,
						skosSchemeEntity);
				saveSkosConceptEntitiesFromRootTopConcept(generatedEntity, relativeSkosConcept, skosSchemeEntity);

				SkosRelationConceptEntity skosRelationConceptEntity = new SkosRelationConceptEntity();
				skosRelationConceptEntity.setTarget(generatedEntity);
				skosRelationConceptEntity.setUuid(UUID.randomUUID());
				skosRelationConceptEntity.setType(SkosRelationType.RELATIVE);

				skosConceptEntity.addSkosRelation(skosRelationConceptEntity);
			}
		}
		skosConceptDao.save(skosConceptEntity);
	}

	private SkosConceptEntity getOrGenerateSkosConceptEntity(SkosConcept skosConcept,
			SkosSchemeEntity skosSchemeEntity) {
		SkosConceptEntity skosConceptEntity = skosConceptDao.findByCodeAndOfSchemeUuid(skosConcept.getConceptCode(),
				skosSchemeEntity.getUuid());
		if (skosConceptEntity == null) {
			skosConceptEntity = skosConceptMapper.dtoToEntity(skosConcept);
			skosConceptEntity.setUuid(UUID.randomUUID());
			if (skosConceptEntity.getOpeningDate() == null) {
				skosConceptEntity.setOpeningDate(LocalDateTime.now());
			}
		}
		return skosConceptEntity;
	}

	private SkosConceptEntity getSkosConceptEntityByCodeOrException(String skosConceptCode, UUID skosSchemeUuid) {
		SkosConceptEntity skosConceptEntity = skosConceptDao.findByCodeAndOfSchemeUuid(skosConceptCode, skosSchemeUuid);
		if (skosConceptEntity == null) {
			throw new IllegalArgumentException(
					String.format(SKOS_CONCEPT_WITH_SKOS_SCHEME_UNKNOWN, "code", skosConceptCode, skosSchemeUuid));
		}
		return skosConceptEntity;
	}

	/**
	 * Mise à jour des relations d'un concept à créer ou à mettre à jour
	 * 
	 * @param skosConceptEntity skosConceptEntity
	 * @param skosConcept       skosConcept
	 * @param skosSchemeEntity  skosSchemeEntity
	 */
	private void setSkosConceptEntityRelations(SkosConceptEntity skosConceptEntity, SkosConcept skosConcept,
			SkosSchemeEntity skosSchemeEntity) {
		if (CollectionUtils.isNotEmpty(skosConcept.getNarrowerConcepts())) {
			skosConcept.getNarrowerConcepts().forEach(narrowerSkosConcept -> {
				SkosConceptEntity narrowerSkosConceptEntity = getSkosConceptEntityByCodeOrException(
						narrowerSkosConcept.getConceptCode(), skosSchemeEntity.getUuid());

				SkosRelationConceptEntity skosRelationConceptEntity = new SkosRelationConceptEntity();
				skosRelationConceptEntity.setTarget(narrowerSkosConceptEntity);
				skosRelationConceptEntity.setUuid(UUID.randomUUID());
				skosRelationConceptEntity.setType(SkosRelationType.NARROWING);

				skosConceptEntity.addSkosRelation(skosRelationConceptEntity);
			});
		}
		if (CollectionUtils.isNotEmpty(skosConcept.getSiblingConcepts())) {
			skosConcept.getSiblingConcepts().forEach(siblingSkosConcept -> {
				SkosConceptEntity siblingSkosConceptEntity = getSkosConceptEntityByCodeOrException(
						siblingSkosConcept.getConceptCode(), skosSchemeEntity.getUuid());

				SkosRelationConceptEntity skosRelationConceptEntity = new SkosRelationConceptEntity();
				skosRelationConceptEntity.setTarget(siblingSkosConceptEntity);
				skosRelationConceptEntity.setUuid(UUID.randomUUID());
				skosRelationConceptEntity.setType(SkosRelationType.SIBLING);

				skosConceptEntity.addSkosRelation(skosRelationConceptEntity);
			});
		}
		if (CollectionUtils.isNotEmpty(skosConcept.getRelativeConcepts())) {
			skosConcept.getRelativeConcepts().forEach(relativeSkosConcept -> {
				SkosConceptEntity relativeSkosConceptEntity = getSkosConceptEntityByCodeOrException(
						relativeSkosConcept.getConceptCode(), skosSchemeEntity.getUuid());

				SkosRelationConceptEntity skosRelationConceptEntity = new SkosRelationConceptEntity();
				skosRelationConceptEntity.setTarget(relativeSkosConceptEntity);
				skosRelationConceptEntity.setUuid(UUID.randomUUID());
				skosRelationConceptEntity.setType(SkosRelationType.RELATIVE);

				skosConceptEntity.addSkosRelation(skosRelationConceptEntity);
			});
		}
		if (CollectionUtils.isNotEmpty(skosConcept.getBroaderConcepts())) {
			skosConcept.getBroaderConcepts().forEach(broaderSkosConcept -> {
				SkosConceptEntity broaderSkosConceptEntity = getSkosConceptEntityByCodeOrException(
						broaderSkosConcept.getConceptCode(), skosSchemeEntity.getUuid());

				SkosRelationConceptEntity skosRelationConceptEntity = new SkosRelationConceptEntity();
				skosRelationConceptEntity.setTarget(broaderSkosConceptEntity);
				skosRelationConceptEntity.setUuid(UUID.randomUUID());
				skosRelationConceptEntity.setType(SkosRelationType.BROADER);

				skosConceptEntity.addSkosRelation(skosRelationConceptEntity);
			});
		}
	}
}
