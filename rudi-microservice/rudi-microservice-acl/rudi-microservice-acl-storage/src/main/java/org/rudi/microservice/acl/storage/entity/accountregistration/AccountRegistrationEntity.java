package org.rudi.microservice.acl.storage.entity.accountregistration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.rudi.common.storage.entity.AbstractLongIdEntity;
import org.rudi.microservice.acl.core.common.SchemaConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * User entity
 */
@Entity
@Table(name = "account_registration", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
@ToString
public class AccountRegistrationEntity extends AbstractLongIdEntity {

	private static final long serialVersionUID = -6508639499690690560L;

	@Column(name = "login", length = 100, nullable = false)
	private String login;

	@Column(name = "password", length = 150, nullable = false)
	private String password;

	@Column(name = "lastname", length = 30)
	private String lastname;

	@Column(name = "firstname", length = 30)
	private String firstname;

	@Column(name = "creation_date")
	private LocalDateTime creationDate;

	@Column(name = "token")
	private String token;

	/**
	 * Peut-on contacter l'utilisateur sur son adresse mail ?
	 */
	@NotNull
	private boolean hasSubscribeToNotifications;

	public boolean hasSubscribeToNotifications() {
		return hasSubscribeToNotifications;
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
		if (!(obj instanceof AccountRegistrationEntity)) {
			return false;
		}
		AccountRegistrationEntity other = (AccountRegistrationEntity) obj;
		if (getId() != null && getId().equals(other.getId())) {
			return true;
		}
		if (getLogin() == null) {
			if (other.getLogin() != null) {
				return false;
			}
		} else if (!getLogin().equals(other.getLogin())) {
			return false;
		}
		return true;
	}

}
