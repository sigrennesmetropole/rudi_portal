/**
 * RUDI Portail
 */
package org.rudi.microservice.selfdata.service.helper.selfdatainformationrequest;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.bpmn.helper.workflow.AbstractAssignmentHelper;
import org.rudi.microservice.selfdata.storage.entity.selfdatainformationrequest.SelfdataInformationRequestEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

/**
 * @author FNI18300
 *
 */
@Component
public class SelfdataInformationRequestAssigmentHelper extends AbstractAssignmentHelper<SelfdataInformationRequestEntity> {

	@Value("${application.role.moderator.code}")
	@Getter
	private String moderatorRoleCode;

	@Override
	public List<String> computeAssignees(SelfdataInformationRequestEntity assetDescription, String roleCode) {
		List<User> users = getAclHelper().searchUsers(roleCode);
		return users.stream().map(User::getLogin).collect(Collectors.toList());
	}

	@Override
	public String computeAssignee(SelfdataInformationRequestEntity assetDescription, String roleCode) {
		List<String> assignees = computeAssignees(assetDescription, roleCode);
		if (CollectionUtils.isNotEmpty(assignees)) {
			return assignees.get(0);
		} else {
			return null;
		}
	}

}
