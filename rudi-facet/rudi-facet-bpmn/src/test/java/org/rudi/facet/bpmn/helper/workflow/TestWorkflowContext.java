/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.helper.workflow;

import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.bpmn.dao.workflow.Test1AssetDescriptionDao;
import org.rudi.facet.bpmn.entity.workflow.Test1AssetDescriptionEntity;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.email.EMailService;
import org.rudi.facet.generator.text.impl.TemplateGeneratorImpl;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component(value = "workflowContext")
public class TestWorkflowContext
		extends AbstractWorkflowContext<Test1AssetDescriptionEntity, Test1AssetDescriptionDao, Test1AssigmentHelper> {

	public TestWorkflowContext(EMailService eMailService, TemplateGeneratorImpl templateGenerator,
			Test1AssetDescriptionDao assetDescriptionDao, Test1AssigmentHelper assignmentHelper, ACLHelper aclHelper,
			FormHelper formHelper) {
		super(eMailService, templateGenerator, assetDescriptionDao, assignmentHelper, aclHelper, formHelper);
	}

	public String getType() {
		return "accept";
	}
}
