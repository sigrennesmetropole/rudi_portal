/**
 * RUDI Portail
 */
package org.rudi.microservice.projekt.service.helper.linkeddataset;

import java.util.Arrays;
import java.util.List;

import org.rudi.facet.bpmn.helper.workflow.AbstractAssignmentHelper;
import org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetEntity;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component
public class LinkedDatasetAssigmentHelper extends AbstractAssignmentHelper<LinkedDatasetEntity> {

	@Override
	public List<String> computeAssignees(LinkedDatasetEntity assetDescription, String roleName) {
		return Arrays.asList("test@test.com");
	}

	@Override
	public String computeAssignee(LinkedDatasetEntity assetDescription, String roleName) {
		return "test@test.com";
	}

}
