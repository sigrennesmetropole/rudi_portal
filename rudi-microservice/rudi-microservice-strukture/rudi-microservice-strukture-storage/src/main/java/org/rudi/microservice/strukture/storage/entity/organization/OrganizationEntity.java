package org.rudi.microservice.strukture.storage.entity.organization;

import lombok.Getter;
import lombok.Setter;
import org.rudi.common.storage.entity.AbstractLongIdEntity;
import org.rudi.microservice.strukture.core.common.SchemaConstants;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * <ul>
 *     <li>Sa photo peut être déposée dans le Dataverse identique à celui utilisé pour les images des Providers</li>
 * </ul>
 */
@Entity
@Table(name = "organization", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
public class OrganizationEntity extends AbstractLongIdEntity {

	public static final String FIELD_MEMBERS = "members";

	private static final long serialVersionUID = -8031214852147803138L;

	@NotNull
	private String name;

	@NotNull
	private LocalDateTime openingDate;

	private LocalDateTime closingDate;

	@Column(name = "description", length = 800)
	private String description;

	@Column(name = "url", length = 80)
	private String url;

	/**
	 * Membres
	 */
	@ElementCollection
	@CollectionTable(name = "organization_member", schema = SchemaConstants.DATA_SCHEMA, joinColumns = @JoinColumn(name = "organization_fk"))
	private Set<OrganizationMemberEntity> members = new HashSet<>();

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), name);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof OrganizationEntity)) return false;
		if (!super.equals(o)) return false;
		final OrganizationEntity that = (OrganizationEntity) o;
		return Objects.equals(name, that.name);
	}

	@Override
	public String toString() {
		return "OrganizationEntity{" +
				"name='" + name + '\'' +
				'}';
	}
}
