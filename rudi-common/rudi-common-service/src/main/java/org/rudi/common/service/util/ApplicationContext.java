/**
 * RUDI
 */
package org.rudi.common.service.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component
public class ApplicationContext implements ApplicationContextAware {

	private static org.springframework.context.ApplicationContext applicationContext;

	private static void initializeApplicationContext(
			org.springframework.context.ApplicationContext applicationContext) {
		ApplicationContext.applicationContext = applicationContext;
	}

	@Override
	public void setApplicationContext(org.springframework.context.ApplicationContext applicationContext)
			throws BeansException {
		initializeApplicationContext(applicationContext);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		return (T) applicationContext.getBean(name);
	}

	public static <T> T getBean(Class<T> clazz) {
		return applicationContext.getBean(clazz);
	}

	public static String[] getBeanNames() {
		return applicationContext.getBeanDefinitionNames();
	}

}
