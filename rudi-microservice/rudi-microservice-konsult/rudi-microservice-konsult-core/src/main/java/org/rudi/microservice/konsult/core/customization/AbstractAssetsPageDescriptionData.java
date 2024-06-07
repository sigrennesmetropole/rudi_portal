package org.rudi.microservice.konsult.core.customization;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Contient les champs minimaux nécessaire à l'affichage de l'ensemble des assets d'un type souhaité.
 */
@Getter
@Setter
@ToString
public class AbstractAssetsPageDescriptionData {
	private List<MultilingualText> titles1;

	private List<MultilingualText> titles2;

	private List<AssetsPageOrderData> orders;
}
