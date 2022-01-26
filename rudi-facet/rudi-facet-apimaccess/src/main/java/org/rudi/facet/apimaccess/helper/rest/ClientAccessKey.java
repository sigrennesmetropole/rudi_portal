package org.rudi.facet.apimaccess.helper.rest;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ClientAccessKey {

    private String clientId;
    private String clientSecret;
}
