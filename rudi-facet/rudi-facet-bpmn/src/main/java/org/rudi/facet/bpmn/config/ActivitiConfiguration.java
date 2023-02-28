/**
 *
 */
package org.rudi.facet.bpmn.config;

import java.util.ArrayList;

import org.activiti.engine.cfg.AbstractProcessEngineConfigurator;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.history.HistoryLevel;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author FNI18300
 *
 */
@Configuration
public class ActivitiConfiguration extends AbstractProcessEngineConfigurator {

	@Autowired
	private PlatformTransactionManager transactionManager;

	@Value("${spring.datasource.url}")
	private String dataSourceURL;

	@Value("${spring.datasource.username}")
	private String dataSourceUsername;

	@Value("${spring.datasource.password}")
	private String dataSourcePassword;

	@Value("${spring.datasource.driver-class-name}")
	private String dataSourceDriver;

	@Value("${rudi.bpmn.schema:}")
	private String dataSourceSchema;

	@Value("${rudi.bpmn.schema.update:true}")
	private String dataSourceSchemaUpdate;

	@Value("${rudi.bpmn.historc.level:full}")
	private String historicLevel;

	@Bean
	public SpringProcessEngineConfiguration springProcessEngineConfiguration() {
		SpringProcessEngineConfiguration configuration = new SpringProcessEngineConfiguration();
		configuration.setTransactionManager(transactionManager);
		configuration.setEventListeners(new ArrayList<>());
		configuration.getEventListeners().add(new HookEventListener());
		configuration.addConfigurator(this);
		return configuration;
	}

	@Override
	public void beforeInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
		processEngineConfiguration.setJdbcUrl(dataSourceURL);
		processEngineConfiguration.setJdbcUsername(dataSourceUsername);
		processEngineConfiguration.setJdbcPassword(dataSourcePassword);
		processEngineConfiguration.setJdbcDriver(dataSourceDriver);
		processEngineConfiguration.setJdbcPingEnabled(false);
		processEngineConfiguration.setJdbcPingQuery("select 1;");
		processEngineConfiguration.setDatabaseSchemaUpdate(dataSourceSchemaUpdate);
		if (StringUtils.isNotEmpty(dataSourceSchema)) {
			processEngineConfiguration.setDatabaseSchema(dataSourceSchema);
		}
		if (StringUtils.isNotEmpty(historicLevel)) {
			processEngineConfiguration.setHistoryLevel(HistoryLevel.getHistoryLevelForKey(historicLevel));
		}
	}

}
