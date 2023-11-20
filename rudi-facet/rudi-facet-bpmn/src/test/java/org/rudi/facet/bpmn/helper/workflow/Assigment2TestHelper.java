/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.helper.workflow;

import java.util.Arrays;
import java.util.List;

import org.rudi.facet.bpmn.entity.workflow.AssetDescription2TestEntity;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component
public class Assigment2TestHelper extends AbstractAssignmentHelper<AssetDescription2TestEntity> {

	@Override
	public List<String> computeAssignees(AssetDescription2TestEntity assetDescription, String roleName) {
		return Arrays.asList("test@test.com");
	}

	@Override
	public String computeAssignee(AssetDescription2TestEntity assetDescription, String roleName) {
		return "test@test.com";
	}

}
