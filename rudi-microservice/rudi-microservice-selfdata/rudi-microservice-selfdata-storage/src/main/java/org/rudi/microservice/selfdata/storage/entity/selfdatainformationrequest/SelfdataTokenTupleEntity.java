package org.rudi.microservice.selfdata.storage.entity.selfdatainformationrequest;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.rudi.common.storage.entity.AbstractLongIdEntity;
import org.rudi.microservice.selfdata.core.common.SchemaConstants;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * TokenTuple entity
 */
@Entity
@Table(name = "selfdata_token_tuple", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
@ToString
public class SelfdataTokenTupleEntity extends AbstractLongIdEntity {
	private static final long serialVersionUID = -6508639488540690560L;

	@Column(name = "token", nullable = false, unique = true)
	private UUID token;

	@Column(name = "dataset_uuid", nullable = false)
	private UUID datasetUuid;

	@Column(name = "user_uuid", nullable = false)
	private UUID userUuid;

	@Column(name = "node_provider_id", nullable = false)
	private UUID nodeProviderId;

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof SelfdataInformationRequestEntity)) {
			return false;
		}
		return super.equals(obj);
	}
}
