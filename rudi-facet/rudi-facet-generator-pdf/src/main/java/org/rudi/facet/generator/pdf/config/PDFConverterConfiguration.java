/**
 * 
 */
package org.rudi.facet.generator.pdf.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

/**
 * @author FNI18300
 *
 */
@Configuration
@ConfigurationProperties(prefix = "rudi.pdf")
@Getter
public class PDFConverterConfiguration {

	private String defaultFont = "fonts/Arial-MT.ttf";

	private String defaultBoldFont = "fonts/Arial-MT-Bold.ttf";

	private String ghostscriptArgs = "-dPDFA=1 -dFIXEDMEDIA -sProcessColorModel=DeviceRGB -sDEVICE=pdfwrite -dPDFACompatibilityPolicy=1";

	private boolean ghostscriptEnabled = false;
}
