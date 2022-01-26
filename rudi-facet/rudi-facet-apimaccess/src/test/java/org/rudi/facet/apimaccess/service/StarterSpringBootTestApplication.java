package org.rudi.facet.apimaccess.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(scanBasePackages = {"org.rudi.facet.apimaccess.api", "org.rudi.facet.apimaccess.service",
        "org.rudi.facet.apimaccess.helper"})
@PropertySource(value = { "classpath:apimaccess-test.properties" })
public class StarterSpringBootTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(StarterSpringBootTestApplication.class, args);
    }
}
