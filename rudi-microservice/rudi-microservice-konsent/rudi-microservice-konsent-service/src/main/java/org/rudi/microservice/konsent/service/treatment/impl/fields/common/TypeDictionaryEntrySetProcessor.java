package org.rudi.microservice.konsent.service.treatment.impl.fields.common;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.konsent.storage.dao.data.DictionaryEntryDao;
import org.rudi.microservice.konsent.storage.entity.data.DictionaryEntryEntity;
import org.rudi.microservice.konsent.storage.entity.treatmentversion.TreatmentVersionEntity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class TypeDictionaryEntrySetProcessor {
	protected final DictionaryEntryDao dictionaryEntryDao;

	protected abstract class TypeDictionaryEntrySetEntityReplacer
			extends TransientEntitiesReplacer<TreatmentVersionEntity, Set<DictionaryEntryEntity>> {

		protected TypeDictionaryEntrySetEntityReplacer(
				Function<TreatmentVersionEntity, Set<DictionaryEntryEntity>> entitiesGetter,
				BiConsumer<TreatmentVersionEntity, Set<DictionaryEntryEntity>> entitiesSetter) {
			super(entitiesGetter, entitiesSetter);
		}

		@Nullable
		@Override
		protected Set<DictionaryEntryEntity> getPersistentEntities(
				@Nullable Set<DictionaryEntryEntity> transientEntities) throws AppServiceException {
			if (transientEntities == null) {
				return null;
			}
			final Set<DictionaryEntryEntity> existingDictionaryEntries = new HashSet<>(transientEntities.size());
			for (final DictionaryEntryEntity dictionaryEntry : transientEntities) {
				final var existingDictionaryEntry = createDictionaryEntryEntity(dictionaryEntry);
				existingDictionaryEntries.add(existingDictionaryEntry);
			}
			return existingDictionaryEntries;
		}

		private DictionaryEntryEntity createDictionaryEntryEntity(DictionaryEntryEntity dictionaryEntry) {
			dictionaryEntry.setUuid(UUID.randomUUID());
			return dictionaryEntryDao.save(dictionaryEntry);
		}
	}
}
