package org.rudi.wso2.mediation;

import javax.annotation.Nullable;

import org.wso2.carbon.apimgt.api.model.API;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class AdditionalPropertiesUtil {

	/**
	 * En interne, WSO2 ajoute ce suffixe au nom des propriétés dont on a coché la case "Show in devportal"
	 */
	public static final String VISIBLE_IN_DEVPORTAL_SUFFIX = "__display";

	@Nullable
	public static String getAdditionalProperty(String name, API engagedApi) {
		final String additionalPropertyNotVisibleInDevPortal = getAdditionalProperty(name, engagedApi, false);
		if (additionalPropertyNotVisibleInDevPortal != null) {
			return additionalPropertyNotVisibleInDevPortal;
		}

		return getAdditionalProperty(name, engagedApi, true);
	}

	@Nullable
	public static String getAdditionalProperty(String name, API engagedApi, boolean visibleInDevPortal) {
		final var additionalProperties = engagedApi.getAdditionalProperties();
		final var internalName = visibleInDevPortal ? name + VISIBLE_IN_DEVPORTAL_SUFFIX : name;
		return (String) additionalProperties.get(internalName);
	}

	/**
	 * @return <code>true</code> si la propriété vaut exactement "true" (en minuscules), <code>false</code> sinon.
	 */
	public static boolean additionalPropertyIsTrue(String name, API engagedApi) {
		final var additionalProperty = getAdditionalProperty(name, engagedApi);
		return Boolean.parseBoolean(additionalProperty);
	}

}
