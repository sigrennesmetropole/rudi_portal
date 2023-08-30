package org.rudi.microservice.konsult.facade.controller;

import java.util.List;

import org.rudi.microservice.konsult.core.bean.LayerInformation;
import org.rudi.microservice.konsult.facade.controller.api.MapApi;
import org.rudi.microservice.konsult.service.map.MapService;
import org.rudi.rva.core.bean.Address;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MapController implements MapApi {
	private final MapService mapService;

	@Override
	public ResponseEntity<List<LayerInformation>> getDatasetBaseLayers() throws Exception {
		return ResponseEntity.ok(mapService.getDatasetBaseLayers());
	}

	@Override
	public ResponseEntity<List<LayerInformation>> getLocalisationBaseLayers() throws Exception {
		return ResponseEntity.ok(mapService.getLocalisationBaseLayers());
	}

	@Override
	public ResponseEntity<List<Address>> searchAddresses(String query) throws Exception {
		return ResponseEntity.ok(mapService.searchAddresses(query));
	}
}
