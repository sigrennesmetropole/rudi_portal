package org.rudi.common.storage.entity;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@MappedSuperclass
@Setter
@Getter
@ToString
public abstract class AbstractTranslationEntity extends AbstractLongIdEntity {

	private static final long serialVersionUID = -7899894899904752206L;

	@Column(name = "lang", length = 10)
	private String lang;

	@Column(name = "text", length = 255)
	private String text;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(getLang());
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
		if (!(obj instanceof AbstractTranslationEntity)) {
			return false;
		}
		AbstractTranslationEntity other = (AbstractTranslationEntity) obj;
		return Objects.equals(getLang(), other.getLang());
	}

}
