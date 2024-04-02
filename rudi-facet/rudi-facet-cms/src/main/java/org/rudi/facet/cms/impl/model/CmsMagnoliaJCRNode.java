/**
 * RUDI Portail
 */
package org.rudi.facet.cms.impl.model;

import java.util.List;

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
public class CmsMagnoliaJCRNode extends CmsMagnoliaNode {

	@Schema(name = "name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("name")
	private String name;

	@Schema(name = "categories", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("categories")
	private List<String> categories;

	@Schema(name = "jcr:mixinTypes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("jcr:mixinTypes")
	private List<String> mixinTypes;

	@Schema(name = "jcr:predecessors", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("jcr:predecessors")
	private List<String> predecessors;

	@Schema(name = "jcr:baseVersion", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("jcr:baseVersion")
	private String baseVersion;

	@Schema(name = "mgnl:comment", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("mgnl:comment")
	private String comment;

	@Schema(name = "jcr:versionHistory", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("jcr:versionHistory")
	private String versionHistory;

	@Schema(name = "jcr:isCheckedOut", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("jcr:isCheckedOut")
	private Boolean isCheckedOut;

}
