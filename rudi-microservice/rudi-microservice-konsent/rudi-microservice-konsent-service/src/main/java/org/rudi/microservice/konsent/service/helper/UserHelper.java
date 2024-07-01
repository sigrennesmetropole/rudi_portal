package org.rudi.microservice.konsent.service.helper;

import org.rudi.common.service.helper.UtilContextHelper;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import static org.rudi.common.core.security.QuotedRoleCodes.MODULE_KONSENT;

@Component
@RequiredArgsConstructor
public class UserHelper {

	private final UtilContextHelper utilContextHelper;

	public boolean isAuthenticatedUserModuleAdministrator() {
		return utilContextHelper.hasRole(MODULE_KONSENT);
	}
}
