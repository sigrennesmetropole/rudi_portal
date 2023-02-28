package org.rudi.facet.rva.impl;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.rudi.common.service.exception.AppServiceBadRequestException;
import org.rudi.facet.rva.AddressService;
import org.rudi.facet.rva.MonoUtils;
import org.rudi.facet.rva.RvaProperties;
import org.rudi.facet.rva.exception.ExternalApiRvaException;
import org.rudi.facet.rva.exception.TooManyAddressesException;
import org.rudi.rva.core.bean.Address;
import org.rudi.rva.core.bean.FullAddressesResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
	private final WebClient rvaWebClient;
	private final RvaProperties rvaProperties;

	@Override
	public List<Address> getFullAddresses(String query) throws AppServiceBadRequestException, ExternalApiRvaException, TooManyAddressesException {
		if (StringUtils.isEmpty(query) || query.length() < rvaProperties.getQueryMinLength()) {
			throw new AppServiceBadRequestException("Missing query parameter or query too short");
		}
		return getAddresses(query);
	}

	@Override
	public Address getAddressById(Integer idAddress) throws ExternalApiRvaException, TooManyAddressesException {
		final Mono<FullAddressesResponse> fullAddressesResponseMono = rvaWebClient.get()
				.uri(uriBuilder -> uriBuilder
						.queryParam("key", rvaProperties.getKey())
						.queryParam("version", rvaProperties.getVersion())
						.queryParam("epsg", rvaProperties.getEpsg())
						.queryParam("format", rvaProperties.getFormat())
						.queryParam("cmd", rvaProperties.getCommandAddressById())
						.queryParam("idaddress", idAddress).build())
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(FullAddressesResponse.class);
		var response = MonoUtils.blockOrThrow(fullAddressesResponseMono);
		if (response == null) {
			return null;
		}
		return response.getRva().getAnswer().getAddresses().get(0);
	}

	private List<Address> getAddresses(String query) throws ExternalApiRvaException, TooManyAddressesException {

		final Mono<FullAddressesResponse> fullAddressesResponseMono = rvaWebClient.get()
				.uri(uriBuilder -> uriBuilder
						.queryParam("key", rvaProperties.getKey())
						.queryParam("version", rvaProperties.getVersion())
						.queryParam("epsg", rvaProperties.getEpsg())
						.queryParam("format", rvaProperties.getFormat())
						.queryParam("cmd", rvaProperties.getCommandFullAddresses())
						.queryParam("query", query).build())
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(FullAddressesResponse.class);

		var response = MonoUtils.blockOrThrow(fullAddressesResponseMono);

		if (response == null) {
			return Collections.emptyList();
		}
		return response.getRva().getAnswer().getAddresses();
	}
}
