package org.rudi.wso2.mediation;

import javax.annotation.Nullable;

import org.wso2.carbon.apimgt.api.model.API;

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

}
