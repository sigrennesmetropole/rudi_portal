/**
 * 
 */
package org.rudi.facet.email;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author fni18300
 *
 */
@Configuration
@Getter
public class EMailConfiguration {

	@Value("${mail.transport.protocol:smtp}")
	private String protocol;

	@Value("${mail.smtp.host}")
	private String host;

	@Value("${mail.smtp.auth:false}")
	private boolean authentification;

	@Value("${mail.smtp.port:25}")
	private int port;

	@Value("${mail.smtp.user:}")
	private String user;

	@Value("${mail.smtp.password:}")
	private String password;

	@Value("${mail.from:ne-pas-repondre@rudi.bzh}")
	private String defaultFrom;

	@Value("${mail.smtp.starttls.enable:false}")
	private boolean ttlsEnable;

	@Value("${mail.debug:false}")
	private boolean debug;
}
