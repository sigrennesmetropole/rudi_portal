package org.rudi.tools.nodestub.datafactory.service.impl;

import java.io.IOException;
import java.time.OffsetDateTime;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.common.service.exception.AppServiceBadRequestException;
import org.rudi.tools.nodestub.config.NodeStubConfiguration;
import org.rudi.tools.nodestub.datafactory.apirecette.bean.BarChartData;
import org.rudi.tools.nodestub.datafactory.apirecette.bean.GenericDataObject;
import org.rudi.tools.nodestub.datafactory.service.DechetsService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@ConditionalOnProperty(value = "datafactory.api-recette.mocked", havingValue = "true")
@RequiredArgsConstructor
class MockedDechetsServiceImpl implements DechetsService {

	private final NodeStubConfiguration nodeStubConfiguration;
	private final JsonResourceReader jsonResourceReader;

	@Override
	public GenericDataObject gdataTypeGet(String idRva, String type) throws IOException, AppServiceBadRequestException {
		checkIdRva(idRva);
		return readMockedResponse("gdataTypeGet-" + type, GenericDataObject.class);
	}

	@Override
	public BarChartData peseesGet(String idRva, OffsetDateTime maxDate, OffsetDateTime minDate)
			throws IOException, AppServiceBadRequestException {
		checkIdRva(idRva);
		return readMockedResponse("peseesGet", BarChartData.class);
	}

	private void checkIdRva(String idRva) throws AppServiceBadRequestException {
		if (!NumberUtils.isParsable(idRva)) {
			throw new AppServiceBadRequestException("id-rva is not a number : " + idRva);
		}
	}

	private <T> T readMockedResponse(String baseName, Class<T> valueType)
			throws IOException, AppServiceBadRequestException {
		if (StringUtils.contains(baseName, "/") || StringUtils.contains(baseName, "\\")) {
			throw new AppServiceBadRequestException("Unexpected baseName " + baseName);
		}
		final var path = nodeStubConfiguration.getWasteApiMockedResponseDirectory().resolve(baseName + ".json");
		return jsonResourceReader.read(path.toString(), valueType);
	}

	@Override
	public boolean validateIdRva(String idRva) {
		return true;
	}
}
