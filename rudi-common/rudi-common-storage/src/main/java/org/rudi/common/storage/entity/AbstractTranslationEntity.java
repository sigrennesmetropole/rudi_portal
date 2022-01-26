package org.rudi.common.storage.entity;

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

}
