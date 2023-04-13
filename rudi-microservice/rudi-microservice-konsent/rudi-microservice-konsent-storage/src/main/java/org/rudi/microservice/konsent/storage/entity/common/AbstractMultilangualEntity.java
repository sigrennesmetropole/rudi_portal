package org.rudi.microservice.konsent.storage.entity.common;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.MappedSuperclass;

import org.rudi.common.storage.entity.AbstractLongIdEntity;
import org.rudi.microservice.konsent.storage.entity.data.DictionaryEntryEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@MappedSuperclass
@Setter
@Getter
@ToString
public abstract class AbstractMultilangualEntity extends AbstractLongIdEntity {
	public static final String CODE_COLUMN_NAME = "code";
	public static final int CODE_COLUMN_LENGTH = 30;
	private static final long serialVersionUID = 3642619521998137595L;

	@Column(name = CODE_COLUMN_NAME, length = CODE_COLUMN_LENGTH, nullable = false)
	private String code;

	@ManyToMany(fetch = FetchType.EAGER)
	private Set<DictionaryEntryEntity> labels = new HashSet<>();

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof AbstractMultilangualEntity)) {
			return false;
		}
		return super.equals(obj);
	}
}

