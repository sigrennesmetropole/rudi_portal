package org.rudi.microservice.selfdata.service.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceForbiddenException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.facet.doks.helper.DocumentContentHelper;
import org.rudi.facet.kaccess.bean.MatchingData;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.providers.bean.NodeProvider;
import org.rudi.facet.providers.helper.ProviderHelper;
import org.rudi.microservice.selfdata.core.bean.MatchingField;
import org.rudi.microservice.selfdata.core.bean.NodeProviderInfo;
import org.rudi.microservice.selfdata.service.exception.MissingProviderException;
import org.rudi.microservice.selfdata.service.helper.selfdatamatchingdata.SelfdataMatchingDataHelper;
import org.rudi.microservice.selfdata.storage.dao.selfdatatokentuple.SelfdataTokenTupleDao;
import org.rudi.microservice.selfdata.storage.entity.selfdatainformationrequest.SelfdataTokenTupleEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Component
@RequiredArgsConstructor
public class SelfdataPairingUtils {
	private final SelfdataTokenTupleDao selfdataTokenDao;
	private final ProviderHelper providerHelper;
	private final SelfdataMatchingDataHelper selfdataMatchingDataHelper;
	private final DocumentContentHelper documentContentHelper;
	private final EverythingAllowedAuthorizationPolicy authorizationPolicy;
	private static final Logger LOGGER = LoggerFactory.getLogger(SelfdataPairingUtils.class);

	public SelfdataTokenTupleEntity savePairingTokenForUser(UUID token, UUID userUuid, UUID datasetUuid,
			UUID nodeProviderUuid) {
		SelfdataTokenTupleEntity tokenTupleEntity = new SelfdataTokenTupleEntity();
		tokenTupleEntity.setUuid(UUID.randomUUID());
		tokenTupleEntity.setToken(token);
		tokenTupleEntity.setUserUuid(userUuid);
		tokenTupleEntity.setDatasetUuid(datasetUuid);
		tokenTupleEntity.setNodeProviderId(nodeProviderUuid);
		return selfdataTokenDao.save(tokenTupleEntity);
	}

	public NodeProviderInfo getNodeProviderInfo(Metadata dataset) throws MissingProviderException {
		UUID providerUuid = dataset.getMetadataInfo().getMetadataProvider().getOrganizationId();
		NodeProvider nodeProvider = providerHelper.getNodeProviderByUUID(providerUuid);
		if (nodeProvider == null) { // Le provider dans le dataset ne correspond à aucun provider connu du système
			throw new MissingProviderException("Aucun provider connu pour ce dataset");
		}
		NodeProviderInfo providerInfo = new NodeProviderInfo();
		providerInfo.setUuid(nodeProvider.getUuid());
		providerInfo.setUrl(nodeProvider.getUrl());
		return providerInfo;
	}

	/**
	 * @param dataset le JDD selfdata
	 * @return La liste des données pivots requises par le JDD
	 */
	public List<MatchingData> getMatchingDataList(Metadata dataset) {
		if (dataset.getExtMetadata() != null && dataset.getExtMetadata().getExtSelfdata() != null
				&& dataset.getExtMetadata().getExtSelfdata().getExtSelfdataContent() != null) { // On est bien avec un selfdata
			return dataset.getExtMetadata().getExtSelfdata().getExtSelfdataContent().getMatchingData();
		}
		return Collections.emptyList();
	}

	/**
	 * Cette méthode extrait la partie de la map correspondant aux données pivots d'un JDD et les cast en des MatchingField
	 *
	 * @param informationRequestMap Map de toutes les données qui ont été saisies à des moments donnés dans des formulaires
	 * @param matchingDataList      Liste des données pivots requises par le JDD
	 * @return Map des données pivots uniquement
	 */
	public List<MatchingField> extractMatchingDataFromFormData(Map<String, Object> informationRequestMap,
			List<MatchingData> matchingDataList) throws AppServiceException {
		List<MatchingField> matchingDataFields = new ArrayList<>();
		for (MatchingData matchingData : matchingDataList) { // Extraire l'entrée de chaque matchingData dans la map principale
			MatchingField field = createFieldForMatchingData(informationRequestMap, matchingData.getCode(),
					matchingData.getRequired());
			if (field != null) {
				matchingDataFields.add(field);
			}
		}
		return matchingDataFields;
	}

