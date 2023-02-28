/**
 * RUDI Portail
 */
package org.rudi.facet.generator.pdf.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author FNI18300
 *
 */
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ValidationResultItem {

	private String errorCode;

	/**
	 * Error details
	 */
	private String details;

	/**
	 * false: this error can't be ignored; true: this error can be ignored
	 */
	private boolean isWarning;

	private Throwable throwable;

	/**
	 * The underlying cause if the ValidationError was caused by a Throwable.
	 */
	private Throwable cause;

	/**
	 * The page number on which the error happened, if known.
	 */
	private Integer pageNumber;

}
