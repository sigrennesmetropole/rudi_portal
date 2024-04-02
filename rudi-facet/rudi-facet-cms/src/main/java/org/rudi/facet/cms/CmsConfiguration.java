/**
 * 
 */
package org.rudi.facet.cms;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

/**
 * @author fni18300
 *
 */
@Configuration
@Getter
public class CmsConfiguration {

	@Value("${cms.url}")
	private String url;

	@Value("${cms.auth:false}")
	private boolean authentification;

	@Value("${cms.auth.user:}")
	private String user;

	@Value("${cms.auth.password:}")
	private String password;

}
