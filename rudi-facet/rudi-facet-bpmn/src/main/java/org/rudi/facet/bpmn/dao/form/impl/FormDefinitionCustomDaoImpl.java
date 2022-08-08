/**
 * 
 */
package org.rudi.facet.bpmn.dao.form.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.common.storage.dao.AbstractCustomDaoImpl;
import org.rudi.facet.bpmn.bean.form.FormDefinitionSearchCriteria;
import org.rudi.facet.bpmn.dao.form.FormDefinitionCustomDao;
import org.rudi.facet.bpmn.entity.form.FormDefinitionEntity;
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
public class FormDefinitionCustomDaoImpl extends
		AbstractCustomDaoImpl<FormDefinitionEntity, FormDefinitionSearchCriteria> implements FormDefinitionCustomDao {

	private static final String FORM_SECTION_DEFINITIONS_PROPERTY = "formSectionDefinitions";
	private static final String NAME_PROPERTY = "name";

	public FormDefinitionCustomDaoImpl(EntityManager entityManager) {
		super(entityManager, FormDefinitionEntity.class);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Page<FormDefinitionEntity> searchFormDefinitions(FormDefinitionSearchCriteria searchCriteria,
			Pageable page) {
		return search(searchCriteria, page);
	}

	@Override
	protected void addPredicates(FormDefinitionSearchCriteria searchCriteria, CriteriaBuilder builder,
			Root<FormDefinitionEntity> root, List<Predicate> predicates) {
		if (searchCriteria != null) {
			if (StringUtils.isNotEmpty(searchCriteria.getFormName())) {
				predicates
						.add(buildPredicateStringCriteria(searchCriteria.getFormName(), NAME_PROPERTY, builder, root));
			}
			if (StringUtils.isNotEmpty(searchCriteria.getSectionName())) {
				Join<FormDefinitionEntity, SectionDefinitionEntity> joinSections = root
						.join(FORM_SECTION_DEFINITIONS_PROPERTY, JoinType.INNER);
				predicateStringCriteriaForJoin(searchCriteria.getSectionName(), NAME_PROPERTY, predicates, builder,
						joinSections);
			}
			if (CollectionUtils.isNotEmpty(searchCriteria.getSectionUuids())) {
				Join<FormDefinitionEntity, SectionDefinitionEntity> joinSections = root
						.join(FORM_SECTION_DEFINITIONS_PROPERTY, JoinType.INNER);
				predicates.add(joinSections.get("uuid").in(searchCriteria.getSectionUuids()));
			}
		}
	}

}
