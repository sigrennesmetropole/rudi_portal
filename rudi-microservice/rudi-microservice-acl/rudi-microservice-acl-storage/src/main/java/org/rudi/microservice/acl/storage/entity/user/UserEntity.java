package org.rudi.microservice.acl.storage.entity.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;
import org.rudi.common.storage.entity.AbstractLongIdEntity;
import org.rudi.microservice.acl.core.common.SchemaConstants;
import org.rudi.microservice.acl.storage.entity.address.AbstractAddressEntity;
import org.rudi.microservice.acl.storage.entity.role.RoleEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

/**
 * User entity
 */
@Entity
@Table(name = "user", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
@ToString
public class UserEntity extends AbstractLongIdEntity {

	private static final long serialVersionUID = -6508639499690690560L;

	@Column(name = "login", length = 100, nullable = false)
	private String login;

	/**
	 * Mot de passe <b>encodé</b> de l'utilisateur.
	 * Cf PasswordHelper#encodePassword(java.lang.String).
	 */
	@Column(name = "password", length = 150, nullable = false)
	private String password;

	@Column(name = "type", nullable = false)
	@Enumerated(EnumType.STRING)
	private UserType type;

	@Column(name = "lastname", length = 30)
	private String lastname;

	@Column(name = "firstname", length = 30)
	private String firstname;

	@Column(name = "company", length = 100)
	private String company;

	@Column(name = "failed_attempt", nullable = false)
	private int failedAttempt;

	@Column(name = "last_failed_attempt")
	private LocalDateTime lastFailedAttempt;

	@Column(name = "last_connexion")
	private LocalDateTime lastConnexion;

	@Column(name = "account_locked", nullable = false)
	private boolean accountLocked;

	@ManyToMany
	@JoinTable(name = "user_role", schema = SchemaConstants.DATA_SCHEMA, joinColumns = @JoinColumn(name = "user_fk"), inverseJoinColumns = @JoinColumn(name = "role_fk"))
	private Set<RoleEntity> roles;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "user_fk")
	private Set<AbstractAddressEntity> addresses;

	/**
	 * Peut-on contacter l'utilisateur sur son adresse mail ?
	 */
	@NotNull
	private boolean hasSubscribeToNotifications;

	/**
	 * Supprime une addresse de la liste
	 * 
	 * @param uuid
	 */
	public AbstractAddressEntity removeAddress(UUID uuid) {
		AbstractAddressEntity result = null;
		if (CollectionUtils.isNotEmpty(getAddresses())) {
			Iterator<AbstractAddressEntity> it = getAddresses().iterator();
			while (it.hasNext()) {
				AbstractAddressEntity addressEntity = it.next();
				if (addressEntity.getUuid().equals(uuid)) {
					result = addressEntity;
					it.remove();
					break;
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * @param uuid
	 * @return
	 */
	public AbstractAddressEntity lookupAddress(UUID uuid) {
		AbstractAddressEntity result = null;
		if (CollectionUtils.isNotEmpty(getAddresses())) {
			result = getAddresses().stream().filter(address -> address.getUuid().equals(uuid)).findAny().orElse(null);
		}
		return result;
	}

	/**
	 * Enlève un role utilisateur de la liste
	 * 
	 * @param uuid l'uuid du role
	 */
	public RoleEntity removeRole(UUID uuid) {
		RoleEntity result = null;
		if (CollectionUtils.isNotEmpty(getRoles())) {
			Iterator<RoleEntity> it = getRoles().iterator();
			while (it.hasNext()) {
				RoleEntity roleEntity = it.next();
				if (roleEntity.getUuid().equals(uuid)) {
					result = roleEntity;
					it.remove();
					break;
				}
			}
		}
		return result;
	}

	/**
	 * Recherche un role dans la liste des roles de l'utilisateur, en fonction de son uuid
	 * 
	 * @param uuid
	 * @return le role s'il est trouvé dans la liste des roles de l'utilisateur
	 */
	public RoleEntity lookupRole(UUID uuid) {
		RoleEntity result = null;
		if (CollectionUtils.isNotEmpty(getRoles())) {
			result = getRoles().stream().filter(role -> role.getUuid().equals(uuid)).findAny().orElse(null);
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
		if (!(obj instanceof UserEntity)) {
			return false;
		}
		UserEntity other = (UserEntity) obj;
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

	/**
	 * Réinit of number of failed attempt
	 */
	public void resetFailedAttempt() {
		setFailedAttempt(0);
	}

	/**
	 * Increment failed attempt count
	 */
	public void incrementFailedAttempts() {
		setFailedAttempt(getFailedAttempt() + 1);
	}

	/**
	 * Lock the user account
	 */
	public void lockAccount() {
		setAccountLocked(true);
	}

	/**
	 * Unlock the user account
	 */
	public void unlockAccount() {
		setAccountLocked(false);
	}
}
