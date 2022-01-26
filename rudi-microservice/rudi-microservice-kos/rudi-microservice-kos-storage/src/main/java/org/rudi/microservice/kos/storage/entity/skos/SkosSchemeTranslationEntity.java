package org.rudi.microservice.kos.storage.entity.skos;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.rudi.common.storage.entity.AbstractTranslationEntity;
import org.rudi.microservice.kos.core.common.SchemaConstants;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "skos_scheme_translation", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
@ToString
public class SkosSchemeTranslationEntity extends AbstractTranslationEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8498042231871466734L;

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
}
