package org.rudi.facet.cms;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
		"org.rudi.common.core",
		"org.rudi.common.service",
		"org.rudi.common.storage",
		"org.rudi.facet.cms",
})
public class SpringBootTestAppplication {
}
