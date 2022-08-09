package org.rudi.wso2.mediation;

import java.io.InputStream;

@FunctionalInterface
interface BodyReplacer {
	InputStream replaceBody(InputStream originalBody) throws Exception;
}
