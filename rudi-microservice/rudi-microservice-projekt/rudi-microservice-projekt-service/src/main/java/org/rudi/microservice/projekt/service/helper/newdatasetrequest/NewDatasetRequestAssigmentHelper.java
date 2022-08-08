/**
 * RUDI Portail
 */
package org.rudi.microservice.projekt.service.helper.newdatasetrequest;

import java.util.Arrays;
import java.util.List;

import org.rudi.facet.bpmn.helper.workflow.AbstractAssignmentHelper;
import org.rudi.microservice.projekt.storage.entity.newdatasetrequest.NewDatasetRequestEntity;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component
public class NewDatasetRequestAssigmentHelper extends AbstractAssignmentHelper<NewDatasetRequestEntity> {

	@Override
	public List<String> computeAssignees(NewDatasetRequestEntity assetDescription, String roleName) {
		return Arrays.asList("test@test.com");
	}

	@Override
	public String computeAssignee(NewDatasetRequestEntity assetDescription, String roleName) {
		return "test@test.com";
	}

}
