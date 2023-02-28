package org.rudi.facet.kaccess.helper.selfdata;

import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.rudi.facet.kaccess.bean.Media;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.dataset.bean.InterfaceContract;
import org.springframework.stereotype.Component;

@Component
public class SelfdataMediaHelper {

	private static final InterfaceContract INTERFACE_CONTRACT_TPBC = InterfaceContract.TEMPORAL_BAR_CHART;
	private static final InterfaceContract INTERFACE_CONTRACT_GDATA = InterfaceContract.GENERIC_DATA;

	/**
	 * Les interface contract obligatoires quand on crée un JDD selfdata à traitement automatique dans RUDI
	 */
	private static final InterfaceContract[] MANDATORY_INTERFACE_CONTRACTS_FOR_AUTOMATIC_SELFDATA = {
			SelfdataMediaHelper.INTERFACE_CONTRACT_TPBC,
			SelfdataMediaHelper.INTERFACE_CONTRACT_GDATA
	};

	/**
	 * Vérifie si le JDD fourni possède les pré-requis pour faire un traitement automatique du selfdata dans RUDI
	 *
	 * @param metadata le JDD testé
	 * @return si conforme ou non
	 */
	public boolean hasMandatoryMediasForAutomaticTreatment(Metadata metadata) {
		if (metadata == null) {
			throw new IllegalStateException("Impossible de tester un JDD si aucun JDD fourni");
		}

		List<Media> medias = metadata.getAvailableFormats();
		if (CollectionUtils.isEmpty(medias)) {
			return false;
		}

		for (InterfaceContract mandatoryInterfaceContract : MANDATORY_INTERFACE_CONTRACTS_FOR_AUTOMATIC_SELFDATA) {
			long numberOfMedias = searchMediaByInterfaceContractName(mandatoryInterfaceContract, medias).count();
			if (numberOfMedias != 1) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Récupère le média GDATA pour restituer les données personnelles en mode JSON
	 *
	 * @param medias l'ensemble des médias d'un JDD
	 * @return le média GDATA du JDD
	 */
	public Media getGdataMedia(List<Media> medias) {
		return searchMediaByInterfaceContractName(INTERFACE_CONTRACT_GDATA, medias).findFirst().orElse(null);
	}

	/**
	 * Récupère le média TPBC pour restituer les données personnelles en mode bar chart
	 *
	 * @param medias l'ensemble des médias d'un JDD
	 * @return le média TPBC du JDD
	 */
	public Media getTpbcMedia(List<Media> medias) {
		return searchMediaByInterfaceContractName(INTERFACE_CONTRACT_TPBC, medias).findFirst().orElse(null);
	}

	/**
	 * Récupération d'un stream qui cherche un média par interface
	 *
	 * @param contract   l'interface cherchée
	 * @param medias 	 la liste des médias
	 * @return le stream pouvant contenir le média concerné
	 */
	private Stream<Media> searchMediaByInterfaceContractName(InterfaceContract contract, List<Media> medias) {
		return medias.stream().filter(media ->
				media.getMediaType().equals(Media.MediaTypeEnum.SERVICE) &&
						media.getConnector().getInterfaceContract().equals(contract.getCode())
		);
	}
}
