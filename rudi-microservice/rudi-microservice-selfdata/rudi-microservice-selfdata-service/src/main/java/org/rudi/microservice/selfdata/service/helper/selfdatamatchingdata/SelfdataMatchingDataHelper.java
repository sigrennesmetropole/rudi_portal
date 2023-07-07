package org.rudi.microservice.selfdata.service.helper.selfdatamatchingdata;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.rudi.bpmn.core.bean.Field;
import org.rudi.bpmn.core.bean.Form;
import org.rudi.bpmn.core.bean.Section;
import org.rudi.facet.bpmn.exception.FormDefinitionException;
import org.rudi.facet.bpmn.exception.InvalidDataException;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.bean.Language;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.SelfdataContent;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.microservice.selfdata.core.bean.MatchingField;
import org.rudi.microservice.selfdata.service.mapper.SelfdataDraftFormMapper;
import org.rudi.microservice.selfdata.storage.dao.selfdatainformationrequest.SelfdataInformationRequestCustomDao;
import org.rudi.microservice.selfdata.storage.dao.selfdatainformationrequest.SelfdataInformationRequestDao;
import org.rudi.microservice.selfdata.storage.entity.selfdatainformationrequest.SelfdataInformationRequestEntity;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SelfdataMatchingDataHelper {

	public static final String PROCESS_DEFINITION_ID = "selfdata-information-request-process";

	private static final String DEFAULT_LANGUAGE = "fr-FR";

	private static final String SELFDATA_SECTION_NAME = "matching-data";

	public static final String SELFDATA_CRYPTED_VALUE_IDENTIFIER = "crypted@";

	private final SelfdataInformationRequestDao selfdataInformationRequestDao;
	private final SelfdataInformationRequestCustomDao selfdataInformationRequestCustomDao;
	private final SelfdataDraftFormMapper selfdataDraftFormMapper;
	private final DatasetService datasetService;
	private final FormHelper formHelper;
	private final MatchingDataCipherOperator matchingDataCipherOperator;

	String getProcessDefinitionKey() {
		return PROCESS_DEFINITION_ID;
	}

	public Form lookupDraftForm() throws FormDefinitionException {
		return formHelper.lookupDraftForm(getProcessDefinitionKey());
	}

	public Form lookupDraftFormWithSelfdata(UUID metadataUuid, Optional<String> languageString)
			throws FormDefinitionException {
		Language language;
		String paramLaguage = languageString.isPresent() ? languageString.get() : DEFAULT_LANGUAGE;
		try {
			language = Language.fromValue(paramLaguage);
		} catch (IllegalArgumentException e) {
			throw new FormDefinitionException(String.format("Langage fourni : %s inconnu ", languageString), e);
		}

		Form form;
		try {
			form = lookupDraftForm();
		} catch (FormDefinitionException e) {
			throw new FormDefinitionException("Le formulaire selfdata est incorrect", e);
		}

		if (form == null) {
			return null;
		}

		Metadata dataset;
		try {
			dataset = datasetService.getDataset(metadataUuid);
		} catch (DataverseAPIException e) {
			throw new FormDefinitionException("Impossible de récupérer le JDD d'uuid : " + metadataUuid, e);
		}

		Optional<Section> sectionSelfdata = getMatchingDataSection(form);

		if (dataset.getExtMetadata() != null && dataset.getExtMetadata().getExtSelfdata() != null
				&& dataset.getExtMetadata().getExtSelfdata().getExtSelfdataContent() != null
				&& sectionSelfdata.isPresent()) {
			SelfdataContent selfdataContent = dataset.getExtMetadata().getExtSelfdata().getExtSelfdataContent();
			selfdataDraftFormMapper.addSelfDataFields(sectionSelfdata.get(), selfdataContent.getMatchingData(),
					language);
		}

		return form;
	}

	public Optional<Section> getMatchingDataSectionByAssetUuid(UUID assetUuid) throws FormDefinitionException {

		// Récupération de l'asset hydraté de la task
		SelfdataInformationRequestEntity asset = selfdataInformationRequestDao.findByUuid(assetUuid);
		if (asset == null) {
			throw new IllegalArgumentException(String.format("Pas d'asset d'uuid ': %s", assetUuid));
		}

		// Récupération du draft form et de la section selfdata
		Form informationRequestDraftForm = lookupDraftFormWithSelfdata(asset.getDatasetUuid(), Optional.empty());
		if (informationRequestDraftForm == null) {
			throw new IllegalArgumentException(
					String.format("Pas de formulaire rempli pour l'asset d'uuid ': %s", assetUuid));
		}

		return getMatchingDataSection(informationRequestDraftForm);
	}

	public Optional<Section> getMatchingDataSectionByDatasetUuid(UUID datasetUuid) throws FormDefinitionException {
		// Récupération du draft form d'un selfdata
		Form informationRequestDraftForm = lookupDraftFormWithSelfdata(datasetUuid, Optional.empty());
		if (informationRequestDraftForm == null) {
			throw new IllegalArgumentException(
					String.format("Pas de formulaire rempli pour la demande de dataset id ': %s", datasetUuid));
		}
		// Extraction de la section MatchingData
		return getMatchingDataSection(informationRequestDraftForm);
	}

	public Map<String, Object> getInformationRequestMapByAssetUuid(UUID assetUuid) throws InvalidDataException {

		SelfdataInformationRequestEntity asset = this.selfdataInformationRequestDao.findByUuid(assetUuid);

		if (asset == null) {
			throw new IllegalArgumentException(String.format("Pas d'asset d'uuid ': %s", assetUuid));
		}
		Map<String, Object> informationRequestMap = this.formHelper.hydrateData(asset.getData());
		// Dechiffrement des matchingData.
		decrypt(informationRequestMap);
		return informationRequestMap;
	}

	public Map<String, Object> getInformationRequestMapByDatasetUuid(String userLogin, UUID datasetUuid)
			throws InvalidDataException {
		SelfdataInformationRequestEntity asset = this.selfdataInformationRequestCustomDao
				.getLastSelfdataInformationRequest(userLogin, datasetUuid);
		if (asset == null) {
			throw new IllegalArgumentException(String
					.format("L'utilisateur %s n'a pas de demande sur le dataset d'uuid ': %s", userLogin, datasetUuid));
		}
		Map<String, Object> informationRequestMap = this.formHelper.hydrateData(asset.getData());
		// Dechiffrement des matchingData.
		decrypt(informationRequestMap);
		return informationRequestMap;
	}

	/**
	 * @param informationRequestForm le formulaire interrogé
	 * @return la section de données pivot dans un formulaire de demande d'information
	 */
	private Optional<Section> getMatchingDataSection(Form informationRequestForm) {
		return informationRequestForm.getSections().stream()
				.filter(section -> section.getName().equalsIgnoreCase(SELFDATA_SECTION_NAME)).findFirst();
	}

	public void recryptSelfdataInformationRequest(SelfdataInformationRequestEntity item, String previousAliasKey)
			throws FormDefinitionException, InvalidDataException {
		// récupération de la section correspondante
		Optional<Section> section = getMatchingDataSectionByAssetUuid(item.getUuid());
		// récupération brutte des données de la demande
		Map<String, Object> datas = formHelper.hydrateData(item.getData());
		if (MapUtils.isNotEmpty(datas)) {
			// parcourt des données de la demande
			boolean update = false;
			for (Map.Entry<String, Object> data : datas.entrySet()) {
				update = recrypteValue(data, previousAliasKey, item.getUuid(), section);
			}
			if (update) {
				item.setData(formHelper.deshydrateData(datas));
			}
		}
	}

	private boolean recrypteValue(Map.Entry<String, Object> data, String previousAliasKey, UUID itemUuid,
			Optional<Section> section) {
		String key = data.getKey();
		Object value = data.getValue();
		log.info("Recrypt request {} -> {}", itemUuid, key);
		if (value != null) {
			if (value.toString().startsWith(SELFDATA_CRYPTED_VALUE_IDENTIFIER)) {
				// la données est chiffrée
				if (previousAliasKey != null) {
					// on a reçu un alias
					try {
						recryptWithPreviousAlias(value, previousAliasKey, itemUuid, key);
						return true;
					} catch (Exception e) {
						log.error("Failed to recrypt {} -> {} - {}", itemUuid, key, e.getMessage());
					}
				}
			} else if (section.isPresent()) {
				// la données n'est pas chiffrée
				Field field = formHelper.lookupField(section.get(), key);
				if (field != null) {
					log.info("Recrypt request {} -> {} migrate", itemUuid, key);
					// mais le champ est dans la section donc on chiffre
					data.setValue(encrypt(value.toString()));
					return true;
				}
			}
		} else {
			log.info("Recrypt request {} -> {} skip null value", itemUuid, key);
		}
		return false;
	}

	public String recryptWithPreviousAlias(Object value, String previousAliasKey, UUID itemUuid, String key)
			throws GeneralSecurityException, IOException {
		log.info("Recrypt request {} -> {} recrypt", itemUuid, key);
		// déchiffrement avec l'ancien alias
		String previousValue = matchingDataCipherOperator
				.decrypt(value.toString().substring(SELFDATA_CRYPTED_VALUE_IDENTIFIER.length()), previousAliasKey);
		// chiffrement avec le nouveau et stockage
		return SELFDATA_CRYPTED_VALUE_IDENTIFIER + matchingDataCipherOperator.encrypt(previousValue);
	}

	private String encrypt(String value) {
		try {
			return SELFDATA_CRYPTED_VALUE_IDENTIFIER + matchingDataCipherOperator.encrypt(value);
		} catch (Exception e) {
			log.error(String.format("Impossible de chiffrer la value %s - %s", value, e.getMessage()), e);
			return value;
		}
	}

	public void encrypt(Form form) {
		if (form != null) {
			Optional<Section> matchingDataSection = getMatchingDataSection(form);
			if (matchingDataSection.isPresent()) {
				encryptField(matchingDataSection.get().getFields());
			}
		}
	}

	private void encryptField(Collection<Field> fields) {
		if (CollectionUtils.isNotEmpty(fields)) {
			fields.stream().forEach(field -> {
				List<String> values = new ArrayList<>();
				if (field != null && field.getValues() != null) {
					field.getValues().stream().forEach(value -> {
						if (!value.isEmpty()) { // On encrypte pas une donnée sans contenu (string vide)
							values.add(encrypt(value));
						}
					});
				}
				field.setValues(values);
			});
		}
	}

	public String decrypt(String value) {
		String decryptableValue;
		if (isCrypted(value)) {
			decryptableValue = value.substring(SELFDATA_CRYPTED_VALUE_IDENTIFIER.length());
			try {
				return matchingDataCipherOperator.decrypt(decryptableValue);
			} catch (Exception e) {
				log.error(String.format("Impossible de déchiffrer la value %s - %s", value, e.getMessage()), e);
				return value;
			}
		}
		log.warn("Valeure non cryptée.");
		return value;
	}

	public void decrypt(Form form) {
		Optional<Section> matchingDataSection = getMatchingDataSection(form);
		if (matchingDataSection.isPresent()) {
			Collection<Field> fields = matchingDataSection.get().getFields();
			if (CollectionUtils.isNotEmpty(fields)) {
				fields.stream().forEach(field -> {
					List<String> values = new ArrayList<>();
					field.getValues().stream().forEach(value -> values.add(decrypt(value)));
					field.setValues(values);
				});
			}

		}
	}

	public void decrypt(Map<String, Object> encryptedValues) {
		if (MapUtils.isNotEmpty(encryptedValues)) {
			encryptedValues.forEach((key, value) -> encryptedValues.replace(key, decrypt(value.toString())));
		}
	}

	public void decrypt(List<MatchingField> matchingDataFields) {
		if (CollectionUtils.isNotEmpty(matchingDataFields)) {
			matchingDataFields.forEach(field -> {
				if (isCrypted(field.getValue())) {
					field.setValue(decrypt(field.getValue()));
				}
			});
		}
	}

	private boolean isCrypted(String value) {
		if (value == null) {
			log.error("Null value given");
			return false;
		}
		if (!value.startsWith(SELFDATA_CRYPTED_VALUE_IDENTIFIER)) {
			log.error("Value not crypted : {}", value);
			return false;
		}
		return true;
	}

}
