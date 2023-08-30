/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.helper.workflow;

import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.bpmn.dao.workflow.AssetDescriptionDao1Test;
import org.rudi.facet.bpmn.entity.workflow.AssetDescriptionEntity1Test;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.email.EMailService;
import org.rudi.facet.generator.text.impl.TemplateGeneratorImpl;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component(value = "workflowContext")
public class WorkflowContextTest
		extends AbstractWorkflowContext<AssetDescriptionEntity1Test, AssetDescriptionDao1Test, AssigmentHelper1Test> {

	public WorkflowContextTest(EMailService eMailService, TemplateGeneratorImpl templateGenerator,
			AssetDescriptionDao1Test assetDescriptionDao, AssigmentHelper1Test assignmentHelper, ACLHelper aclHelper,
			FormHelper formHelper) {
		super(eMailService, templateGenerator, assetDescriptionDao, assignmentHelper, aclHelper, formHelper);
	}

	public String getType() {
		return "accept";
	}
}
