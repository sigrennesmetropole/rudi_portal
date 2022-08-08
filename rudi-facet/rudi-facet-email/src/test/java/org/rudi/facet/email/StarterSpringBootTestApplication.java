package org.rudi.facet.email;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(scanBasePackages = { "org.rudi.facet.email" })
@PropertySource(value = { "classpath:email-test.properties" })
public class StarterSpringBootTestApplication {
	public static void main(String[] args) {
		SpringApplication.run(StarterSpringBootTestApplication.class, args);
	}
}
