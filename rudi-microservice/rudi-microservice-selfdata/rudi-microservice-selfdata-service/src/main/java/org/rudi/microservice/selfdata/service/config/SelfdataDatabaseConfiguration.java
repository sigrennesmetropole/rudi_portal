package org.rudi.microservice.selfdata.service.config;

import org.rudi.common.storage.dao.StampedRepositoryImpl;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = {
		"org.rudi.common.storage.entity",
		"org.rudi.microservice.selfdata.storage.entity",
		"org.rudi.facet.bpmn.entity",
		"org.rudi.facet.doks",
})
@EnableJpaRepositories(basePackages = {
		"org.rudi.common.storage.dao",
		"org.rudi.microservice.selfdata.storage.dao",
		"org.rudi.facet.bpmn.dao",
		"org.rudi.facet.doks",
},
		repositoryBaseClass = StampedRepositoryImpl.class)
public class SelfdataDatabaseConfiguration {

}
