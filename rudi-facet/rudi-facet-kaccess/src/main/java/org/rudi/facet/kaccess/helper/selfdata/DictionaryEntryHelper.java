package org.rudi.facet.kaccess.helper.selfdata;

import java.util.List;
import java.util.Optional;

import org.rudi.facet.kaccess.bean.DictionaryEntry;
import org.rudi.facet.kaccess.bean.Language;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DictionaryEntryHelper {

	/**
	 * Filtre un ensemble d'entrées pour récupérer une seule du language demandé
	 *
	 * @param entries  ensemble d'entrées, ne doit pas être nul ou vide
	 * @param language le language pour récupérer l'entrée qu'on cherche
	 * @return une entrée dont le label correspond au language demandé
	 */
	public DictionaryEntry filterByLanguage(List<DictionaryEntry> entries, Language language) {

		if (CollectionUtils.isEmpty(entries)) {
			throw new IllegalStateException("Aucune entrée DictionaryEntry dans la liste pour trouver" +
					"une entrée par recherche par language");
		}

		Optional<DictionaryEntry> searched = entries.stream()
				.filter(dictionaryEntry -> dictionaryEntry.getLang().equals(language))
				.findFirst();

		if (searched.isPresent()) {
			return searched.get();
		} else {
			log.warn(String.format("Aucun DictionaryEntry trouvé pour la langue %s", language.toString()));
			return entries.get(0);
		}
	}
}
