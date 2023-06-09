/**
 * 
 */
package org.rudi.facet.generator.pdf.model;

import java.awt.Rectangle;

import org.rudi.common.core.DocumentContent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author FNI18300
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class VisibleSignatureDescription {

	private int page;

	private Rectangle rectangle;

	private DocumentContent image;

}
