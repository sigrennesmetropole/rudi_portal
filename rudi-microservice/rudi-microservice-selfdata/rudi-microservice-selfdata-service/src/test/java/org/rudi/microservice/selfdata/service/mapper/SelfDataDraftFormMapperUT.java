package org.rudi.microservice.selfdata.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.rudi.bpmn.core.bean.Field;
import org.rudi.bpmn.core.bean.Section;
import org.rudi.facet.bpmn.exception.FormDefinitionException;
import org.rudi.facet.kaccess.bean.DictionaryEntry;
import org.rudi.facet.kaccess.bean.Language;
import org.rudi.facet.kaccess.bean.MatchingData;
import org.rudi.microservice.selfdata.service.SelfdataSpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.RequiredArgsConstructor;

@SelfdataSpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class SelfDataDraftFormMapperUT {

	private final SelfdataDraftFormMapper selfdataDraftFormMapper;

	private final String matchingDataCode = "address";
	private final String matchingDataLabel = "Adresse";
	private final String helpText = "Veuillez saisir votre adresse complète, en indiquant le numéro de rue, le nom de rue et la ville.";

	@Test
	void test_addSelfDataFields() throws FormDefinitionException {

		// On définit la langue de recherche fr-Fr
		Locale currentLocale = Locale.getDefault();
		Language currentLanguage = Language.fromValue(currentLocale.getLanguage());

		// Création des mocks
		List<MatchingData> matchingDatas = createMatchingDatasForTests(currentLanguage);

		Section section = new Section();
		selfdataDraftFormMapper.addSelfDataFields(section, matchingDatas, currentLanguage);

		// Retrouver les données de test
		List<Field> fieldsUpdated = section.getFields();
		Field fieldUpdated = fieldsUpdated.stream()
				.filter(field -> field.getDefinition().getName().equals(matchingDataCode)).collect(Collectors.toList())
				.stream().findFirst().orElse(null);

		assertThat(fieldUpdated).isNotNull();
		assertThat(fieldUpdated.getDefinition().getLabel()).isEqualTo(matchingDataLabel);
		assertThat(fieldUpdated.getDefinition().getHelp()).isEqualTo(helpText);
	}

	@Test
	void test_addSelfDataFields_defautLanguage() throws FormDefinitionException {

		// langue des données mockées fr-Fr
		Locale currentLocale = Locale.getDefault();
		Language currentLanguage = Language.fromValue(currentLocale.getLanguage());

		// langue demandée : anglais
		Language askedLanguage = Language.EN_US;

		// Création des mocks
		List<MatchingData> matchingDatas = createMatchingDatasForTests(currentLanguage);

		Section section = new Section();
		selfdataDraftFormMapper.addSelfDataFields(section, matchingDatas, askedLanguage);

		// On vérifie que la section n'est pas vide même si les langages correspondent pas
		List<Field> fieldsUpdated = section.getFields();
		assertThat(fieldsUpdated.isEmpty()).isFalse();
	}

	private List<MatchingData> createMatchingDatasForTests(Language currentLanguage) {
		List<MatchingData> matchingDatas = new ArrayList<>();
		MatchingData data = new MatchingData();
		data.setCode(matchingDataCode);
		data.setType(MatchingData.TypeEnum.STRING);
		List<DictionaryEntry> labels = new ArrayList<>();
		DictionaryEntry entry1 = new DictionaryEntry();
		entry1.setText(matchingDataLabel);
		entry1.setLang(currentLanguage);
		labels.add(entry1);
		List<DictionaryEntry> helps = new ArrayList<>();
		DictionaryEntry entry2 = new DictionaryEntry();
		entry2.setText(helpText);
		entry2.setLang(currentLanguage);
		helps.add(entry2);
		data.setLabel(labels);
		data.setHelp(helps);
		matchingDatas.add(data);
		return matchingDatas;
	}
}
