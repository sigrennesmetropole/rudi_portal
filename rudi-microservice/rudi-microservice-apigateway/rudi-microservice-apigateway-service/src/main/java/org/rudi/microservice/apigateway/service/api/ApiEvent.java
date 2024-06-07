/**
 * RUDI Portail
 */
package org.rudi.microservice.apigateway.service.api;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

/**
 * @author FNI18300
 *
 */
public class ApiEvent extends ApplicationEvent {

	private static final long serialVersionUID = 4369928451741007880L;

	@Getter
	private ApiEventMode mode;

	public ApiEvent(ApiEventMode mode, Object source) {
		super(source);
		this.mode = mode;
	}
}
