package org.rudi.microservice.selfdata.storage.dao.selfdatatokentuple.impl;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.Predicate;

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.Nullable;
import org.rudi.common.storage.dao.AbstractCustomDaoImpl;
import org.rudi.microservice.selfdata.core.bean.SelfdataTokenTupleCriteria;
import org.rudi.microservice.selfdata.storage.dao.selfdatatokentuple.SelfdataTokenTupleCustomDao;
import org.rudi.microservice.selfdata.storage.entity.selfdatainformationrequest.SelfdataTokenTupleEntity;
import org.springframework.stereotype.Repository;

import lombok.val;

@Repository
public class SelfdataTokenTupleCustomDaoImpl extends AbstractCustomDaoImpl<SelfdataTokenTupleEntity, SelfdataTokenTupleCriteria> implements SelfdataTokenTupleCustomDao {
	private static final String FIELD_DATASET_UUID = "datasetUuid";
	private static final String FIELD_USER_UUID = "userUuid";
	private static final String FIELD_ID = "id";

	public SelfdataTokenTupleCustomDaoImpl(EntityManager entityManager) {
		super(entityManager, SelfdataTokenTupleEntity.class);
	}

	@Nullable
	@Override
	public SelfdataTokenTupleEntity findByDatasetUuidAndUserUuid(UUID datasetUuid, UUID userUuid) {
		val builder = entityManager.getCriteriaBuilder();
		val searchQuery = builder.createQuery(entitiesClass);
		val searchRoot = searchQuery.from(entitiesClass);
		final Predicate whereCondition = builder.and(builder.equal(searchRoot.get(FIELD_DATASET_UUID), datasetUuid), builder.equal(searchRoot.get(FIELD_USER_UUID), userUuid));
		javax.persistence.criteria.Order order = builder.desc(searchRoot.get(FIELD_ID));
		searchQuery.where(whereCondition)
				.orderBy(order);
		TypedQuery<SelfdataTokenTupleEntity> typedQuery = entityManager.createQuery(searchQuery);
		List<SelfdataTokenTupleEntity> tokenList = typedQuery.getResultList();
		if (CollectionUtils.isNotEmpty(tokenList)) { // Tri par ordre décroissant d'id et renvoie le prémier token (corresponondant à la dernière demande)
			return tokenList.get(0);
		}
		return null;
	}
}
