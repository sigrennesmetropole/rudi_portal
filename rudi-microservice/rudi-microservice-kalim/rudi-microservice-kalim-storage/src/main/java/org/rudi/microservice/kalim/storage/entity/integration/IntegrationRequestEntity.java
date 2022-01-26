package org.rudi.microservice.kalim.storage.entity.integration;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.rudi.common.storage.entity.AbstractLongIdEntity;
import org.rudi.common.storage.entity.HibernateEntityHelper;
import org.rudi.microservice.kalim.core.bean.IntegrationStatus;
import org.rudi.microservice.kalim.core.bean.Method;
import org.rudi.microservice.kalim.core.bean.ProgressStatus;
import org.rudi.microservice.kalim.core.common.SchemaConstants;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "integration_request", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
@ToString
@NoArgsConstructor
@SuperBuilder
public class IntegrationRequestEntity extends AbstractLongIdEntity implements Serializable {

	private static final long serialVersionUID = -3932076897798766498L;

	// ça c'est la date de soumission de la demande - c'est comme une date de création
	@Column(name = "submission_date")
	private LocalDateTime submissionDate;

	/**
	 * La demande a été soumise par le moissonnage du nœud fournisseur ?
	 */
	@Column(name = "submitted_by_harvesting")
	private boolean submittedByHarvesting;

	// ça c'est la date de traitement de la demande
	@Column(name = "treatment_date")
	private LocalDateTime treatmentDate;

	// ça c'est la date d'envoie du rapport d'intégration
	@Column(name = "send_request_date")
	private LocalDateTime sendRequestDate;

	@Column(name = "method", nullable = false, length = 10)
	@Enumerated(EnumType.STRING)
	private Method method;

	@Column(name = "integration_status", length = 20)
	@Enumerated(EnumType.STRING)
	private IntegrationStatus integrationStatus;

	@Column(name = "progress_status", nullable = false, length = 20)
	@Enumerated(EnumType.STRING)
	private ProgressStatus progressStatus;

	/**
	 * Représentation JSON de la {@link org.rudi.facet.kaccess.bean.Metadata} concernée par cette requête
	 */
	@Lob
	@Column(name = "file")
	private String file;

	@Column(name = "rapport_transmission_attempts", nullable = false)
	@Builder.Default
	private int rapportTransmissionAttempts = 0;

	@Column(name = "resource_title")
	private String resourceTitle;

	@Column(name = "version", nullable = false, length = 10)
	private String version;

	@Column(name = "global_id", nullable = false)
	private UUID globalId;

	@Column(name = "node_provider_id", nullable = false)
	private UUID nodeProviderId;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "integration_request_fk")
	private Set<IntegrationRequestErrorEntity> errors;

	public void setErrors(Set<IntegrationRequestErrorEntity> errors) {
		HibernateEntityHelper.setCollection(this::getErrors, errors);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof IntegrationRequestEntity)) {
			return false;
		}
		return super.equals(obj);
	}
}
