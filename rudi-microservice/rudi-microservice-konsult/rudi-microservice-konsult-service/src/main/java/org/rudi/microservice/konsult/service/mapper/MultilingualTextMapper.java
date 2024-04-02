package org.rudi.microservice.konsult.service.mapper;

import java.util.List;
import java.util.Locale;

import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.rudi.microservice.konsult.core.customization.MultilingualText;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MultilingualTextMapper {

	default String mapLabel(List<MultilingualText> value, @Context Locale locale) {
		if (CollectionUtils.isNotEmpty(value)) {
			// Récupère le texte correspondent à la locale passée en paramètre,
			// Sinon récupère le texte en français,
			// Sinon récupère le premier texte
			MultilingualText label = value.stream().filter(f -> f.getLocale().equals(locale)).findFirst().orElse(
					value.stream().filter(f -> f.getLocale().equals(Locale.FRANCE)).findFirst().orElse(value.get(0)));
			return label.getText();
		}
		return null;
	}

}
