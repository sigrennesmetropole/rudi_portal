/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.helper.workflow;

import java.util.Arrays;
import java.util.List;

import org.rudi.facet.bpmn.entity.workflow.AssetDescriptionEntity1Test;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component
public class AssigmentHelper1Test extends AbstractAssignmentHelper<AssetDescriptionEntity1Test> {

	@Override
	public List<String> computeAssignees(AssetDescriptionEntity1Test assetDescription, String roleName) {
		return Arrays.asList("test@test.com");
	}

	@Override
	public String computeAssignee(AssetDescriptionEntity1Test assetDescription, String roleName) {
		return "test@test.com";
	}

}
