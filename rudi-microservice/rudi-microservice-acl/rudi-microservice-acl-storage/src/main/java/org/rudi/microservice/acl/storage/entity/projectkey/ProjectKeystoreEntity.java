package org.rudi.microservice.acl.storage.entity.projectkey;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.collections4.CollectionUtils;
import org.rudi.common.storage.entity.AbstractLongIdEntity;
import org.rudi.microservice.acl.core.common.SchemaConstants;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Conteneur de clés pour un projet
 */
@Entity
@Table(name = "project_keystore", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
@ToString
public class ProjectKeystoreEntity extends AbstractLongIdEntity {

	private static final long serialVersionUID = -6508639499690690560L;

	@Column(name = "project_uuid", nullable = false)
	private UUID projectUuid;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "project_keystore_fk")
	private Set<ProjectKeyEntity> projectKeys;

	public void addProjectKey(ProjectKeyEntity item) {
		if (getProjectKeys() == null) {
			setProjectKeys(new HashSet<>());
		}
		getProjectKeys().add(item);
	}

	public ProjectKeyEntity removeProjectKeyByUUID(UUID projetKeyUuid) {
		ProjectKeyEntity result = null;
		if (CollectionUtils.isNotEmpty(getProjectKeys())) {
			Iterator<ProjectKeyEntity> it = getProjectKeys().iterator();
			while (it.hasNext()) {
				ProjectKeyEntity item = it.next();
				if (item.getUuid().equals(projetKeyUuid)) {
					it.remove();
					break;
				}
			}
		}
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(getProjectUuid());
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
		if (!(obj instanceof ProjectKeystoreEntity)) {
			return false;
		}
		ProjectKeystoreEntity other = (ProjectKeystoreEntity) obj;
		return Objects.equals(getProjectUuid(), other.getProjectUuid());
	}

}
