package org.rudi.microservice.projekt.service.config;

import org.rudi.common.storage.dao.StampedRepositoryImpl;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = { "org.rudi.common.storage.entity", "org.rudi.facet.bpmn.entity",
		"org.rudi.microservice.projekt.storage.entity" })
@EnableJpaRepositories(basePackages = { "org.rudi.common.storage.dao", "org.rudi.facet.bpmn.dao",
		"org.rudi.microservice.projekt.storage.dao" }, repositoryBaseClass = StampedRepositoryImpl.class)
public class ProjectDatabaseConfiguration {

}
