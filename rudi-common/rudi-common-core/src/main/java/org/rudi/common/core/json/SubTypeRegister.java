package org.rudi.common.core.json;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface SubTypeRegister {

	void addSubTypes(ObjectMapper objectMapper);

}