	/**
	 * @param informationRequestMap map des données de formulaire
	 * @param matchingDataCode      clé de la donnée pivot
	 * @param isRequired            indique si la donnée pivot est réquise ou pas dans sa description dans le JDD
	 * @return le MatchingField correspondant au matchingDataCode qui lui est passé
	 */
	private MatchingField createFieldForMatchingData(Map<String, Object> informationRequestMap, String matchingDataCode,
			Boolean isRequired) throws AppServiceException {
		LOGGER.info("Donnée pivot en cours de traitement: {}", matchingDataCode);
		val matchingDataValue = informationRequestMap.get(matchingDataCode);
		if (Boolean.TRUE.equals(isRequired) && matchingDataValue == null) { // Donnée pivot requise non renseignée => incohérence
			throw new AppServiceException("Une donnée pivot obligatoire n'a pas été retrouvée");
		}
		if (matchingDataValue == null) {
			return null; // Correspond à une donnée pivot facultative non renseignée
		}
		MatchingField field = new MatchingField();
		field.setCode(matchingDataCode);
		field.setValue(matchingDataValue.toString());
		return field;
	}

	/**
	 * @param matchingDataList      liste des données pivots du JDD
	 * @param informationRequestMap la map contenant toutes les infos qui ont été saisies dans des formulaires selfdata
	 */
	public void removeAttachmentFromMatchingData(List<MatchingData> matchingDataList,
			Map<String, Object> informationRequestMap)
			throws AppServiceNotFoundException, AppServiceForbiddenException, AppServiceUnauthorizedException {
		for (MatchingData matchingData : matchingDataList) {
			if (matchingData.getType() == MatchingData.TypeEnum.ATTACHMENT) { // Supprimer la PJ pour donnée pivot de type ATTACHMENT
				deleteAttachment(matchingData, informationRequestMap);
			}
		}
	}

	/**
	 * @param matchingData          donnée pivot de type attachment à supprimer
	 * @param informationRequestMap map de toutes les données saisies dans des formulaires selfdata
	 * @throws AppServiceNotFoundException
	 * @throws AppServiceForbiddenException
	 * @throws AppServiceUnauthorizedException
	 */
	private void deleteAttachment(MatchingData matchingData, Map<String, Object> informationRequestMap)
			throws AppServiceNotFoundException, AppServiceForbiddenException, AppServiceUnauthorizedException {
		final Object matchingDataValue = informationRequestMap.get(matchingData.getCode());
		// Si le matchingData est optionnel, sa valeur peut ne pas avoir été renseignée => Pas de tentative de suppression
		if (matchingDataValue == null) {
			return;
		}
		String matchingDateUuid = matchingDataValue.toString();
		if (matchingDateUuid.startsWith(SelfdataMatchingDataHelper.SELFDATA_CRYPTED_VALUE_IDENTIFIER)) {
			matchingDateUuid = selfdataMatchingDataHelper.decrypt(matchingDateUuid);
		}
		UUID attachmentUuid = UUID.fromString(matchingDateUuid);
		// On ne veut pas bloquer le traitement de la tâche parce qu'on arrive pas à supprimer une PJ (les PJ étant gardées sous forme de métadata dans RUDI)
		try {
			documentContentHelper.deleteAttachment(attachmentUuid, authorizationPolicy);
		} catch (AppServiceNotFoundException | AppServiceForbiddenException
				| AppServiceUnauthorizedException exception) {
			LOGGER.error("Une erreur est survenue lors de la suppression d'une pièce jointe.", exception);
		}
	}
}
