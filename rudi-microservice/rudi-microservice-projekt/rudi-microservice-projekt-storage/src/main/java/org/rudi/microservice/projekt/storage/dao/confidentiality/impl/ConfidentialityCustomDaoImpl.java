package org.rudi.microservice.projekt.storage.dao.confidentiality.impl;

import org.rudi.common.storage.dao.AbstractCustomDaoImpl;
import org.rudi.microservice.projekt.core.bean.ConfidentialitySearchCriteria;
import org.rudi.microservice.projekt.storage.dao.confidentiality.ConfidentialityCustomDao;
import org.rudi.microservice.projekt.storage.entity.ConfidentialityEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class ConfidentialityCustomDaoImpl extends AbstractCustomDaoImpl<ConfidentialityEntity, ConfidentialitySearchCriteria> implements ConfidentialityCustomDao {

	public ConfidentialityCustomDaoImpl(EntityManager entityManager) {
		super(entityManager, ConfidentialityEntity.class);
	}

	@Override
	public Page<ConfidentialityEntity> searchConfidentialities(ConfidentialitySearchCriteria searchCriteria, Pageable pageable) {
		return search(searchCriteria, pageable);
	}
}
