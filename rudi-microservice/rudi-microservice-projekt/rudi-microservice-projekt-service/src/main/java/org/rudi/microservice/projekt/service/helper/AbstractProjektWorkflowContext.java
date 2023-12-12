/**
 * RUDI Portail
 */
package org.rudi.microservice.projekt.service.helper;

import java.time.LocalDateTime;
import java.util.Map;

import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.bpmn.bean.workflow.EMailDataModel;
import org.rudi.facet.bpmn.dao.workflow.AssetDescriptionDao;
import org.rudi.facet.bpmn.entity.workflow.AssetDescriptionEntity;
import org.rudi.facet.bpmn.exception.InvalidDataException;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.bpmn.helper.workflow.AbstractWorkflowContext;
import org.rudi.facet.bpmn.helper.workflow.AssignmentHelper;
import org.rudi.facet.email.EMailService;
import org.rudi.facet.generator.text.impl.TemplateGeneratorImpl;
import org.springframework.beans.factory.annotation.Value;

import lombok.extern.slf4j.Slf4j;

/**
 * @author FNI18300
 *
 */
@Slf4j
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

	public E injectData(ExecutionEntity executionEntity, String key, Object value) throws AppServiceException {
		E assetDescriptionEntity = lookupAssetDescriptionEntity(executionEntity);
		try {
			log.debug("Try to populate asset with ({}{})", key, value);
			if (assetDescriptionEntity != null) {
				Map<String, Object> map = getFormHelper().hydrateData(assetDescriptionEntity.getData());
				map.put(key, value);
				assetDescriptionEntity.setData(getFormHelper().deshydrateData(map));
			}
		} catch (InvalidDataException e) {
			throw new AppServiceException("Une erreur est survenue lors de l'injection de données dans l'asset", e);
		}
		return assetDescriptionEntity;
	}

	public LocalDateTime getCurrentLocalDateTime() {
		return LocalDateTime.now();
	}
}
