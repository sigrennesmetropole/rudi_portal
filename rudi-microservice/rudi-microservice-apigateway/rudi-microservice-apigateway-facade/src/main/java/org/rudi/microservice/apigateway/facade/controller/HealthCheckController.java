package org.rudi.microservice.apigateway.facade.controller;

import org.rudi.microservice.apigateway.facade.controller.api.HealthCheckApi;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController implements HealthCheckApi, HealthIndicator {

	public HealthCheckController() {
		super();
	}

	@Override
	@ResponseBody
	public ResponseEntity<Void> checkHealth() {
		return ResponseEntity.ok().build();
	}

	@Override
	public Health health() {
		return Health.up().build();
	}

}
