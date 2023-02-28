/**
 * 
 */
package org.rudi.facet.generator.docx.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author FNI18300
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetadataFieldNameDescription {

	private String name;

	private Class<?> type;

	private boolean multiple;
}
