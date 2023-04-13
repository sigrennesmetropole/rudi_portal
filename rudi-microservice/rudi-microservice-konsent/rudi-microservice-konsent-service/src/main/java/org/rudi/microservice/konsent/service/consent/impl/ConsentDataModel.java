package org.rudi.microservice.konsent.service.consent.impl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import freemarker.template.SimpleSequence;
import freemarker.template.TemplateModelException;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.generator.docx.model.AbstractDocxDataModel;
import org.rudi.facet.generator.exception.GenerationException;
import org.rudi.facet.generator.model.GenerationFormat;
import org.rudi.facet.organization.bean.Organization;
import org.rudi.microservice.konsent.storage.entity.consent.ConsentEntity;
import org.rudi.microservice.konsent.storage.entity.data.DictionaryEntryEntity;
import org.rudi.microservice.konsent.storage.entity.treatment.TreatmentEntity;
import org.rudi.microservice.konsent.storage.entity.treatmentversion.TreatmentVersionEntity;

import lombok.Getter;


@Getter
public class ConsentDataModel extends AbstractDocxDataModel {
	private final ConsentEntity consentEntity;
	private final Locale locale;
	private static final Locale defaultLocale = Locale.FRENCH;
	private final User consentor;
	private final TreatmentEntity treatmentEntity;
	private final TreatmentVersionEntity versionEntity;
	private final User treamentUser;
	private final Organization treatmentOrganization;

	public ConsentDataModel(ConsentEntity consentEntity, Locale locale, User consentor, TreatmentEntity treatmentEntity, TreatmentVersionEntity versionEntity, User treamentUser, Organization treatmentOrganization) {
		super(GenerationFormat.DOCX, "template/consent.docx");
		this.consentEntity = consentEntity;
		this.locale = locale;
		this.consentor = consentor;
		this.treatmentEntity = treatmentEntity;
		this.versionEntity = versionEntity;
		this.treamentUser = treamentUser;
		this.treatmentOrganization = treatmentOrganization;
	}

	@Override
	public Map<String, Object> getDataModel() throws GenerationException {
		Map<String, Object> data = new HashMap<>();
		// Inject les variables à afficher dans le template
		data.put("consent", consentEntity);
		data.put("consentor", consentor);
		data.put("treatmentUser", treamentUser);
		data.put("treatmentOrganization", treatmentOrganization);
		data.put("dataUtils", this);
		return data;
	}

	@Override
	protected String generateFileName() {
		return getFormat().generateFileName("consentProof");
	}

	public String translateDictionaryEntry(SimpleSequence labels) throws TemplateModelException {

		String resultText = null;
		String localeText = "Texte de test";
		boolean localeTextFound = false;
		for (Object object : labels.toList()) {
			DictionaryEntryEntity dictionaryEntry = (DictionaryEntryEntity) object;
			if (dictionaryEntry.getLang().equals(locale.getLanguage())) {
				resultText = dictionaryEntry.getText();
				break;
			}
			if (!localeTextFound && dictionaryEntry.getLang().equals(defaultLocale.getLanguage())) { // Permet d'eviter une seconde boucle si la lang voulue n'est pas trouvée
				localeText = dictionaryEntry.getText();
				localeTextFound = true;
			}
		}
		if (resultText == null) {
			resultText = localeText;
		}
		return resultText;
	}

	public String uuidToString(UUID uuid) {
		return uuid.toString();
	}
}
