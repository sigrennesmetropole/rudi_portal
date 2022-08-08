/**
 * 
 */
package org.rudi.facet.bpmn.dao.form.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.rudi.common.storage.dao.AbstractCustomDaoImpl;
import org.rudi.facet.bpmn.bean.form.SectionDefinitionSearchCriteria;
import org.rudi.facet.bpmn.dao.form.SectionDefinitionCustomDao;
import org.rudi.facet.bpmn.entity.form.SectionDefinitionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author FNI18300
 *
 */
@Repository
public class SectionDefinitionCustomDaoImpl
		extends AbstractCustomDaoImpl<SectionDefinitionEntity, SectionDefinitionSearchCriteria>
		implements SectionDefinitionCustomDao {

	private static final String NAME_PROPERTY = "name";

	public SectionDefinitionCustomDaoImpl(EntityManager entityManager) {
		super(entityManager, SectionDefinitionEntity.class);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Page<SectionDefinitionEntity> searchSectionDefinitions(SectionDefinitionSearchCriteria searchCriteria,
			Pageable page) {
		return search(searchCriteria, page);
	}

	@Override
	protected void addPredicates(SectionDefinitionSearchCriteria searchCriteria, CriteriaBuilder builder,
			Root<SectionDefinitionEntity> root, List<Predicate> predicates) {
		if (searchCriteria != null) {
			if (StringUtils.isNotEmpty(searchCriteria.getName())) {
				buildPredicateStringCriteria(searchCriteria.getName(), NAME_PROPERTY, builder, root);
			}
		}
	}

}
