/**
 * RUDI Portail
 */
package org.rudi.facet.cms.impl.model;

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
public class CmsMagnoliaCategory extends CmsMagnoliaNode {

	@Schema(name = "level", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("level")
	private String level;

	@Schema(name = "displayName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("displayName")
	private String displayName;

}
