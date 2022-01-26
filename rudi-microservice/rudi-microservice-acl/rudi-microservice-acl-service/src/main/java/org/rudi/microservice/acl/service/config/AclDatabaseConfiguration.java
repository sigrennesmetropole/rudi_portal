package org.rudi.microservice.acl.service.config;

import org.rudi.common.storage.dao.StampedRepositoryImpl;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = { "org.rudi.common.storage.entity", "org.rudi.microservice.acl.storage.entity" })
@EnableJpaRepositories(basePackages = {"org.rudi.common.storage.dao", "org.rudi.microservice.acl.storage.dao"}, repositoryBaseClass = StampedRepositoryImpl.class)
public class AclDatabaseConfiguration {

}
