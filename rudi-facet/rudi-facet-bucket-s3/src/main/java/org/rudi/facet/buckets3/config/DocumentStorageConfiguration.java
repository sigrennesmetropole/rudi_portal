/**
 * 
 */
package org.rudi.facet.buckets3.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

/**
 * @author FNI18300
 *
 */
@Configuration
@ConfigurationProperties(prefix = "rudi.documentstorage")
@Getter
public class DocumentStorageConfiguration {

	private String providerId = "s3";

	private String endPoint;

	private String bucketName;

	private String identity;

	private String credential;

	private String threadCount = "1";

	private boolean ensureRoot = false;

	private boolean ensureBucket = true;

	private boolean trustAllCerts = false;

	private boolean deleteEnable = false;
}
