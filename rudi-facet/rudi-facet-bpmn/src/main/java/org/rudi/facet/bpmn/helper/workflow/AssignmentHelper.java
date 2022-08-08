/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.helper.workflow;

import java.util.List;

import org.rudi.facet.acl.bean.User;
import org.rudi.facet.bpmn.entity.workflow.AssetDescriptionEntity;

/**
 * @author FNI18300
 *
 */
public interface AssignmentHelper<E extends AssetDescriptionEntity> {

	List<String> computeAssignees(E assetDescription, String roleCode);

	String computeAssignee(E assetDescription, String roleCode);

	User getUserByLogin(String login);

}
