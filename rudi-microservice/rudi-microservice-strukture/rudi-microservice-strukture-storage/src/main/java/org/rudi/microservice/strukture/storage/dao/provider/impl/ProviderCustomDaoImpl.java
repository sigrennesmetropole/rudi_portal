package org.rudi.microservice.strukture.storage.dao.provider.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.rudi.common.storage.dao.AbstractCustomDaoImpl;
import org.rudi.microservice.strukture.core.bean.ProviderSearchCriteria;
import org.rudi.microservice.strukture.storage.dao.provider.ProviderCustomDao;
import org.rudi.microservice.strukture.storage.entity.provider.NodeProviderEntity;
import org.rudi.microservice.strukture.storage.entity.provider.ProviderEntity;
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
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository custom pour les producteurs
 *
 * @author FNI18300
 */
@Repository
public class ProviderCustomDaoImpl extends AbstractCustomDaoImpl<ProviderEntity, ProviderSearchCriteria> implements ProviderCustomDao {

	// Champs utilisés pour le filtrage
	public static final String FIELD_CODE = "code";
	public static final String FIELD_LABEL = "label";
	public static final String FIELD_OPENING_DATE = "openingDate";
	public static final String FIELD_CLOSING_DATE = "closingDate";
	public static final String FIELD_NODE_PROVIDERS = "nodeProviders";
	public static final String FIELD_NODE_PROVIDERS_UUID = "uuid";

	public ProviderCustomDaoImpl(EntityManager entityManager) {
		super(entityManager, ProviderEntity.class);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Page<ProviderEntity> searchProviders(ProviderSearchCriteria searchProviderCriteria, Pageable pageable) {

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();

		// Requête pour compter le nombre de resultats total
		CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
		Root<ProviderEntity> countRoot = countQuery.from(ProviderEntity.class);
		buildQuery(searchProviderCriteria, builder, countQuery, countRoot);
		countQuery.select(builder.countDistinct(countRoot));
		Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

		// si aucun resultat

		if (totalCount == 0) {
			return new PageImpl<>(new ArrayList<>(), pageable, 0);
		}

		// Requête de recherche
		CriteriaQuery<ProviderEntity> searchQuery = builder.createQuery(ProviderEntity.class);
		Root<ProviderEntity> searchRoot = searchQuery.from(ProviderEntity.class);
		buildQuery(searchProviderCriteria, builder, searchQuery, searchRoot);

		searchQuery.select(searchRoot);
		searchQuery.orderBy(QueryUtils.toOrders(pageable.getSort(), searchRoot, builder));

		TypedQuery<ProviderEntity> typedQuery = entityManager.createQuery(searchQuery);
		List<ProviderEntity> providerEntities = typedQuery.setFirstResult((int) pageable.getOffset())
				.setMaxResults(pageable.getPageSize()).getResultList();
		return new PageImpl<>(providerEntities, pageable, totalCount.intValue());
	}

	private void buildQuery(ProviderSearchCriteria searchProviderCriteria, CriteriaBuilder builder,
			CriteriaQuery<?> criteriaQuery, Root<ProviderEntity> root) {

		if (searchProviderCriteria != null) {
			List<Predicate> predicates = new ArrayList<>();

			// code
			predicateStringCriteria(searchProviderCriteria.getCode(), FIELD_CODE, predicates, builder, root);

			// label
			predicateStringCriteria(searchProviderCriteria.getLabel(), FIELD_LABEL, predicates, builder, root);

			// nodeProviders.uuid
			if (searchProviderCriteria.getNodeProviderUuid() != null) {
				Join<ProviderEntity, NodeProviderEntity> nodeProviderJoin = root.join(FIELD_NODE_PROVIDERS,
						JoinType.LEFT);
				predicates
						.add(builder.equal(nodeProviderJoin.get("uuid"), searchProviderCriteria.getNodeProviderUuid()));
			}

			// inactif
			if (Boolean.TRUE.equals(searchProviderCriteria.getActive())) {
				final LocalDateTime d = LocalDateTime.now();
				predicates.add(builder.and(builder.lessThanOrEqualTo(root.get(FIELD_OPENING_DATE), d),
						builder.or(builder.greaterThanOrEqualTo(root.get(FIELD_CLOSING_DATE), d),
								builder.isNull(root.get(FIELD_CLOSING_DATE)))));
			}

			// cas critères dateDebut et dateFin renseignés
			if (searchProviderCriteria.getDateDebut() != null && searchProviderCriteria.getDateFin() != null) {

				final LocalDateTime dateDebut = searchProviderCriteria.getDateDebut();
				final LocalDateTime dateFin = searchProviderCriteria.getDateFin();

				predicates.add(builder.and(
						(builder.or(builder.isNull(root.get(FIELD_CLOSING_DATE)),
								builder.greaterThanOrEqualTo(root.get(FIELD_CLOSING_DATE), dateDebut))),
						builder.lessThanOrEqualTo(root.get(FIELD_OPENING_DATE), dateFin)));
			}

			// cas critères dateDebut uniquement :
			else if (searchProviderCriteria.getDateDebut() != null) {
				final LocalDateTime dateDebut = searchProviderCriteria.getDateDebut();
				predicates.add(builder.or(builder.isNull(root.get(FIELD_CLOSING_DATE)),
						builder.greaterThanOrEqualTo(root.get(FIELD_CLOSING_DATE), dateDebut)));

			} else
			// cas critères dateFin uniquement :
			if (searchProviderCriteria.getDateFin() != null) {
				final LocalDateTime dateFin = searchProviderCriteria.getDateFin();
				predicates.add(builder.lessThanOrEqualTo(root.get(FIELD_OPENING_DATE), dateFin));
			}

			// Définition de la clause Where
			if (CollectionUtils.isNotEmpty(predicates)) {
				criteriaQuery.where(builder.and(predicates.toArray(new Predicate[predicates.size()])));
			}
		}
	}

}
