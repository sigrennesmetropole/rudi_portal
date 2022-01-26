package org.rudi.common.service.configuration;

import org.rudi.common.core.ApplicationInformation;

/**
 * Interface de gestion de la configuration
 */
public interface ConfigurationService {

	/**
	 * @return application information
	 */
	ApplicationInformation getApplicationInformation();

}
