package org.rudi.microservice.konsult.service.apim;

import javax.net.ssl.SSLException;

import org.rudi.common.service.exception.AppServiceForbiddenException;
import org.rudi.facet.apimaccess.bean.Credentials;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.apimaccess.exception.GetClientRegistrationException;
import org.rudi.microservice.konsult.core.bean.ApiKeys;
import org.rudi.microservice.konsult.core.bean.ApiKeysType;

public interface ApimService {
	boolean hasEnabledApi(Credentials credentials) throws SSLException, AppServiceForbiddenException, GetClientRegistrationException;

	void enableApi(Credentials credentials) throws SSLException, APIManagerException, AppServiceForbiddenException;

	ApiKeys getKeys(ApiKeysType type, Credentials credentials) throws APIManagerException, AppServiceForbiddenException;
}
