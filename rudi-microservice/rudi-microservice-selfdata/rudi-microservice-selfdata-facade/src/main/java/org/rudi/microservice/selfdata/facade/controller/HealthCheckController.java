package org.rudi.microservice.selfdata.facade.controller;

import org.rudi.microservice.selfdata.facade.controller.api.HealthCheckApi;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HealthCheckController implements HealthCheckApi, HealthIndicator {

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
