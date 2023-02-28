package org.rudi.tools.nodestub.datafactory.service;

import java.io.IOException;
import java.time.OffsetDateTime;

import org.rudi.common.service.exception.AppServiceBadRequestException;
import org.rudi.tools.nodestub.datafactory.apirecette.bean.BarChartData;
import org.rudi.tools.nodestub.datafactory.apirecette.bean.GenericDataObject;
import org.rudi.tools.nodestub.datafactory.service.config.DataFactoryApiException;

public interface DechetsService {
	GenericDataObject gdataTypeGet(String idRva, String type)
			throws IOException, AppServiceBadRequestException, DataFactoryApiException;

	BarChartData peseesGet(String idRva, OffsetDateTime maxDate, OffsetDateTime minDate)
			throws IOException, AppServiceBadRequestException, DataFactoryApiException;

	boolean validateIdRva(String idRva);
}
