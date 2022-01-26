package org.rudi.microservice.providers.core.bean;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

/**
 * 
 * @author FNI18300
 *
 */
@Data
public class ProviderSearchCriteria {

	private String code;

	private String label;

	private UUID nodeProviderUuid;

	private Boolean active;

	private Boolean full;

	private LocalDateTime dateDebut;

	private LocalDateTime dateFin;

}
