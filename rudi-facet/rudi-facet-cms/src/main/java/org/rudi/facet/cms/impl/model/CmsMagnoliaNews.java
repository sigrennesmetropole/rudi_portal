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
public class CmsMagnoliaNews extends CmsMagnoliaJCRNode {

	@Schema(name = "image1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("image1")
	private CmsMagnoliaImage image1;

	@Schema(name = "image2", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@JsonProperty("image2")
	private CmsMagnoliaImage image2;
}
