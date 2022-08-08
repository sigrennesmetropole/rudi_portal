/**
 * 
 */
package org.rudi.microservice.acl.service.helper;

import java.util.Locale;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.rudi.facet.generator.model.GenerationFormat;
import org.rudi.facet.generator.text.model.AbstractTemplateDataModel;
import org.rudi.microservice.acl.storage.entity.user.UserEntity;

/**
 * @author fni18300
 *
 */
public class AccountCreationConfirmationDataModel extends AbstractTemplateDataModel {

	private UserEntity user;
	private String urlServer;

	public AccountCreationConfirmationDataModel(UserEntity user, String urlServer, @NotNull Locale locale,
			@NotNull String model) {
		super(GenerationFormat.HTML, locale, model);
		this.user = user;
		this.urlServer = urlServer;
	}

	@Override
	protected void fillDataModel(Map<String, Object> data) {
		data.put("user", user);
		data.put("urlServer", urlServer);
	}

}
