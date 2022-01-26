package org.rudi.microservice.kalim.service.config;

import org.rudi.common.storage.dao.StampedRepositoryImpl;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = { "org.rudi.common.storage.entity", "org.rudi.microservice.kalim.storage.entity" })
@EnableJpaRepositories(basePackages = {"org.rudi.common.storage.dao", "org.rudi.microservice.kalim.storage.dao"}, repositoryBaseClass = StampedRepositoryImpl.class)
public class KalimDatabaseConfiguration {

}
