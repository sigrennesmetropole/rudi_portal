package org.rudi.microservice.konsult.service.map.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.BusinessException;
import org.rudi.facet.rva.AddressService;
import org.rudi.microservice.konsult.core.bean.LayerInformation;
import org.rudi.microservice.konsult.service.map.MapService;
import org.rudi.rva.core.bean.Address;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MapServiceImpl implements MapService {

	private final AddressService addressService;
	private final JsonResourceReader jsonResourceReader = new JsonResourceReader();
	private List<LayerInformation> datasetBaseLayers;
	private List<LayerInformation> localisationBaseLayers;

	@PostConstruct
	private void initBaseLayers() throws IOException {
		this.datasetBaseLayers = jsonResourceReader.readList("map/dataset-base-layers.json", LayerInformation.class);
		this.localisationBaseLayers = jsonResourceReader.readList("map/localisation-base-layers.json", LayerInformation.class);
	}

	@Override
	public List<LayerInformation> getDatasetBaseLayers() {
		return this.datasetBaseLayers;
	}

	@Override
	public List<LayerInformation> getLocalisationBaseLayers() {
		return this.localisationBaseLayers;
	}

	@Override
	public List<Address> searchAddresses(String input) throws AppServiceException {
		List<Address> addresses;
		try {
			addresses = addressService.getFullAddresses(input);
		} catch (BusinessException e) {
			// Dans le module map on ne veut pas avoir de business exception sur la recherche d'adresse
			log.error(e.getMessage());
			return Collections.emptyList();
		}
		return addresses;
	}
}
