/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.helper.workflow;

import java.util.UUID;

import org.rudi.facet.acl.bean.User;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.bpmn.entity.workflow.AssetDescriptionEntity;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * @author FNI18300
 *
 */
public abstract class AbstractAssignmentHelper<E extends AssetDescriptionEntity> implements AssignmentHelper<E> {

	@Autowired
	@Getter(value = AccessLevel.PROTECTED)
	private ACLHelper aclHelper;

	@Override
	public User getUserByLogin(String login) {
		return aclHelper.getUserByLogin(login);
	}

	@Override
	public User getUserByUuid(UUID uuid){
		return aclHelper.getUserByUUID(uuid);
	}


}
