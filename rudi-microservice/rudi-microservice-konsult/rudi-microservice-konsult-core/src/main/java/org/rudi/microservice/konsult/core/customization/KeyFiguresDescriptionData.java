package org.rudi.microservice.konsult.core.customization;

import java.util.List;

import lombok.Data;

@Data
public class KeyFiguresDescriptionData {
	private List<KeyFigureData> keyFigures;

	private String keyFiguresLogo;
}
