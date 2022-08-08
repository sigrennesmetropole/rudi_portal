/**
 *
 */
package org.rudi.microservice.acl.storage.entity.address;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.rudi.common.storage.entity.AbstractLongIdEntity;
import org.rudi.microservice.acl.core.common.SchemaConstants;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * AbstractAddress Entity
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "abstract_address", schema = SchemaConstants.DATA_SCHEMA)
@Setter
@Getter
@ToString
public abstract class AbstractAddressEntity extends AbstractLongIdEntity implements Serializable {

	private static final long serialVersionUID = 1674111844242326124L;

	@Column(name = "type", nullable = false)
	@Enumerated(EnumType.STRING)
	private AddressType type;

	@ManyToOne
	@JoinColumn(name = "address_role_fk")
	private AddressRoleEntity addressRole;

	protected AbstractAddressEntity(AddressType type) {
		this.type = type;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		AbstractAddressEntity that = (AbstractAddressEntity) o;
		return type == that.type;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), type);
	}
}
