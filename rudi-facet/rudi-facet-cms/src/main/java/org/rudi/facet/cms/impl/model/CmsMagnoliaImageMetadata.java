/**
 * RUDI Portail
 */
package org.rudi.facet.cms.impl.model;

import java.time.LocalDateTime;

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
public class CmsMagnoliaImageMetadata extends CmsMagnoliaNode {

	@Schema(name = "fileName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("fileName")
	private String fileName;

	@Schema(name = "mimeType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("mimeType")
	private String mimeType;

	@Schema(name = "fileSize", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("fileSize")
	private String fileSize;

	@Schema(name = "height", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("height")
	private String height;

	@Schema(name = "width", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("width")
	private String width;

	@Schema(name = "format", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("format")
	private String format;

	@Schema(name = "date", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("date")
	private LocalDateTime date;

	@Schema(name = "created", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("created")
	private LocalDateTime created;

	@Schema(name = "modified", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("modified")
	private LocalDateTime modified;

}
