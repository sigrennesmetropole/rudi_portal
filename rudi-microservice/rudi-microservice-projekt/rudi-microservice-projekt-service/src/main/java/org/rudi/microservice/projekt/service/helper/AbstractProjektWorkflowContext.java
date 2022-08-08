/**
 * RUDI Portail
 */
package org.rudi.microservice.projekt.service.helper;

import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.bpmn.bean.workflow.EMailDataModel;
import org.rudi.facet.bpmn.dao.workflow.AssetDescriptionDao;
import org.rudi.facet.bpmn.entity.workflow.AssetDescriptionEntity;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.bpmn.helper.workflow.AbstractWorkflowContext;
import org.rudi.facet.bpmn.helper.workflow.AssignmentHelper;
import org.rudi.facet.email.EMailService;
import org.rudi.facet.generator.text.impl.TemplateGeneratorImpl;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author FNI18300
 *
 */
public abstract class AbstractProjektWorkflowContext<E extends AssetDescriptionEntity, D extends AssetDescriptionDao<E>, A extends AssignmentHelper<E>>
		extends AbstractWorkflowContext<E, D, A> {

	@Value("${email.urlServer:}")
	private String urlServer;

	protected AbstractProjektWorkflowContext(EMailService eMailService, TemplateGeneratorImpl templateGenerator,
			D assetDescriptionDao, A assignmentHelper, ACLHelper aclHelper, FormHelper formHelper) {
		super(eMailService, templateGenerator, assetDescriptionDao, assignmentHelper, aclHelper, formHelper);
	}

	@Override
	protected void addEmailDataModelData(EMailDataModel<E, A> eMailDataModel) {
		eMailDataModel.addData("urlServer", urlServer);
	}

}
