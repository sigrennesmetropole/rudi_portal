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
public class CmsMagnoliaLocalizedText extends CmsMagnoliaNode {

	@Schema(name = "locale", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("locale")
	private String locale;

	@Schema(name = "message", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("message")
	private String message;

}
