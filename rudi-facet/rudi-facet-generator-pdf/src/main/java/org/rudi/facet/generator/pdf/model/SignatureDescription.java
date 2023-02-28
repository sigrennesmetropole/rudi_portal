/**
 *
 */
package org.rudi.facet.generator.pdf.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author FNI18300
 *
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignatureDescription {

	@Builder.Default
	private String fieldname = "signature";

	private String name;

	private String reason;

	private String location;

	private LocalDateTime date;

	private VisibleSignatureDescription visibleSignatureDescription;

	/**
	 * Constructeur pour SignatureDescription
	 * 
	 * @param reason
	 * @param location
	 */
	public SignatureDescription(String name, String reason, String location) {
		this.name = name;
		this.reason = reason;
		this.location = location;
	}

}
