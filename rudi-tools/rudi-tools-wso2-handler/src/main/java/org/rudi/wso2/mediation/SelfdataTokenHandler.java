package org.rudi.wso2.mediation;


import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.apache.synapse.MessageContext;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.model.API;

import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
public class SelfdataTokenHandler extends AbstractRudiHandler {

	private static final String GDPR_SENSITIVE_PROPERTY = "gdpr_sensitive";
	private static final String GLOBAL_ID_PROPERTY = "global_id";
	private static final String CONTENT_RESPONSE_EMPTY = "Le microservice selfdata a renvoyé une erreur HTTP sans body.";
	private final PropertiesLoader propertiesLoader = new PropertiesLoader();
	private SelfdataHttpClient selfdataHttpClient;

	@SuppressWarnings("unused")
	// Utilisé par WSO2 au chargement du Handler quand on définit la propriété "propertiesPath" dans le velocity_template.xml
	public void setPropertiesPath(String stringValue) throws IOException {
		final var propertiesPath = Path.of(stringValue);
		final var selfdataProperties = propertiesLoader.loadProperties(propertiesPath, SelfdataProperties.class);
		final var selfdataOauth2Properties = propertiesLoader.loadProperties(propertiesPath, SelfdataOauth2Properties.class);
		selfdataHttpClient = new SelfdataHttpClient(selfdataProperties, selfdataOauth2Properties);
	}

	/**
	 * @return true si ce handler doit être engagé pour traiter la requête reçue par l'API Manager
	 */
	@Override
	protected boolean engageRequest(MessageContext messageContext) {
		final API engagedApi;
		try {
			engagedApi = getEngagedApi(messageContext);
		} catch (APIManagementException e) {
			log.error("Failed to get engaged API. This handler will not be engaged.");
			return false;
		}

		return AdditionalPropertiesUtil.additionalPropertyIsTrue(GDPR_SENSITIVE_PROPERTY, engagedApi);
	}

	@Override
	protected void doHandleRequest(MessageContext messageContext) throws APIManagementException {
		log.info("Récupération du token selfdata...");

		final var datasetUuid = getDatasetUuid(messageContext);
		final var login = MessageContextUtils.getAuthenticatedUserLogin(messageContext);
		log.debug("Appel de l'API getMatchingToken de selfdata pour récuperer le token de l'utilisateur");
		final var selfdataToken = selfdataHttpClient.getSelfdataToken(datasetUuid, login);
		// Add token to request header
		addSelfdataTokenToHeader(messageContext, selfdataToken);

		log.info("Token selfdata ajouté à la requête.");
	}

	@Nonnull
	private UUID getDatasetUuid(MessageContext messageContext) throws APIManagementException {
		final var engagedApi = getEngagedApi(messageContext);
		final var additionalProperty = Objects.requireNonNull(AdditionalPropertiesUtil.getAdditionalProperty(GLOBAL_ID_PROPERTY, engagedApi));
		return UUID.fromString(additionalProperty);
	}

	private void addSelfdataTokenToHeader(MessageContext messageContext, UUID selfdataToken) {
		Axis2MessageContextUtils.setSelfdataTokenHeader(getAxis2MessageContext(messageContext), selfdataToken);
	}

	@Override
	protected String getErrorMessage(Exception e) {
		if (e instanceof IllegalArgumentException) {
			return CONTENT_RESPONSE_EMPTY + "\n" + DEFAULT_ERROR_MESSAGE_PREFIX;
		}
		return DEFAULT_ERROR_MESSAGE_PREFIX;
	}
}
