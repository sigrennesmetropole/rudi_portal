package org.rudi.microservice.strukture.service.helper;

import java.util.Locale;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.rudi.facet.generator.model.GenerationFormat;
import org.rudi.facet.generator.text.model.AbstractTemplateDataModel;

public class UserOrganizationPasswordUpdateDataModel extends AbstractTemplateDataModel {
	private String urlServer;
	private String organisationName;

	UserOrganizationPasswordUpdateDataModel(String urlServer, String organisationName, @NotNull Locale locale, @NotNull String model) {
		super(GenerationFormat.HTML, locale, model);
		this.urlServer = urlServer;
		this.organisationName = organisationName;
	}

	@Override
	protected void fillDataModel(Map<String, Object> data) {
		data.put("organisationName", organisationName);
		data.put("urlServer", urlServer);
	}
}
