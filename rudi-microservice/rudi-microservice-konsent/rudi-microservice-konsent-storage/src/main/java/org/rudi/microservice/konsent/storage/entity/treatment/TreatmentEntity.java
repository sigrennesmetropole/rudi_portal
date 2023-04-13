package org.rudi.microservice.konsent.storage.entity.treatment;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.rudi.common.storage.entity.AbstractLongIdEntity;
import org.rudi.microservice.konsent.core.common.SchemaConstants;
import org.rudi.microservice.konsent.storage.entity.common.OwnerType;
import org.rudi.microservice.konsent.storage.entity.common.TargetType;
import org.rudi.microservice.konsent.storage.entity.common.TreatmentStatus;
import org.rudi.microservice.konsent.storage.entity.treatmentversion.TreatmentVersionEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "treatment", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
public class
TreatmentEntity extends AbstractLongIdEntity {
	private static final long serialVersionUID = 4513827704992762762L;

	public static final String FIELD_VERSION = "versions";
	public static final String FIELD_OWNER_TYPE = "ownerType";
	public static final String FIELD_STATUS = "status";
	public static final String FIELD_OWNER_UUID = "ownerUuid";


	@Column(name = "owner_type", length = 20, nullable = false)
	@Enumerated(EnumType.STRING)
	private OwnerType ownerType;

	@Column(name = "owner_uuid", nullable = false)
	private UUID ownerUuid;

	@Column(name = "target_type", length = 20, nullable = false)
	@Enumerated(EnumType.STRING)
	private TargetType targetType;

	@Column(name = "name", nullable = false)
	private String name; // OUI

	@Column(name = "target_uuid", nullable = false)
	private UUID targetUuid;

	@Column(name = "status", length = 20, nullable = false)
	@Enumerated(EnumType.STRING)
	private TreatmentStatus status;

	@Column(name = "creation_date", nullable = false)
	private OffsetDateTime creationDate;

	@Column(name = "updated_date")
	private OffsetDateTime updatedDate;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "treatement_fk")
	private Set<TreatmentVersionEntity> versions = new HashSet<>();

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof TreatmentEntity)) {
			return false;
		}
		return super.equals(obj);
	}
}
