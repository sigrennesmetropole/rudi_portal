package org.rudi.tools.nodestub.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.text.MessageFormat;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "rudi.nodestub")
@Getter
@Setter
public class NodeStubConfiguration {
	private File reportsDirectory;
	private File resourcesDirectory;

	@Value("#{'${rudi.nodestub.errors.429}'.split(',')}")
	private List<String> errors429;

	/**
	 * {1} : Report UUID
	 */
	private MessageFormat reportsNameFormat = new MessageFormat("{0}.rpt");

	/**
	 * {1} : Resource UUID
	 */
	private MessageFormat resourcesNameFormat = new MessageFormat("{0}.json");

}
