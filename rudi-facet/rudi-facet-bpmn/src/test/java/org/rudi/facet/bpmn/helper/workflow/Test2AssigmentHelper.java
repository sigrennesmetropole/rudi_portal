/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.helper.workflow;

import java.util.Arrays;
import java.util.List;

import org.rudi.facet.bpmn.entity.workflow.Test2AssetDescriptionEntity;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component
public class Test2AssigmentHelper extends AbstractAssignmentHelper<Test2AssetDescriptionEntity> {

	@Override
	public List<String> computeAssignees(Test2AssetDescriptionEntity assetDescription, String roleName) {
		return Arrays.asList("test@test.com");
	}

	@Override
	public String computeAssignee(Test2AssetDescriptionEntity assetDescription, String roleName) {
		return "test@test.com";
	}

}
