/**
 * RUDI Portail
 */
package org.rudi.facet.cms.impl.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author FNI18300
 *
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CmsMagnoliaImage extends CmsMagnoliaNode {

	@Schema(name = "@link", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("@link")
	private String link;

	@Schema(name = "metadata", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("metadata")
	private CmsMagnoliaImageMetadata metadata;

	@Schema(name = "renditions", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("renditions")
	private Map<String, CmsMagnoliaImageRendition> renditions;

}
