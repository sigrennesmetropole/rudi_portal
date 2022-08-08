package org.rudi.microservice.strukture.storage.entity.provider;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;
import org.rudi.common.storage.entity.AbstractStampedEntity;
import org.rudi.microservice.strukture.core.common.SchemaConstants;
import org.rudi.microservice.strukture.storage.entity.address.AbstractAddressEntity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

/**
 * Providers entity
 */
@Entity
@Table(name = "provider", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
@ToString
public class ProviderEntity extends AbstractStampedEntity {

	private static final long serialVersionUID = -6508639499690690560L;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "provider_fk")
	private Set<AbstractAddressEntity> addresses;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "provider_fk")
	private Set<NodeProviderEntity> nodeProviders;

	/**
	 * Supprime un noeud de la liste
	 * 
	 * @param uuid
	 */
	public NodeProviderEntity removeNodeProvider(UUID uuid) {
		NodeProviderEntity result = null;
		if (CollectionUtils.isNotEmpty(getNodeProviders())) {
			Iterator<NodeProviderEntity> it = getNodeProviders().iterator();
			while (it.hasNext()) {
				NodeProviderEntity nodeProviderEntity = it.next();
				if (nodeProviderEntity.getUuid().equals(uuid)) {
					result = nodeProviderEntity;
					it.remove();
					break;
				}
			}
		}
		return result;
	}

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
	public NodeProviderEntity lookupNodeProvider(UUID uuid) {
		NodeProviderEntity result = null;
		if (CollectionUtils.isNotEmpty(getNodeProviders())) {
			result = getNodeProviders().stream().filter(nodeProvider -> nodeProvider.getUuid().equals(uuid)).findAny()
					.orElse(null);
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

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ProviderEntity)) {
			return false;
		}
		return super.equals(obj);
	}

}
