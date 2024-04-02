package org.rudi.microservice.konsult.core.customization;

import java.util.List;

import lombok.Data;

@Data
public class KeyFigureData {
	private Long count;
	private List<MultilingualText> labels;
	private KeyFigureTypeData type;

}
