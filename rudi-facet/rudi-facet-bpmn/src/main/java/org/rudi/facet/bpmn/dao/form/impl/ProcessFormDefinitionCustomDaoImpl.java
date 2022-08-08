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
import org.rudi.facet.bpmn.bean.form.ProcessFormDefinitionSearchCriteria;
import org.rudi.facet.bpmn.dao.form.ProcessFormDefinitionCustomDao;
import org.rudi.facet.bpmn.entity.form.ProcessFormDefinitionEntity;
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
public class ProcessFormDefinitionCustomDaoImpl
		extends AbstractCustomDaoImpl<ProcessFormDefinitionEntity, ProcessFormDefinitionSearchCriteria>
		implements ProcessFormDefinitionCustomDao {

	private static final String PROCESS_DEFINITION_ID_PROPERTY = "processDefinitionId";
	private static final String USER_TASK_ID_PROPERTY = "userTaskId";
	private static final String ACTION_NAME_PROPERTY = "actionName";
	private static final String REVISION_PROPERTY = "revision";

	public ProcessFormDefinitionCustomDaoImpl(EntityManager entityManager) {
		super(entityManager, ProcessFormDefinitionEntity.class);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Page<ProcessFormDefinitionEntity> searchProcessFormDefintions(
			ProcessFormDefinitionSearchCriteria searchCriteria, Pageable page) {
		return search(searchCriteria, page);
	}

	private void buildPredicateRevision(ProcessFormDefinitionSearchCriteria searchCriteria, CriteriaBuilder builder,
			Root<ProcessFormDefinitionEntity> root, List<Predicate> predicates) {
		Predicate p = builder.equal(root.get(REVISION_PROPERTY), searchCriteria.getRevision());
		if (!searchCriteria.isAcceptFlexRevision()) {
			predicates.add(p);
		} else {
			Predicate f = builder.isNull(root.get(REVISION_PROPERTY));
			predicates.add(builder.or(p, f));
		}
	}

	private void buildPredicateUserTaskId(ProcessFormDefinitionSearchCriteria searchCriteria, CriteriaBuilder builder,
			Root<ProcessFormDefinitionEntity> root, List<Predicate> predicates) {
		Predicate p = builder.equal(root.get(USER_TASK_ID_PROPERTY), searchCriteria.getUserTaskId());
		if (!searchCriteria.isAcceptFlexUserTaskId()) {
			predicates.add(p);
		} else {
			Predicate f = builder.isNull(root.get(USER_TASK_ID_PROPERTY));
			predicates.add(builder.or(p, f));
		}
	}

	private void buildPredicateActionName(ProcessFormDefinitionSearchCriteria searchCriteria, CriteriaBuilder builder,
			Root<ProcessFormDefinitionEntity> root, List<Predicate> predicates) {
		Predicate p = builder.equal(root.get(ACTION_NAME_PROPERTY), searchCriteria.getUserTaskId());
		if (!searchCriteria.isAcceptFlexActionName()) {
			predicates.add(p);
		} else {
			Predicate f = builder.isNull(root.get(ACTION_NAME_PROPERTY));
			predicates.add(builder.or(p, f));
		}
	}

	@Override
	protected void addPredicates(ProcessFormDefinitionSearchCriteria searchCriteria, CriteriaBuilder builder,
			Root<ProcessFormDefinitionEntity> root, List<Predicate> predicates) {
		if (searchCriteria != null) {
			if (StringUtils.isNotEmpty(searchCriteria.getProcessDefinitionId())) {
				predicates.add(builder.equal(root.get(PROCESS_DEFINITION_ID_PROPERTY),
						searchCriteria.getProcessDefinitionId()));
			}
			if (searchCriteria.getRevision() != null || searchCriteria.isAcceptFlexRevision()) {
				buildPredicateRevision(searchCriteria, builder, root, predicates);
			}
			if (StringUtils.isNotEmpty(searchCriteria.getUserTaskId())) {
				buildPredicateUserTaskId(searchCriteria, builder, root, predicates);
			}
			if (StringUtils.isNotEmpty(searchCriteria.getActionName())) {
				buildPredicateActionName(searchCriteria, builder, root, predicates);
			}
		}
	}

}
