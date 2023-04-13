package org.rudi.microservice.konsent.storage.entity.data;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.rudi.common.storage.entity.AbstractTranslationEntity;
import org.rudi.microservice.konsent.core.common.SchemaConstants;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "dictionary_entry", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
public class DictionaryEntryEntity extends AbstractTranslationEntity {
	private static final long serialVersionUID = -7899894847504752206L;
}
