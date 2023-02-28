package org.rudi.facet.rva;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rudi.common.service.exception.AppServiceBadRequestException;
import org.rudi.facet.rva.exception.ExternalApiRvaException;
import org.rudi.facet.rva.exception.TooManyAddressesException;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.RequiredArgsConstructor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RvaSpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AddressServiceTest {

	private final AddressService addressService;

	@DisplayName("Taille de query trop petite")
	@Test
	void getFullAddressesOneCharacterQuery() {
		assertThatThrownBy(() -> addressService.getFullAddresses("a"))
				.isInstanceOf(AppServiceBadRequestException.class);
	}

	@DisplayName("Aucune addresse ne matche avec la query")
	@Test
	void getFullAddressesIncorrectAddress() throws ExternalApiRvaException, AppServiceBadRequestException, TooManyAddressesException {
		assertThat(addressService.getFullAddresses("spod"))
				.isEmpty();
	}

	@DisplayName(" L'API renvoie bien des addresses")
	@Test
	void getFullAddressesRightAddress() throws ExternalApiRvaException, AppServiceBadRequestException, TooManyAddressesException {
		assertThat(addressService.getFullAddresses("103 Boulevard"))
				.isNotEmpty();
	}

	@Test
	void getFullAddresses_BUSINESS_ERROR() {
		assertThatThrownBy(() -> addressService.getFullAddresses("6 ru"))
				.isInstanceOf(TooManyAddressesException.class);
	}
}
