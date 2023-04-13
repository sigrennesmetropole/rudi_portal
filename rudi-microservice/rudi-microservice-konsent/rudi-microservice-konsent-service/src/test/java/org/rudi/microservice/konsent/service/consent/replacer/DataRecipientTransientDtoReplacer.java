package org.rudi.microservice.konsent.service.consent.replacer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.konsent.core.bean.DataRecipient;
import org.rudi.microservice.konsent.core.bean.TreatmentVersion;
import org.rudi.microservice.konsent.service.mapper.data.DataRecipientMapper;
import org.rudi.microservice.konsent.storage.dao.data.DataRecipientDao;
import org.rudi.microservice.konsent.storage.dao.data.DictionaryEntryDao;
import org.springframework.stereotype.Component;

import lombok.val;

@Component
public class DataRecipientTransientDtoReplacer extends DictionaryEntrySetTransientDtoReplacer implements TransientDtoReplacer {
	private final DataRecipientDao dataRecipientDao;
	private final DataRecipientMapper dataRecipientMapper;

	public DataRecipientTransientDtoReplacer(DictionaryEntryDao dictionaryEntryDao, DataRecipientDao dataRecipientDao, DataRecipientMapper dataRecipientMapper) {
		super(dictionaryEntryDao);
		this.dataRecipientDao = dataRecipientDao;
		this.dataRecipientMapper = dataRecipientMapper;
	}


	@Override
	public void replaceDtoFor(TreatmentVersion treatmentVersion) throws AppServiceException {
		val dataRecipients = treatmentVersion.getDataRecipients();
		if (CollectionUtils.isNotEmpty(dataRecipients)) {
			final List<DataRecipient> savedDataRecipients = new ArrayList<>(dataRecipients.size());
			for (final DataRecipient dataRecipient : dataRecipients) {
				val dataRecipientEntity = dataRecipientMapper.dtoToEntity(dataRecipient);
				dataRecipientEntity.setUuid(UUID.randomUUID()); // savedDataRecipientEntity et dataRecipientEntity référence le même objet
				val savedDataRecipientEntity = dataRecipientDao.save(dataRecipientEntity);
				saveLabels(savedDataRecipientEntity.getLabels());
				savedDataRecipients.add(
						dataRecipientMapper.entityToDto(savedDataRecipientEntity)
				);
			}
			treatmentVersion.setDataRecipients(savedDataRecipients);
		}
	}
}
