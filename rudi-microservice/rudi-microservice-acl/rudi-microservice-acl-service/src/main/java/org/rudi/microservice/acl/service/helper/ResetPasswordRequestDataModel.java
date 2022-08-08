package org.rudi.microservice.acl.service.helper;

import org.rudi.facet.generator.model.GenerationFormat;
import org.rudi.facet.generator.text.model.AbstractTemplateDataModel;
import org.rudi.microservice.acl.storage.entity.accountupdate.ResetPasswordRequestEntity;

import javax.validation.constraints.NotNull;
import java.util.Locale;
import java.util.Map;

public class ResetPasswordRequestDataModel extends AbstractTemplateDataModel {
	private String urlServer;
	private String pathServer;
	private ResetPasswordRequestEntity resetPasswordRequestEntity;
	protected ResetPasswordRequestDataModel(ResetPasswordRequestEntity passwordEntity, String urlServer, String pathServer, @NotNull Locale locale, String model) {
		super(GenerationFormat.HTML, locale, model);
		resetPasswordRequestEntity = passwordEntity;
		this.urlServer = urlServer;
		this.pathServer = pathServer;
	}

	@Override
	protected void fillDataModel(Map<String, Object> data) {
		data.put("resetPasswordRequest", resetPasswordRequestEntity);
		data.put("urlServer", urlServer);
		data.put("pathServer", pathServer);
	}
}
