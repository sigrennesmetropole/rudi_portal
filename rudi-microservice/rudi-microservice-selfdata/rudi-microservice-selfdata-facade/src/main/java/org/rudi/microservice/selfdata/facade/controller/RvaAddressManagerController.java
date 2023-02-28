package org.rudi.microservice.selfdata.facade.controller;

import java.util.List;

import org.rudi.common.service.exception.AppServiceBadRequestException;
import org.rudi.facet.rva.AddressService;
import org.rudi.facet.rva.exception.ExternalApiRvaException;
import org.rudi.facet.rva.exception.TooManyAddressesException;
import org.rudi.microservice.selfdata.facade.controller.api.RvaAddressManagerApi;
import org.rudi.rva.core.bean.Address;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RvaAddressManagerController implements RvaAddressManagerApi {
	private final AddressService addressService;

	@Override
	public ResponseEntity<List<Address>> getFullAddresses(String query) throws ExternalApiRvaException, AppServiceBadRequestException, TooManyAddressesException {
		var result = addressService.getFullAddresses(query);
		return ResponseEntity.ok(result);
	}

	@Override
	public ResponseEntity<Address> getAddressById(Integer addressId) throws Exception {
		return ResponseEntity.ok(addressService.getAddressById(addressId));
	}
}
