package org.rudi.tools.nodestub.datafactory.service.impl;

import java.time.OffsetDateTime;

import org.rudi.tools.nodestub.datafactory.apirecette.bean.BarChartData;
import org.rudi.tools.nodestub.datafactory.apirecette.bean.GenericDataObject;
import org.rudi.tools.nodestub.datafactory.service.DechetsService;
import org.rudi.tools.nodestub.datafactory.service.config.DataFactoryApiException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;

@Service
@ConditionalOnProperty(value = "datafactory.api-recette.mocked", havingValue = "false", matchIfMissing = true)
@Slf4j
class DataFactoryDechetsServiceImpl implements DechetsService {

	private final WebClient dataFactoryApiRecetteWebClient;

	public DataFactoryDechetsServiceImpl(@Qualifier("datafactory_webclient") WebClient dataFactoryApiRecetteWebClient) {
		super();
		this.dataFactoryApiRecetteWebClient = dataFactoryApiRecetteWebClient;
	}

	@Override
	public GenericDataObject gdataTypeGet(String idRva, String type) throws DataFactoryApiException {
		final var mono = dataFactoryApiRecetteWebClient.get()
				.uri(uriBuilder -> uriBuilder.path("/v1/gdata/{type}").queryParam("id-rva", idRva).build(type))
				.attributes(ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId("datafactory"))
				.retrieve().bodyToMono(GenericDataObject.class);
		return MonoUtils.blockOrThrow(mono, DataFactoryApiException.class);
	}

	@Override
	public BarChartData peseesGet(String idRva, OffsetDateTime maxDate, OffsetDateTime minDate)
			throws DataFactoryApiException {
		MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
		if (minDate != null) {
			queryParams.add("min-date", minDate.toString());
		}
		if (maxDate != null) {
			queryParams.add("max-date", maxDate.toString());
		}
		final var mono = dataFactoryApiRecetteWebClient.get().uri(uriBuilder -> uriBuilder.path("/v1/pesees")
				.queryParam("id-rva", idRva).queryParams(queryParams).build()).retrieve()
				.bodyToMono(BarChartData.class);
		return MonoUtils.blockOrThrow(mono, DataFactoryApiException.class);
	}

	@Override
	public boolean validateIdRva(String idRva) {
		try {
			var a = gdataTypeGet(idRva, "frequence");
			log.info("validate idRva {} = {}", idRva, a);
			return a != null;
		} catch (IllegalArgumentException e1) {
			log.warn("invalide idRva " + idRva, e1);
		} catch (Exception e) {
			log.info("invalide idRva {}", idRva);
		}
		return false;
	}
}
