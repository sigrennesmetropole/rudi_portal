/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.helper.workflow;

import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.bpmn.dao.workflow.AssetDescription1TestDao;
import org.rudi.facet.bpmn.entity.workflow.AssetDescription1TestEntity;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.email.EMailService;
import org.rudi.facet.generator.text.impl.TemplateGeneratorImpl;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component(value = "workflowContext")
public class WorkflowTestContext
		extends AbstractWorkflowContext<AssetDescription1TestEntity, AssetDescription1TestDao, Assigment1TestHelper> {

	public WorkflowTestContext(EMailService eMailService, TemplateGeneratorImpl templateGenerator,
			AssetDescription1TestDao assetDescriptionDao, Assigment1TestHelper assignmentHelper, ACLHelper aclHelper,
			FormHelper formHelper) {
		super(eMailService, templateGenerator, assetDescriptionDao, assignmentHelper, aclHelper, formHelper);
	}

	public String getType() {
		return "accept";
	}
}
