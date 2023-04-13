package org.rudi.microservice.konsent.service.treatment.impl;

import java.util.UUID;

import org.rudi.facet.organization.helper.OrganizationHelper;
import org.rudi.facet.organization.helper.exceptions.GetOrganizationMembersException;
import org.rudi.microservice.konsent.storage.entity.common.OwnerType;
import org.rudi.microservice.konsent.storage.entity.treatment.TreatmentEntity;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TreatmentUtils {
	private final OrganizationHelper organizationHelper;

	boolean isAllowToDoActionOnTreatment(UUID userUuid, TreatmentEntity treatmentEntity) throws GetOrganizationMembersException {
		if (treatmentEntity.getOwnerUuid().equals(userUuid)) {
			return true;
		}
		return treatmentEntity.getOwnerType() == OwnerType.ORGANIZATION && organizationHelper.organizationContainsUser(treatmentEntity.getOwnerUuid(), userUuid);
	}
}
