package org.rudi.microservice.konsult.service.map;

import java.util.List;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.konsult.core.bean.LayerInformation;
import org.rudi.rva.core.bean.Address;

public interface MapService {

	/**
	 * Récupération des fonds de plan disponibles à l'affichage pour le détail d'un JDD
	 *
	 * @return une liste de couches affichables
	 */
	List<LayerInformation> getDatasetBaseLayers();

	/**
	 * Récupération des fonds de plan disponibles à l'affichage pour la mini carte de localisations des données d'un JDD
	 *
	 * @return une liste de couches affichables
	 */
	List<LayerInformation> getLocalisationBaseLayers();

	/**
	 * Recherche d'adresse RVA avec geocoding pour récupérer des coordonénes de centrage
	 *
	 * @param input la recherche utilisateur
	 * @return une liste d'adresse
	 * @throws Exception si quelque chose de mal arrive
	 */
	List<Address> searchAddresses(String input) throws AppServiceException;
}
