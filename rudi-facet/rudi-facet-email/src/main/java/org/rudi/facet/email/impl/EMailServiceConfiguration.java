/**
 * RUDI Portail
 */
package org.rudi.facet.email.impl;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.rudi.facet.email.EMailConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * @author FNI18300
 *
 */
@Component
@RequiredArgsConstructor
public class EMailServiceConfiguration {

	private final EMailConfiguration emailConfiguration;

	@Bean
	public JavaMailSenderImpl getJavaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setProtocol(emailConfiguration.getProtocol());
		mailSender.setHost(emailConfiguration.getHost());
		mailSender.setPort(emailConfiguration.getPort());
		mailSender.setDefaultEncoding(StandardCharsets.UTF_8.name());

		if (emailConfiguration.isAuthentification()) {
			mailSender.setUsername(emailConfiguration.getUser());
			mailSender.setPassword(emailConfiguration.getPassword());
		}

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", emailConfiguration.getProtocol());
		props.put("mail.smtp.auth", Boolean.toString(emailConfiguration.isAuthentification()));
		props.put("mail.smtp.starttls.enable", Boolean.toString(emailConfiguration.isTtlsEnable()));
		props.put("mail.debug", Boolean.toString(emailConfiguration.isDebug()));

		return mailSender;
	}
}
