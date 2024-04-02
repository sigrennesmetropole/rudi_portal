/**
 * RUDI Portail
 */
package org.rudi.facet.cms.impl.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author FNI18300
 *
 */
@Data
public class CmsMagnoliaNode {

	@Schema(name = "@name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("@name")
	private String name;

	@Schema(name = "@id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("@id")
	private String id;

	@Schema(name = "@nodeType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("@nodeType")
	private String nodeType;

	@Schema(name = "@path", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("@path")
	private String path;

	@Schema(name = "@nodes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("@nodes")
	private List<String> nodes;

	@Schema(name = "jcr:createdBy", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("jcr:createdBy")
	private String jcrCreatedBy;

	@Schema(name = "jcr:created", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("jcr:created")
	private LocalDateTime jcrCreate;

	@Schema(name = "mgnl:lastActivatedBy", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("mgnl:lastActivatedBy")
	private String lastActivatedBy;

	@Schema(name = "mgnl:lastActivated", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("mgnl:lastActivated")
	private LocalDateTime lastActivated;

	@Schema(name = "mgnl:createdBy", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("mgnl:createdBy")
	private String createdBy;

	@Schema(name = "mgnl:created", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("mgnl:created")
	private LocalDateTime created;

	@Schema(name = "mgnl:lastModifiedBy", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("mgnl:lastModifiedBy")
	private String lastModifiedBy;

	@Schema(name = "mgnl:lastModified", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("mgnl:lastModified")
	private LocalDateTime lastModified;

	@Schema(name = "mgnl:activationStatus", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("mgnl:activationStatus")
	private Boolean activationStatus;

}
