package org.rudi.microservice.strukture.core.bean;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

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
