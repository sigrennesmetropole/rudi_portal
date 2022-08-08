/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.helper.workflow;

import java.util.Arrays;
import java.util.List;

import org.rudi.facet.bpmn.entity.workflow.Test1AssetDescriptionEntity;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component
public class Test1AssigmentHelper extends AbstractAssignmentHelper<Test1AssetDescriptionEntity> {

	@Override
	public List<String> computeAssignees(Test1AssetDescriptionEntity assetDescription, String roleName) {
		return Arrays.asList("test@test.com");
	}

	@Override
	public String computeAssignee(Test1AssetDescriptionEntity assetDescription, String roleName) {
		return "test@test.com";
	}

}
