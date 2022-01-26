package org.rudi.microservice.kos.core.bean;

import java.util.UUID;

import lombok.Data;

@Data
public class SimpleSkosConceptProjection {

	private UUID conceptId;

	private String conceptCode;

	private String conceptUri;

	private String conceptIcon;

	private String ofSchemeCode;

	private String conceptRole;

	private String text;

	public SimpleSkosConceptProjection(UUID conceptId, String conceptCode, String conceptUri, String conceptIcon,
			String ofSchemeCode, String conceptRole, String text) {
		super();
		this.conceptId = conceptId;
		this.conceptCode = conceptCode;
		this.conceptUri = conceptUri;
		this.conceptIcon = conceptIcon;
		this.ofSchemeCode = ofSchemeCode;
		this.conceptRole = conceptRole;
		this.text = text;
	}

}
