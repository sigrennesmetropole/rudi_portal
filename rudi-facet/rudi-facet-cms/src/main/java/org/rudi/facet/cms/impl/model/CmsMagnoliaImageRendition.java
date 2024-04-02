/**
 * RUDI Portail
 */
package org.rudi.facet.cms.impl.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author FNI18300
 *
 */
@Data
public class CmsMagnoliaImageRendition {

	@Schema(name = "mimeType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("mimeType")
	private String mimeType;

	@Schema(name = "link", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("link")
	private String link;

}
