package org.rudi.wso2.mediation;

import org.wso2.carbon.apimgt.api.model.API;

interface PublicKeyComparator {
	boolean usesSamePublicKey(EncryptedMediaHandler encryptedMediaHandler, API engagedApi);
}
