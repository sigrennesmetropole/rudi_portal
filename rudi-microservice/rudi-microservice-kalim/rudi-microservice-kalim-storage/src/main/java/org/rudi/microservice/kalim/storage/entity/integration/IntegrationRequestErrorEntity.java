package org.rudi.microservice.kalim.storage.entity.integration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.rudi.common.core.Coded;
import org.rudi.common.storage.entity.AbstractLongIdEntity;
import org.rudi.microservice.kalim.core.common.SchemaConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "integration_request_error", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
@ToString
public class IntegrationRequestErrorEntity extends AbstractLongIdEntity implements Coded {

	private static final long serialVersionUID = 290472601767597688L;

	public IntegrationRequestErrorEntity() {

	}

	public IntegrationRequestErrorEntity(String code, String message) {
		this(UUID.randomUUID(), code, message, null, LocalDateTime.now());
	}

	public IntegrationRequestErrorEntity(UUID uuid, String code, String message, String fieldName, LocalDateTime errorDate) {
		this.code = code;
		this.message = message;
		this.fieldName = fieldName;
		this.errorDate = errorDate;
		setUuid(uuid);
	}


	@Column(name = "code", length = 10, nullable = false)
	private String code;

	@Column(name = "message", length = 1024)
	private String message;

	@Column(name = "field_name")
	private String fieldName;

	@Column(name = "error_date")
	private LocalDateTime errorDate;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((getCode() == null) ? 0 : getCode().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof IntegrationRequestErrorEntity)) {
			return false;
		}
		IntegrationRequestErrorEntity other = (IntegrationRequestErrorEntity) obj;
		if (getCode() == null) {
			return other.getCode() == null;
		} else {
			return getCode().equals(other.getCode());
		}
	}

}
