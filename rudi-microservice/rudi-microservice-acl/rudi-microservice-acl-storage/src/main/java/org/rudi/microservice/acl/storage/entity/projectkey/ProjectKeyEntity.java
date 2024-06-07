package org.rudi.microservice.acl.storage.entity.projectkey;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.rudi.common.storage.entity.AbstractLongIdEntity;
import org.rudi.microservice.acl.core.common.SchemaConstants;
import org.rudi.microservice.acl.storage.entity.user.UserEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Conteneur d'une clé associée à un {@link ProjectKeystoreEntity}
 */
@Entity
@Table(name = "project_key", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
@ToString
public class ProjectKeyEntity extends AbstractLongIdEntity {

	private static final long serialVersionUID = -6508639499690690560L;

	@Column(name = "name", length = 256, nullable = false)
	private String name;

	@Column(name = "creation_date", nullable = false)
	private LocalDateTime creationDate;

	@Column(name = "expiration_date")
	private LocalDateTime expirationDate;

	@ManyToOne
	@JoinColumn(name = "user_fk")
	private UserEntity client;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(client, creationDate, expirationDate);
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
		if (!(obj instanceof ProjectKeyEntity)) {
			return false;
		}
		ProjectKeyEntity other = (ProjectKeyEntity) obj;
		return Objects.equals(client, other.client) && Objects.equals(creationDate, other.creationDate)
				&& Objects.equals(expirationDate, other.expirationDate);
	}

}
