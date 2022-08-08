package org.rudi.microservice.strukture.storage.entity.organization;

import lombok.Data;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
public class OrganizationMemberEntity implements Serializable {
	@NotNull
	private UUID userUuid;
	@NotNull
	@Enumerated(EnumType.STRING)
	private OrganizationRole role;
}
