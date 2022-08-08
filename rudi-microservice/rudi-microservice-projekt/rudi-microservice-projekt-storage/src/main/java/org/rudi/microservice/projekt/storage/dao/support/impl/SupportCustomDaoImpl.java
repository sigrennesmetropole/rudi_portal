package org.rudi.microservice.projekt.storage.dao.support.impl;

import org.rudi.common.storage.dao.AbstractCustomDaoImpl;
import org.rudi.microservice.projekt.core.bean.SupportSearchCriteria;
import org.rudi.microservice.projekt.storage.dao.support.SupportCustomDao;
import org.rudi.microservice.projekt.storage.entity.SupportEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class SupportCustomDaoImpl extends AbstractCustomDaoImpl<SupportEntity, SupportSearchCriteria> implements SupportCustomDao {

	public SupportCustomDaoImpl(EntityManager entityManager) {
		super(entityManager, SupportEntity.class);
	}

	@Override
	public Page<SupportEntity> searchSupports(SupportSearchCriteria searchCriteria, Pageable pageable) {
		return search(searchCriteria, pageable);
	}
}
