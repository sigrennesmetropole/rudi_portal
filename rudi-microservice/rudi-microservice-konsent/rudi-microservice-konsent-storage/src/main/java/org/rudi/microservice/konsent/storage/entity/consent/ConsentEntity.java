package org.rudi.microservice.konsent.storage.entity.consent;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.rudi.common.storage.entity.AbstractLongIdEntity;
import org.rudi.microservice.konsent.core.common.SchemaConstants;
import org.rudi.microservice.konsent.storage.entity.common.OwnerType;
import org.rudi.microservice.konsent.storage.entity.treatment.TreatmentEntity;
import org.rudi.microservice.konsent.storage.entity.treatmentversion.TreatmentVersionEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Consent entity
 */
@Entity
@Table(name = "consent", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ConsentEntity extends AbstractLongIdEntity {

	private static final long serialVersionUID = -6508639499690690560L;

	@Column(name = "owner_type", length = 20, nullable = false)
	@Enumerated(EnumType.STRING)
	private OwnerType ownerType;

	@Column(name = "owner_uuid", nullable = false)
	private UUID ownerUuid;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "treatment_fk")
	private TreatmentEntity treatment;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "treatment_version_fk")
	private TreatmentVersionEntity treatmentVersion;

	@Column(name = "consent_date", nullable = false)
	private OffsetDateTime consentDate;

	@Column(name = "expiration_date")
	private OffsetDateTime expirationDate;

	@Column(name = "storage_key", nullable = false, length = 1024)
	private String storageKey;

	@Column(name = "consent_hash", nullable = false, length = 512)
	private String consentHash;

	@Column(name = "revoke_hash", length = 512)
	private String revokeHash;

	public ConsentEntity(ConsentEntity source) {
		setConsentDate(source.getConsentDate());
		setConsentHash(source.getConsentHash());
		setRevokeHash(source.getRevokeHash());
		setExpirationDate(source.getExpirationDate());
		setId(source.getId());
		setOwnerType(source.getOwnerType());
		setOwnerUuid(source.getOwnerUuid());
		setStorageKey(source.getStorageKey());
		setTreatment(source.getTreatment());
		setTreatmentVersion(source.getTreatmentVersion());
		setUuid(source.getUuid());
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
		if (!(obj instanceof ConsentEntity)) {
			return false;
		}
		return super.equals(obj);
	}
}
