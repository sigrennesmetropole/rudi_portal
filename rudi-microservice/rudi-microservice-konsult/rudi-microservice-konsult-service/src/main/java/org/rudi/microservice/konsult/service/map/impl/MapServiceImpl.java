package org.rudi.microservice.konsult.service.map.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.BusinessException;
import org.rudi.common.service.exception.ExternalServiceException;
import org.rudi.common.service.util.MonoUtils;
import org.rudi.facet.rva.AddressService;
import org.rudi.microservice.konsult.core.bean.Bbox;
import org.rudi.microservice.konsult.core.bean.EpsgIoProjection;
import org.rudi.microservice.konsult.core.bean.EpsgIoResponse;
import org.rudi.microservice.konsult.core.bean.LayerInformation;
import org.rudi.microservice.konsult.core.bean.Proj4Information;
import org.rudi.microservice.konsult.service.map.MapService;
import org.rudi.rva.core.bean.Address;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MapServiceImpl implements MapService {

	private final AddressService addressService;
	private final JsonResourceReader jsonResourceReader = new JsonResourceReader();
	private final WebClient epsgIoWebClient;
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

	@Override
	public Proj4Information searchProjectionInformation(String epsgCode) throws AppServiceException {

		final Mono<EpsgIoResponse> epsgProjectionsMono = epsgIoWebClient.get()
				.uri(uriBuilder -> uriBuilder
						.queryParam("q", epsgCode)
						.queryParam("format", "json")
						.build())
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(EpsgIoResponse.class);

		var response = MonoUtils.blockOrThrow(epsgProjectionsMono, ExternalServiceException.class);

		if (response == null) {
			throw new AppServiceException("Erreur lors de l'appel de EPSG.io avec : " + epsgCode);
		}

		if (CollectionUtils.isEmpty(response.getResults())) {
			return null;
		}

		if (response.getResults().size() > 1) {
			String codeNames = response.getResults().stream().map(EpsgIoProjection::getCode).collect(Collectors.joining(","));
			throw new AppServiceException("Erreur plusieurs codes epsg trouvés pour  " + epsgCode + ". Précisement : " + codeNames);
		}

		EpsgIoProjection epsgProjection = response.getResults().get(0);
		Bbox bbox = new Bbox();
		// format de retour [minLat, minLon, maxLat, MaxLon] donc faut réagencer
		bbox.setSouthLatitude(epsgProjection.getBbox().get(0));
		bbox.setWestLongitude(epsgProjection.getBbox().get(1));
		bbox.setNorthLatitude(epsgProjection.getBbox().get(2));
		bbox.setEastLongitude(epsgProjection.getBbox().get(3));

		return new Proj4Information()
				.proj4(epsgProjection.getProj4())
				.bbox(bbox)
				.code(epsgProjection.getCode());
	}
}
