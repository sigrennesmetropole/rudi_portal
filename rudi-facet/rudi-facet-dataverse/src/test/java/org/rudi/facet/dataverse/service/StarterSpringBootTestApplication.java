package org.rudi.facet.dataverse.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(scanBasePackages = {"org.rudi.facet.dataverse.api", "org.rudi.facet.dataverse.service", "org.rudi.facet.dataverse.helper"})
@PropertySource(value = { "classpath:dataverse-test.properties" })
public class StarterSpringBootTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(StarterSpringBootTestApplication.class, args);
    }
}
