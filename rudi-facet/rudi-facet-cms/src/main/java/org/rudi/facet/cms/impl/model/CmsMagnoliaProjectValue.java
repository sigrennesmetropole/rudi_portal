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
public class CmsMagnoliaProjectValue extends CmsMagnoliaJCRNode {

	@Schema(name = "shortimage", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("shortimage")
	private CmsMagnoliaImage shortimage;

	@Schema(name = "image", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("image")
	private CmsMagnoliaImage image;
}
