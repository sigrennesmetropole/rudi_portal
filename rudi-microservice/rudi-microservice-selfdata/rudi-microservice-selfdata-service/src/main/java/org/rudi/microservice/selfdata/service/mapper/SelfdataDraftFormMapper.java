package org.rudi.microservice.selfdata.service.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.NotImplementedException;
import org.rudi.bpmn.core.bean.Field;
import org.rudi.bpmn.core.bean.FieldDefinition;
import org.rudi.bpmn.core.bean.FieldType;
import org.rudi.bpmn.core.bean.Section;
import org.rudi.bpmn.core.bean.Validator;
import org.rudi.facet.bpmn.exception.FormDefinitionException;
import org.rudi.facet.kaccess.bean.Language;
import org.rudi.facet.kaccess.bean.MatchingData;
import org.rudi.facet.kaccess.helper.selfdata.DictionaryEntryHelper;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SelfdataDraftFormMapper {

	private final DictionaryEntryHelper dictionaryEntryHelper;

	/**
	 * Ajout des champs selfdata dans un formulaire de workflow DRAFT à partir des données pivots
	 *
	 * @param section       section de formulaire à enrichir
	 * @param matchingDatas données pivots à mapper
	 * @param language      la langue dans laquelle on souhaite récupérer les libellés des données pivots
	 * @throws org.rudi.facet.bpmn.exception.FormDefinitionException
	 */
	public void addSelfDataFields(Section section, List<MatchingData> matchingDatas,
			Language language) throws FormDefinitionException {
		if (section == null || matchingDatas == null || matchingDatas.isEmpty()) {
			return;
		}

		List<Field> selfdataFields = new ArrayList<>();
		for (MatchingData matchingData : matchingDatas) {
			selfdataFields.add(matchingDataToField(matchingData, language));
		}

		section.setFields(selfdataFields);
	}

	/**
	 * Mapping Donnée pivot vers champ de formulaire
	 *
	 * @param matchingData donnée pivot à mapper
	 * @param language     la langue pour les libellés du formulaire
	 * @return un champ de formulaire
	 * @throws FormDefinitionException
	 */
	private Field matchingDataToField(MatchingData matchingData, Language language) throws FormDefinitionException {
		Field field = new Field();
		FieldDefinition definition = new FieldDefinition();

		FieldType type;
		try {
			type = FieldType.valueOf(matchingData.getType().toString());
		} catch (IllegalArgumentException e) {
			throw new FormDefinitionException(
					String.format("Aucune correspondance de type pour la donnée pivot %s de type %s",
							matchingData.getCode(), matchingData.getType().toString()), e);
		}

		definition.setName(matchingData.getCode());
		definition.setType(type);
		definition.setLabel(dictionaryEntryHelper.filterByLanguage(matchingData.getLabel(), language).getText());
		definition.setHelp(dictionaryEntryHelper.filterByLanguage(matchingData.getHelp(), language).getText());
		definition.setRequired(matchingData.getRequired());
		definition.setValidators(map(matchingData.getValidators()));
		field.setDefinition(definition);

		return field;
	}

	@Nullable
	private List<Validator> map(@Nullable List<org.rudi.facet.kaccess.bean.Validator> selfdataValidators) {
		if (selfdataValidators == null) {
			return null;
		}
		return selfdataValidators.stream().map(this::map).collect(Collectors.toList());
	}

	private Validator map(@Nonnull org.rudi.facet.kaccess.bean.Validator selfdataValidator) {
		// TODO RUDI-2755
		final var message = String.format("Self-data Validator of type \"%s\" has no matching workflow validator", selfdataValidator.getType());
		throw new NotImplementedException(message);
	}
}
