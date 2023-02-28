package org.rudi.microservice.selfdata.storage.dao.selfdatadataset.impl;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Predicate;

import org.rudi.common.storage.dao.AbstractCustomDaoImpl;
import org.rudi.microservice.selfdata.core.bean.SelfdataDatasetSearchCriteria;
import org.rudi.microservice.selfdata.storage.dao.selfdatadataset.SelfdataDatasetCustomDao;
import org.rudi.microservice.selfdata.storage.dao.selfdatadataset.SelfdataDatasetCustomSearchCriteria;
import org.rudi.microservice.selfdata.storage.entity.SelfdataDataset.SelfdataDatasetEntity;
import org.springframework.stereotype.Repository;

import lombok.val;
import static org.rudi.microservice.selfdata.storage.RepositoryConstants.DATASET_UUID_FIELD;
import static org.rudi.microservice.selfdata.storage.entity.SelfdataDataset.SelfdataDatasetEntity.INITIATOR_FIELD;

@Repository
public class SelfdataDatasetCustomDaoImpl
		extends AbstractCustomDaoImpl<SelfdataDatasetEntity, SelfdataDatasetSearchCriteria>
		implements SelfdataDatasetCustomDao {

	public SelfdataDatasetCustomDaoImpl(EntityManager entityManager) {
		super(entityManager, SelfdataDatasetEntity.class);
	}

	@Override
	public List<SelfdataDatasetEntity> searchSelfdataDatasets(SelfdataDatasetCustomSearchCriteria criteria) {

		String login = criteria.getLogin();
		List<UUID> datasetUuids = criteria.getDatasetUuids();

		val builder = entityManager.getCriteriaBuilder();
		val searchQuery = builder.createQuery(entitiesClass);
		val searchRoot = searchQuery.from(SelfdataDatasetEntity.class);

		Predicate equalsLogin = builder.equal(searchRoot.get(INITIATOR_FIELD), login);
		Predicate inDatasetUuids = searchRoot.get(DATASET_UUID_FIELD).in(datasetUuids);

		searchQuery.select(searchRoot).where(builder.and(equalsLogin, inDatasetUuids));
		return entityManager.createQuery(searchQuery).getResultList();
	}
}
