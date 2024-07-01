package org.rudi.microservice.konsult.core.customization;

import java.util.List;

import lombok.Data;

/**
 * Classe gérant les orders pour tous les assets
 */
@Data
public class AssetsPageOrderData {
	private List<MultilingualText> libelles;
	private String value;
}
