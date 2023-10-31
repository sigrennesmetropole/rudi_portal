package org.rudi.microservice.selfdata.service.helper.selfdatamatchingdata;


import java.util.List;

import org.rudi.microservice.selfdata.core.bean.SelfdataRequestAllowedAttachementType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "rudi.selfdata.attachement.allowed")
@Getter
@Setter
public class SelfdataRequestAttachementProperties {
	List<SelfdataRequestAllowedAttachementType> types;
}
