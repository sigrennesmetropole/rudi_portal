package org.rudi.microservice.projekt.storage.dao.reutilisationstatus.impl;

import javax.persistence.EntityManager;

import org.rudi.common.storage.dao.AbstractCustomDaoImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.rudi.microservice.projekt.core.bean.ReutilisationStatusSearchCriteria;
import org.rudi.microservice.projekt.storage.dao.reutilisationstatus.ReutilisationStatusCustomDao;
import org.rudi.microservice.projekt.storage.entity.ReutilisationStatusEntity;
import org.springframework.stereotype.Repository;

@Repository
public class ReutilisationStatusCustomDaoImpl extends AbstractCustomDaoImpl<ReutilisationStatusEntity, ReutilisationStatusSearchCriteria> implements ReutilisationStatusCustomDao {

	public ReutilisationStatusCustomDaoImpl(EntityManager entityManager) {
		super(entityManager, ReutilisationStatusEntity.class);
	}

	@Override
	public Page<ReutilisationStatusEntity> searchReutilisationStatus(ReutilisationStatusSearchCriteria criteria, Pageable pageable) {
		return search(criteria, pageable);
	}
}
