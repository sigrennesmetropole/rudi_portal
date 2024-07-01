/**
 * RUDI Portail
 */
package org.rudi.microservice.projekt.service.helper.project;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.bpmn.helper.workflow.AbstractAssignmentHelper;
import org.rudi.facet.organization.bean.OrganizationMember;
import org.rudi.facet.organization.helper.OrganizationHelper;
import org.rudi.facet.organization.helper.exceptions.GetOrganizationMembersException;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author FNI18300
 *
 */
@Component
@Slf4j
public class ProjectAssigmentHelper extends AbstractAssignmentHelper<ProjectEntity> {

	@Value("${application.role.moderator.code}")
	@Getter
	private String moderatorRoleCode;

	@Autowired
	@Getter(value = AccessLevel.PROTECTED)
	private OrganizationHelper organizationHelper;

	@Autowired
	@Getter(value = AccessLevel.PROTECTED)
	private ACLHelper aclHelper;

	@Override
	public List<String> computeAssignees(ProjectEntity assetDescription, String roleCode) {
		List<User> users = getAclHelper().searchUsers(roleCode);
		return users.stream().map(User::getLogin).collect(Collectors.toList());
	}

	@Override
	public String computeAssignee(ProjectEntity assetDescription, String roleCode) {
		List<String> assignees = computeAssignees(assetDescription, roleCode);
		if (CollectionUtils.isNotEmpty(assignees)) {
			return assignees.get(0);
		} else {
			return null;
		}
	}

	public List<String> computeOrganizationMembersLogins(UUID organizationUuid) {
		List<String> membersLogins = new ArrayList<>();

		List<User> users = computeOrganizationMembers(organizationUuid);
		if (CollectionUtils.isNotEmpty(users)) {
			CollectionUtils.addAll(membersLogins, aclHelper.lookupEmailAddresses(users));
		}

		return membersLogins;
	}

	public List<User> computeOrganizationMembers(UUID organizationUuid){
		List<User> membersUser = new ArrayList<>();
		try {
			Collection<OrganizationMember> members = getOrganizationHelper().getOrganizationMembers(organizationUuid);
			if (CollectionUtils.isNotEmpty(members)) {
				for (OrganizationMember member : members) {
					final var user = getAclHelper().getUserByUUID(member.getUserUuid());
					if (user != null) {
						membersUser.add(user);
					} else {
						log.warn("Member with user uuid \"{}\" does not exist as user in ACL", member.getUserUuid());
					}
				}
			}
		} catch (GetOrganizationMembersException e) {
			log.error("Erreur de récupération des membres de l'organization {}", organizationUuid, e);
		}

		return membersUser;
	}


}
