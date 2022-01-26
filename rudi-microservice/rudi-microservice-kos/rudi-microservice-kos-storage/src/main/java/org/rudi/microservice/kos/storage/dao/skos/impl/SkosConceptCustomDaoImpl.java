package org.rudi.microservice.kos.storage.dao.skos.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.common.storage.dao.AbstractCustomDaoImpl;
import org.rudi.microservice.kos.core.bean.Language;
import org.rudi.microservice.kos.core.bean.SimpleSkosConceptProjection;
import org.rudi.microservice.kos.core.bean.SkosConceptLabel;
import org.rudi.microservice.kos.core.bean.SkosConceptSearchCriteria;
import org.rudi.microservice.kos.core.bean.SkosRelationType;
import org.rudi.microservice.kos.storage.dao.skos.SkosConceptCustomDao;
import org.rudi.microservice.kos.storage.entity.skos.SkosConceptEntity;
import org.rudi.microservice.kos.storage.entity.skos.SkosConceptTranslationEntity;
import org.rudi.microservice.kos.storage.entity.skos.SkosRelationConceptEntity;
import org.rudi.microservice.kos.storage.entity.skos.SkosSchemeEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class SkosConceptCustomDaoImpl extends AbstractCustomDaoImpl implements SkosConceptCustomDao {

	// Champs utilisés pour le filtrage
	public static final String FIELD_UUID = "uuid";
	public static final String FIELD_ID = "id";
	public static final String FIELD_CODE = "code";
	public static final String FIELD_TEXT = "text";
	public static final String FIELD_LANG = "lang";
	public static final String FIELD_ALTERNATE_LABELS = "alternateLabels";
	public static final String FIELD_PREFERRED_LABELS = "preferedLabels";
	public static final String FIELD_HIDDEN_LABELS = "hiddenLabels";
	public static final String FIELD_RELATION = "relationConcepts";
	public static final String FIELD_SCHEME = "ofScheme";
	public static final String FIELD_ROLE = "conceptRole";
	public static final String FIELD_TYPE = "type";
	public static final String FIELD_TARGET = "target";
	public static final String FIELD_URI = "conceptUri";
	public static final String FIELD_ICON = "conceptIcon";
	private final EntityManager entityManager;
	@Value("${kos.translation.default.language.value:fr}")
	private String defaultLanguageValue;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Page<SimpleSkosConceptProjection> searchSkosConcepts(SkosConceptSearchCriteria searchCriteria, Pageable pageable) {

		if (searchCriteria == null) {
			return new PageImpl<>(new ArrayList<>(), pageable, 0);
		}

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();

		// Requête pour compter le nombre de resultats total
		CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
		Root<SkosConceptEntity> countRoot = countQuery.from(SkosConceptEntity.class);
		List<Predicate> predicates = new ArrayList<>();
		buildQuery(searchCriteria, builder, countQuery, countRoot, predicates);
		buildLangJoinsQuery(searchCriteria.getLang(), FIELD_PREFERRED_LABELS, builder, countRoot);

		countQuery.select(builder.countDistinct(countRoot)).distinct(true);
		// Définition de la clause Where
		if (CollectionUtils.isNotEmpty(predicates)) {
			countQuery.where(builder.and(predicates.toArray(Predicate[]::new)));
		}
		Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

		// si aucun resultat

		if (totalCount == 0) {
			return new PageImpl<>(new ArrayList<>(), pageable, 0);
		}

		// Requête de recherche
		CriteriaQuery<SimpleSkosConceptProjection> searchQuery = builder.createQuery(SimpleSkosConceptProjection.class);
		Root<SkosConceptEntity> searchRoot = searchQuery.from(SkosConceptEntity.class);

		predicates = new ArrayList<>();
		buildQuery(searchCriteria, builder, searchQuery, searchRoot, predicates);

		Join<SkosConceptEntity, SkosSchemeEntity> skosSchemeJoin = searchRoot.join(FIELD_SCHEME, JoinType.INNER);
		List<Join<SkosConceptEntity, SkosConceptTranslationEntity>> skosConceptTranslationJoins =
				buildLangJoinsQuery(searchCriteria.getLang(), FIELD_PREFERRED_LABELS, builder, searchRoot);

		CriteriaBuilder.Coalesce<?> coalesce = buildCoalesceValues(builder, FIELD_TEXT, skosConceptTranslationJoins);

		searchQuery.select(builder.construct(SimpleSkosConceptProjection.class, searchRoot.get(FIELD_UUID),
				searchRoot.get(FIELD_CODE),
				searchRoot.get(FIELD_URI),
				searchRoot.get(FIELD_ICON),
				skosSchemeJoin.get(FIELD_CODE),
				searchRoot.get(FIELD_ROLE),
				coalesce))
				.distinct(true);
		searchQuery.orderBy(QueryUtils.toOrders(pageable.getSort(), searchRoot, builder));

		// Définition de la clause Where
		if (CollectionUtils.isNotEmpty(predicates)) {
			searchQuery.where(builder.and(predicates.toArray(Predicate[]::new)));
		}

		TypedQuery<SimpleSkosConceptProjection> typedQuery = entityManager.createQuery(searchQuery);
		List<SimpleSkosConceptProjection> simpleSkosConceptProjections = typedQuery.setFirstResult((int) pageable.getOffset())
				.setMaxResults(pageable.getPageSize()).getResultList();
		return new PageImpl<>(simpleSkosConceptProjections, pageable, totalCount.intValue());
	}

	private void buildQuery(SkosConceptSearchCriteria searchCriteria, CriteriaBuilder builder,
			CriteriaQuery<?> criteriaQuery, Root<SkosConceptEntity> root, List<Predicate> predicates) {

		if (searchCriteria != null) {

			List<Predicate> predicatesRoot = buildRootEntity(searchCriteria, builder, root);

			// types
			List<Predicate> predicatesRelations = new ArrayList<>();
			if (CollectionUtils.isNotEmpty(searchCriteria.getTypes())) {

				Subquery<Long> subQuery;
				Root<SkosConceptEntity> subFrom;
				Join<SkosConceptEntity, SkosRelationConceptEntity> skosRelationConceptJoin;
				Join<SkosRelationConceptEntity, SkosConceptEntity> skosRelationTargetJoin;

				if (searchCriteria.getTypes().contains(SkosRelationType.BROADER)) {
					subQuery = criteriaQuery.subquery(Long.class);
					subFrom = subQuery.from(SkosConceptEntity.class);
					skosRelationConceptJoin = subFrom.join(FIELD_RELATION, JoinType.LEFT);
					skosRelationTargetJoin = skosRelationConceptJoin.join(FIELD_TARGET, JoinType.LEFT);

					subQuery.select(subFrom.get(FIELD_ID));
					subQuery.where(builder.and(buildRootEntity(searchCriteria, builder, skosRelationTargetJoin).toArray(Predicate[]::new)),
							skosRelationConceptJoin.get(FIELD_TYPE).in(SkosRelationType.NARROWING));

					predicatesRelations.add(root.get(FIELD_ID).in(subQuery));
				}

				List<SkosRelationType> types = searchCriteria.getTypes().stream()
						.filter(skosRelationType -> !skosRelationType.equals(SkosRelationType.BROADER))
						.collect(Collectors.toList());
				if (CollectionUtils.isNotEmpty(types)) {
					subQuery = criteriaQuery.subquery(Long.class);
					subFrom = subQuery.from(SkosConceptEntity.class);
					skosRelationConceptJoin = subFrom.join(FIELD_RELATION, JoinType.LEFT);
					skosRelationTargetJoin = skosRelationConceptJoin.join(FIELD_TARGET, JoinType.LEFT);

					subQuery.select(skosRelationTargetJoin.get(FIELD_ID));
					subQuery.where(builder.and(buildRootEntity(searchCriteria, builder, subFrom).toArray(Predicate[]::new)),
							skosRelationConceptJoin.get(FIELD_TYPE).in(types));

					predicatesRelations.add(root.get(FIELD_ID).in(subQuery));
				}
			}
			predicatesRelations.add(builder.and(predicatesRoot.toArray(Predicate[]::new)));
			predicates.add(builder.or(predicatesRelations.toArray(Predicate[]::new)));
		}
	}

	private List<Predicate> buildRootEntity(SkosConceptSearchCriteria searchCriteria, CriteriaBuilder builder,
			From<?, ?> root) {

		List<Predicate> predicates = new ArrayList<>();

		if (searchCriteria != null) {
			// codes concepts
			predicateStringCriteria(searchCriteria.getCodes(), FIELD_CODE, predicates, builder, root);

			// labels
			buildLabelsQuery(searchCriteria, builder, predicates, root);

			// roles
			predicateStringCriteria(searchCriteria.getRoles(), FIELD_ROLE, predicates, builder, root);

			// codes scheme
			if (CollectionUtils.isNotEmpty(searchCriteria.getCodesScheme())) {
				Join<SkosConceptEntity, SkosSchemeEntity> skosSchemeJoin = root.join(FIELD_SCHEME,
						JoinType.LEFT);
				predicateStringCriteria(searchCriteria.getCodesScheme(), FIELD_CODE, predicates, builder, skosSchemeJoin);
			}
		}

		return predicates;

	}

	private void buildLabelsQuery(SkosConceptSearchCriteria searchCriteria, CriteriaBuilder builder,
			List<Predicate> predicates, From<?, ?> root) {

		// si le type de translation est précisé, on recherche le lang et text par rapport à la SkosConceptTranslationEntity concernée
		if (CollectionUtils.isNotEmpty(searchCriteria.getLabels())) {
			if (searchCriteria.getLabels().contains(SkosConceptLabel.ALTERNATE)) {
				buildLabelsQuery(searchCriteria, builder, predicates, root, FIELD_ALTERNATE_LABELS);
			}
			if (searchCriteria.getLabels().contains(SkosConceptLabel.PREFERRED)) {
				buildLabelsQuery(searchCriteria, builder, predicates, root, FIELD_PREFERRED_LABELS);
			}
			if (searchCriteria.getLabels().contains(SkosConceptLabel.HIDDEN)) {
				buildLabelsQuery(searchCriteria, builder, predicates, root, FIELD_HIDDEN_LABELS);
			}
		}
	}

	private void buildLabelsQuery(SkosConceptSearchCriteria searchCriteria, CriteriaBuilder builder,
			List<Predicate> predicates, From<?, ?> root, String labelColumn) {
		List<Join<SkosConceptEntity, SkosConceptTranslationEntity>> skosConceptAlternateTranslationJoins =
				buildLangJoinsQuery(searchCriteria.getLang(), labelColumn, builder, root);
		if (StringUtils.isNotEmpty(searchCriteria.getText())) {
			skosConceptAlternateTranslationJoins.forEach(skosConceptAlternateTranslationJoin ->
					predicateStringCriteria(searchCriteria.getText(), FIELD_TEXT, predicates, builder, skosConceptAlternateTranslationJoin));
		}
	}

	private List<Join<SkosConceptEntity, SkosConceptTranslationEntity>> buildLangJoinsQuery(Language language, String column,
			CriteriaBuilder builder, From<?, ?> rootJoin) {

		Set<Language> languagesRequest = buildLanguageRequestList(language);
		List<Join<SkosConceptEntity, SkosConceptTranslationEntity>> joins = new ArrayList<>();

		for (Language languageRequest : languagesRequest) {
			Join<SkosConceptEntity, SkosConceptTranslationEntity> skosConceptTranslationJoin = rootJoin.join(column,
					JoinType.LEFT);
			skosConceptTranslationJoin.on(builder.equal(skosConceptTranslationJoin.get(FIELD_LANG), languageRequest.getValue()));
			joins.add(skosConceptTranslationJoin);
		}
		return joins;
	}

	private Set<Language> buildLanguageRequestList(Language language) {
		Set<Language> languageRequestList = new LinkedHashSet<>();
		Language defaultLanguage = Language.fromValue(defaultLanguageValue);

		languageRequestList.add(language);
		if (language.getValue().contains("-")) {
			languageRequestList.add(Language.fromValue(language.getValue().split("-")[0]));
		}
		languageRequestList.add(defaultLanguage);

		return languageRequestList;
	}

	private CriteriaBuilder.Coalesce<?> buildCoalesceValues(CriteriaBuilder builder, String column, List<Join<SkosConceptEntity, SkosConceptTranslationEntity>> joins) {
		CriteriaBuilder.Coalesce<?> coalesce = builder.coalesce();

		for (Join<SkosConceptEntity, SkosConceptTranslationEntity> join : joins) {
			coalesce = coalesce.value(join.get(column));
		}
		return coalesce;
	}

}
