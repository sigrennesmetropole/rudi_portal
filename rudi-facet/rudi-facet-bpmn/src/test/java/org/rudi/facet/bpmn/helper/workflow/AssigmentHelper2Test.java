/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.helper.workflow;

import java.util.Arrays;
import java.util.List;

import org.rudi.facet.bpmn.entity.workflow.AssetDescriptionEntity2Test;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component
public class AssigmentHelper2Test extends AbstractAssignmentHelper<AssetDescriptionEntity2Test> {

	@Override
	public List<String> computeAssignees(AssetDescriptionEntity2Test assetDescription, String roleName) {
		return Arrays.asList("test@test.com");
	}

	@Override
	public String computeAssignee(AssetDescriptionEntity2Test assetDescription, String roleName) {
		return "test@test.com";
	}

}
