package org.rudi.common.facade.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class RudiApplicationListener implements ApplicationListener<ApplicationReadyEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(RudiApplicationListener.class);

	/**
	 * Methode appelée à l'initilisation de l'application SpringBoot
	 *
	 * @param event
	 */
	@Override
	public void onApplicationEvent(final ApplicationReadyEvent event) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Args: {} {}", event.getArgs(), event.getSpringApplication().getWebApplicationType());
		}
	}

}
