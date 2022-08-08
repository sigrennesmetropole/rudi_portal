/**
 * 
 */
package org.rudi.facet.bpmn.bean.workflow;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.bpmn.entity.workflow.AssetDescriptionEntity;
import org.rudi.facet.bpmn.exception.InvalidDataException;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.bpmn.helper.workflow.AssignmentHelper;
import org.rudi.facet.generator.model.GenerationFormat;
import org.rudi.facet.generator.text.model.AbstractTemplateDataModel;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author FNI18300
 *
 */
@Slf4j
public class EMailDataModel<E extends AssetDescriptionEntity, A extends AssignmentHelper<E>>
		extends AbstractTemplateDataModel {

	private String roleName;

	@Getter
	private ExecutionEntity executionEntity;

	@Getter
	private FormHelper formHelper;

	@Getter
	private E assetDescription;

	private A assignmentHelper;

	private Map<String, Object> localDatas;

	/**
	 * @param assignmentHelper
	 * @param executionEntity
	 * @param assetDescriptionEntity
	 * @param roleName
	 * @param locale
	 * @param modele
	 */
	public EMailDataModel(A assignmentHelper, ExecutionEntity executionEntity, E assetDescriptionEntity,
			FormHelper formHelper, String roleName, Locale locale, String modele) {
		super(GenerationFormat.HTML, locale, modele);
		this.executionEntity = executionEntity;
		this.assetDescription = assetDescriptionEntity;
		this.roleName = roleName;
		this.assignmentHelper = assignmentHelper;
		this.formHelper = formHelper;
	}

	@Override
	protected String generateFileName() {
		return "emailBody.html";
	}

	public User getUser(String login) {
		User u = assignmentHelper.getUserByLogin(login);
		if (u != null) {
			if (u.getFirstname() == null) {
				u.setFirstname(StringUtils.EMPTY);
			}
			if (u.getLastname() == null) {
				u.setLastname(StringUtils.EMPTY);
			}
		}
		return u;
	}

	public void addData(String name, Object value) {
		if (localDatas == null) {
			localDatas = new HashMap<>();
		}
		localDatas.put(name, value);
	}

	@Override
	protected void fillDataModel(Map<String, Object> data) {
		data.put("assignmentHelper", assignmentHelper);
		data.put("execution", executionEntity);
		data.put("asset", assetDescription);
		data.put("roleName", roleName);
		if (localDatas != null) {
			data.putAll(localDatas);
		}
		if (assetDescription != null && StringUtils.isNotEmpty(assetDescription.getData())) {
			try {
				Map<String, Object> datas = formHelper.hydrateData(assetDescription.getData());
				if (MapUtils.isNotEmpty(datas)) {
					data.putAll(datas);
				}
			} catch (InvalidDataException e) {
				log.warn("Impossible de désérialiser les données de formulaires", e);
			}
		}
	}

}
