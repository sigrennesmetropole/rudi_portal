package org.rudi.microservice.selfdata.service.helper.selfdatamatchingdata;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
import org.rudi.microservice.selfdata.service.mapper.SelfdataDraftFormMapper;
import org.rudi.microservice.selfdata.storage.dao.selfdatainformationrequest.SelfdataInformationRequestCustomDao;
import org.rudi.microservice.selfdata.storage.dao.selfdatainformationrequest.SelfdataInformationRequestDao;
import org.rudi.microservice.selfdata.storage.entity.selfdatainformationrequest.SelfdataInformationRequestEntity;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SelfDataMatchingDataHelper {

	public static final String PROCESS_DEFINITION_ID = "selfdata-information-request-process";
	private static final String DEFAULT_LANGUAGE = "fr-FR";
	private static final String SELFDATA_SECTION_NAME = "matching-data";
	private final SelfdataInformationRequestDao selfdataInformationRequestDao;
	private final SelfdataInformationRequestCustomDao selfdataInformationRequestCustomDao;
	private final SelfdataDraftFormMapper selfdataDraftFormMapper;
	private final DatasetService datasetService;
	private final FormHelper formHelper;

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
			form = this.lookupDraftForm();
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
		SelfdataInformationRequestEntity asset = this.selfdataInformationRequestDao.findByUuid(assetUuid);
		if (asset == null) {
			throw new IllegalArgumentException(String.format("Pas d'asset d'uuid ': %s", assetUuid));
		}

		// Récupération du draft form et de la section selfdata
		Form informationRequestDraftForm = this.lookupDraftFormWithSelfdata(asset.getDatasetUuid(), Optional.empty());
		if (informationRequestDraftForm == null) {
			throw new IllegalArgumentException(
					String.format("Pas de formulaire rempli pour l'asset d'uuid ': %s", assetUuid));
		}

		return this.getMatchingDataSection(informationRequestDraftForm);
	}

	public Optional<Section> getMatchingDataSectionByDatasetUuid(UUID datasetUuid) throws FormDefinitionException {
		// Récupération du draft form d'un selfdata
		Form informationRequestDraftForm = this.lookupDraftFormWithSelfdata(datasetUuid, Optional.empty());
		if (informationRequestDraftForm == null) {
			throw new IllegalArgumentException(
					String.format("Pas de formulaire rempli pour la demande de dataset id ': %s", datasetUuid));
		}
		// Extraction de la section MatchingData
		return this.getMatchingDataSection(informationRequestDraftForm);
	}

	public Map<String, Object> getInformationRequestMapByAssetUuid(UUID assetUuid) throws InvalidDataException {

		SelfdataInformationRequestEntity asset = this.selfdataInformationRequestDao.findByUuid(assetUuid);

		if (asset == null) {
			throw new IllegalArgumentException(String.format("Pas d'asset d'uuid ': %s", assetUuid));
		}
		return this.formHelper.hydrateData(asset.getData());
	}

	public Map<String, Object> getInformationRequestMapByDatasetUuid(String userLogin, UUID datasetUuid) throws InvalidDataException {
		SelfdataInformationRequestEntity asset = this.selfdataInformationRequestCustomDao.getLastSelfdataInformationRequest(userLogin, datasetUuid);
		if (asset == null) {
			throw new IllegalArgumentException(String.format("L'utilisateur %s n'a pas de demande sur le dataset d'uuid ': %s", userLogin, datasetUuid));
		}
		return this.formHelper.hydrateData(asset.getData());
	}

	/**
	 * @param informationRequestForm le formulaire interrogé
	 * @return la section de données pivot dans un formulaire de demande d'information
	 */
	private Optional<Section> getMatchingDataSection(Form informationRequestForm) {
		return informationRequestForm.getSections().stream()
				.filter(section -> section.getName().equalsIgnoreCase(SELFDATA_SECTION_NAME)).findFirst();
	}

}
