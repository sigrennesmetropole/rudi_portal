package org.rudi.microservice.kalim.service.integration.impl.validator;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.constant.RudiMetadataField;
import org.rudi.facet.kos.helper.KosHelper;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
class ThemeValidator extends AbstractMetadataValidator<String> {

	private final KosHelper kosHelper;

	@Override
	public Set<IntegrationRequestErrorEntity> validate(String theme) {
		Set<IntegrationRequestErrorEntity> errors = new HashSet<>();
		CollectionUtils.addIgnoreNull(errors, validateThemeSkosConceptCode(theme));
		return errors;
	}

	@Override
	protected String getMetadataElementToValidate(Metadata metadata) {
		return metadata.getTheme();
	}

	private IntegrationRequestErrorEntity validateThemeSkosConceptCode(String theme) {
		if (!kosHelper.skosConceptThemeExists(theme)) {
			return new Error303Builder()
					.field(RudiMetadataField.THEME)
					.fieldValue(theme)
					.expectedString("un code de concept SKOS connu")
					.build();
		}
		return null;
	}
}
