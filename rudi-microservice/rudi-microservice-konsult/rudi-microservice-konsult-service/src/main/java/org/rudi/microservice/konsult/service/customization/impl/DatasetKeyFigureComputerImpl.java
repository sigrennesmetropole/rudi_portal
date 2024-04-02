package org.rudi.microservice.konsult.service.customization.impl;

import org.rudi.facet.kaccess.bean.DatasetSearchCriteria;
import org.rudi.facet.kaccess.bean.MetadataList;
import org.rudi.microservice.konsult.core.customization.KeyFigureData;
import org.rudi.microservice.konsult.core.customization.KeyFigureTypeData;
import org.rudi.microservice.konsult.service.customization.KeyFigureComputer;
import org.rudi.microservice.konsult.service.metadata.MetadataService;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class DatasetKeyFigureComputerImpl implements KeyFigureComputer {

	private final MetadataService metadataService;

	@Override
	public KeyFigureTypeData getAcceptedData() {
		return KeyFigureTypeData.DATASET;
	}

	@Override
	public void compute(KeyFigureData keyFigureData) {
		// comptage des datasets
		try {
			MetadataList container = metadataService
					.searchMetadatas(new DatasetSearchCriteria().limit(0).offset(0));
			Long datasetCount = container.getTotal();
			keyFigureData.setCount(datasetCount);
		} catch (Exception e) {
			log.error("Erreur lors de la récupération du nombre de jeux de données de la plateforme", e);
			keyFigureData.setCount(getDefaultKeyFigureValue());
		}
	}
}
