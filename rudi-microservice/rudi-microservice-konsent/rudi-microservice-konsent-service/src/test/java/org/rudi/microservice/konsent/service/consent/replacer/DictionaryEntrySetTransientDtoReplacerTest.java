package org.rudi.microservice.konsent.service.consent.replacer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.rudi.microservice.konsent.storage.dao.data.DictionaryEntryDao;
import org.rudi.microservice.konsent.storage.entity.data.DictionaryEntryEntity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class DictionaryEntrySetTransientDtoReplacerTest {
	private final DictionaryEntryDao dictionaryEntryDao;

	protected List<DictionaryEntryEntity> saveLabels(Set<DictionaryEntryEntity> transientDtos) {
		List<DictionaryEntryEntity> savedLabels = new ArrayList<>();
		for (DictionaryEntryEntity dictionaryEntryEntity : transientDtos) {
			dictionaryEntryEntity.setUuid(UUID.randomUUID());
			savedLabels.add(dictionaryEntryDao.save(dictionaryEntryEntity));
		}
		return savedLabels;
	}

	protected Set<DictionaryEntryEntity> convertListToSet(List<DictionaryEntryEntity> list) {
		Set<DictionaryEntryEntity> dictionaryEntryEntitySet = new HashSet<>(list.size());
		dictionaryEntryEntitySet.addAll(list);
		return dictionaryEntryEntitySet;
	}
}
