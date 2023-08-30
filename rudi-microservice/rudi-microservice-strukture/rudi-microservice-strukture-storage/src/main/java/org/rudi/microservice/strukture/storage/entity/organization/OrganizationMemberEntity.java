package org.rudi.microservice.strukture.storage.entity.organization;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Embeddable
@Data
public class OrganizationMemberEntity implements Serializable {

	public static final String FIELD_ID = "id";
	public static final String FIELD_USER_UUID = "userUuid";
	public static final String FIELD_ROLE = "role";

	private static final long serialVersionUID = 7622950489139578791L;

	@NotNull
	private UUID userUuid;
	@NotNull
	@Enumerated(EnumType.STRING)
	private OrganizationRole role;
	@NotNull
	private LocalDateTime addedDate;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof OrganizationMemberEntity)){
			return false;
		}
		final OrganizationMemberEntity that = (OrganizationMemberEntity) o;
		return userUuid.equals(that.getUserUuid());
	}
}
