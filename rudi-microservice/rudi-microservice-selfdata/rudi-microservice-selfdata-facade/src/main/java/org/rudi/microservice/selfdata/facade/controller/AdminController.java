/**
 * RUDI Portail
 */
package org.rudi.microservice.selfdata.facade.controller;

import static org.rudi.common.core.security.QuotedRoleCodes.ADMINISTRATOR;

import org.rudi.microservice.selfdata.facade.controller.api.AdminApi;
import org.rudi.microservice.selfdata.service.selfdata.SelfdataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

/**
 * @author FNI18300
 *
 */
@RestController
@RequiredArgsConstructor
public class AdminController implements AdminApi {

	private final SelfdataService selfdataService;

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ")")
	public ResponseEntity<Void> recryptSelfdataInformationRequest(String previousAliasKey) throws Exception {
		selfdataService.recryptSelfdataInformationRequest(previousAliasKey);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

}
