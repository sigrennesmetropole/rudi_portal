package org.rudi.common.service.configuration.impl;

import org.rudi.common.core.ApplicationInformation;
import org.rudi.common.service.configuration.ConfigurationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationServiceImpl implements ConfigurationService {

	@Value("${application.version}")
	private String applicationVersion;

	@Value("${application.comment}")
	private String applicationComment;

	/**
	 * Permet de récupérer la configuration
	 *
	 * @return
	 */
	@Override
	public ApplicationInformation getApplicationInformation() {
		ApplicationInformation result = new ApplicationInformation();

		// Ajout de la version contenu dans le fichier properties
		result.setVersion(applicationVersion);
		result.setComment(applicationComment);

		return result;

	}

}
