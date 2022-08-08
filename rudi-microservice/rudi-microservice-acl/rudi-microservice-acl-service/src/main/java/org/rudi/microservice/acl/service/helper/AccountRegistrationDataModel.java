/**
 * 
 */
package org.rudi.microservice.acl.service.helper;

import java.util.Locale;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.rudi.facet.generator.model.GenerationFormat;
import org.rudi.facet.generator.text.model.AbstractTemplateDataModel;
import org.rudi.microservice.acl.storage.entity.accountregistration.AccountRegistrationEntity;

/**
 * @author fni18300
 *
 */
public class AccountRegistrationDataModel extends AbstractTemplateDataModel {

	private AccountRegistrationEntity accountRegistration;
	private String urlServer;
	private String pathServer;

	public AccountRegistrationDataModel(AccountRegistrationEntity accountRegistration, String urlServer,
			String pathServer, @NotNull Locale locale, @NotNull String model) {
		super(GenerationFormat.HTML, locale, model);
		this.accountRegistration = accountRegistration;
		this.urlServer = urlServer;
		this.pathServer = pathServer;
	}

	@Override
	protected void fillDataModel(Map<String, Object> data) {
		data.put("account", accountRegistration);
		data.put("urlServer", urlServer);
		data.put("pathServer", pathServer);
	}

}
