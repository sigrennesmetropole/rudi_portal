package org.rudi.microservice.apigateway.storage.entity.api;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.collections4.CollectionUtils;
import org.rudi.common.storage.entity.AbstractLongIdEntity;
import org.rudi.microservice.apigateway.core.common.SchemaConstants;
import org.rudi.microservice.apigateway.storage.entity.throttling.ThrottlingEntity;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Apigateway entity
 */
@Entity
@Table(name = "api", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
@ToString
@EntityListeners(AuditingEntityListener.class)
public class ApiEntity extends AbstractLongIdEntity {

	private static final long serialVersionUID = -6508639499690690560L;

	@Column(name = "global_id", nullable = false)
	private UUID globalId;

	@Column(name = "provider_id")
	private UUID providerId;

	@Column(name = "producer_id")
	private UUID producerId;

	@Column(name = "node_provider_id")
	private UUID nodeProviderId;

	@Column(name = "media_id", nullable = false)
	private UUID mediaId;

	@Column(name = "contract", nullable = false, length = 25)
	private String contract;

	@Column(name = "url", nullable = false, length = 1024)
	private String url;

	@CreatedDate
	@Column(name = "creation_date", nullable = false)
	private LocalDateTime creationDate;

	@LastModifiedDate
	@Column(name = "updated_date", nullable = false)
	private LocalDateTime updatedDate;

	@ElementCollection
	@CollectionTable(name = "api_method", schema = SchemaConstants.DATA_SCHEMA, joinColumns = @JoinColumn(name = "api_fk"))
	@Enumerated(EnumType.STRING)
	private Set<ApiMethod> methods;

	@ManyToMany
	@JoinTable(name = "api_throttling", schema = SchemaConstants.DATA_SCHEMA, joinColumns = @JoinColumn(name = "api_fk"), inverseJoinColumns = @JoinColumn(name = "throttling_fk"))
	private Set<ThrottlingEntity> throttlings;

	/**
	 * Child entity example
	 */
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "api_fk", nullable = false)
	private Set<ApiParameterEntity> parameters;

	public void addThrottling(ThrottlingEntity throttling) {
		if (getThrottlings() == null) {
			setThrottlings(new HashSet<>());
		}
		getThrottlings().add(throttling);
	}

	public ThrottlingEntity lookupThrottlingByUuid(UUID throttlingUuid) {
		ThrottlingEntity result = null;
		if (CollectionUtils.isNotEmpty(getThrottlings())) {
			result = getThrottlings().stream().filter(t -> t.getUuid().equals(throttlingUuid)).findFirst().orElse(null);
		}
		return result;
	}

	public void addParameter(ApiParameterEntity apiParameter) {
		if (getParameters() == null) {
			setParameters(new HashSet<>());
		}
		getParameters().add(apiParameter);
	}

	public ApiParameterEntity lookupParameterByUuid(UUID apiParameterUuid) {
		ApiParameterEntity result = null;
		if (CollectionUtils.isNotEmpty(getParameters())) {
			result = getParameters().stream().filter(t -> t.getUuid().equals(apiParameterUuid)).findFirst()
					.orElse(null);
		}
		return result;
	}

	public ApiParameterEntity lookupParameterByName(String apiParameterName) {
		ApiParameterEntity result = null;
		if (CollectionUtils.isNotEmpty(getParameters())) {
			result = getParameters().stream().filter(t -> t.getName().equals(apiParameterName)).findFirst()
					.orElse(null);
		}
		return result;
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
		if (!(obj instanceof ApiEntity)) {
			return false;
		}
		return super.equals(obj);
	}

}
