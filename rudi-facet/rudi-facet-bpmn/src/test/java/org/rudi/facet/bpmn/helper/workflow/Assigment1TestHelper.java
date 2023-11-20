/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.helper.workflow;

import java.util.Arrays;
import java.util.List;

import org.rudi.facet.bpmn.entity.workflow.AssetDescription1TestEntity;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component
public class Assigment1TestHelper extends AbstractAssignmentHelper<AssetDescription1TestEntity> {

	@Override
	public List<String> computeAssignees(AssetDescription1TestEntity assetDescription, String roleName) {
		return Arrays.asList("test@test.com");
	}

	@Override
	public String computeAssignee(AssetDescription1TestEntity assetDescription, String roleName) {
		return "test@test.com";
	}

}
