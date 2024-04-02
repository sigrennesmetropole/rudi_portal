package org.rudi.microservice.konsult.service.customization;

import org.rudi.microservice.konsult.core.customization.KeyFigureData;
import org.rudi.microservice.konsult.core.customization.KeyFigureTypeData;

public interface KeyFigureComputer {

	default Long getDefaultKeyFigureValue(){
		return 0L;
	}

	default boolean  accept(KeyFigureTypeData keyFigureTypeData){
		return keyFigureTypeData == getAcceptedData();
	}

	KeyFigureTypeData getAcceptedData();

	void compute(KeyFigureData keyFigureData);

}
