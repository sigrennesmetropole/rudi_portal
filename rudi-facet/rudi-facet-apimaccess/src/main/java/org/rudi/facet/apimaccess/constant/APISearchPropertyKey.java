package org.rudi.facet.apimaccess.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class APISearchPropertyKey {

    public static final String NAME = "name";
    public static final String VERSION = "version";
    public static final String DESCRIPTION = "description";
    public static final String PROVIDER_UUID = "provider_uuid";
    public static final String PROVIDER_CODE = "provider_code";
    public static final String GLOBAL_ID = "global_id";
    public static final String MEDIA_UUID = "media_uuid";
    public static final String INTERFACE_CONTRACT = "interface_contract";
    public static final String EXTENSION = "extension";
    public static final String STATUS = "status"; // cf param "query" de l'API "/apis" dans publisher-v1.yaml
}
