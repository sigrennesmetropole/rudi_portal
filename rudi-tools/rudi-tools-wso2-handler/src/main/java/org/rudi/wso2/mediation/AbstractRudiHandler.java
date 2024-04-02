package org.rudi.wso2.mediation;

import java.util.Iterator;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.ManagedLifecycle;
import org.apache.synapse.Mediator;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.SynapseEnvironment;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.rest.AbstractHandler;
import org.apache.synapse.rest.RESTConstants;
import org.apache.synapse.transport.passthru.util.RelayUtils;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.APIManager;
import org.wso2.carbon.apimgt.api.model.API;
import org.wso2.carbon.apimgt.api.model.APIIdentifier;
import org.wso2.carbon.apimgt.gateway.handlers.Utils;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.APIManagerFactory;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
abstract class AbstractRudiHandler extends AbstractHandler implements ManagedLifecycle {

	protected static final String API_UT_API_PUBLISHER_PROPERTY = "api.ut.apiPublisher";

	protected static final String API_UT_API_PROPERTY = "api.ut.api";

	private static final String OPEN_API_STRING_PROPERTY_KEY = "OPEN_API_STRING";

	private static final String OPEN_API_OBJECT_PROPERTY_KEY = "OPEN_API_OBJECT";

	private static final Log LOGGER = LogFactory.getLog(AbstractRudiHandler.class);

	private static final String BINARY_CONTENT_TYPE = "application/octet-stream";

	/**
	 * Réponse renvoyée par {@link #handleRequest(MessageContext)} ou {@link #handleResponse(MessageContext)} pour indiquer qu'on peut continuer le
	 * traitement par le(s) Handler(s).
	 */
	private static final boolean CONTINUE = true;

	/**
	 * Réponse renvoyée par {@link #handleRequest(MessageContext)} ou {@link #handleResponse(MessageContext)} pour indiquer qu'on ne doit pas continuer le
	 * traitement par le(s) Handler(s). En cas d'erreurs, par exemple.
	 */
	private static final boolean ABORT = true;
	protected static final String DEFAULT_ERROR_MESSAGE_PREFIX = "Failed to handle response for API: ";
	private APIManager fixedAPIManager;

	/**
	 * @return true si on doit continuer l'exécution du handler Si on renvoie false dans handleRequest et true dans handleResponse, WSO2 renvoie un HTTP
	 *         202 sans body
	 */
	@Override
	public boolean handleRequest(MessageContext messageContext) {
		logAll(messageContext, "In");

		try {
			if (engageRequest(messageContext)) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("EncryptedHandler engaged for"
							+ messageContext.getProperty(RESTConstants.REST_API_CONTEXT));
				}
				doHandleRequest(messageContext);
			}
		} catch (Exception e) {
			final var errorMessagePrefix = getErrorMessage(e);
			LOGGER.error(errorMessagePrefix + messageContext.getProperty(RESTConstants.SYNAPSE_REST_API), e);
			onError(messageContext, 500, errorMessagePrefix + e.getMessage());
			return ABORT;
		}

		// Si on renvoie false dans handleRequest et true dans handleResponse, WSO2 renvoie un HTTP 202 sans body
		return CONTINUE;
	}

	/**
	 * @return true si ce handler doit être engagé pour traiter la requête reçue par l'API Manager
	 */
	protected boolean engageRequest(MessageContext messageContext) throws Exception {
		return false; // Par défaut un handler ne traite pas la requête
	}

	protected void doHandleRequest(MessageContext messageContext) throws Exception {
		// Par défaut un handler ne traite pas la requête
	}

	/**
	 * @return true si on doit continuer l'exécution du handler Si on renvoie false, il faut appeler {@link #onError(MessageContext, int, String)} sinon
	 *         WSO2 ne transmet plus la réponse et laisse tomber le client en timeout (socket hang up)
	 */
	@Override
	public boolean handleResponse(MessageContext messageContext) {
		logAll(messageContext, "Out");

		try {
			if (engageResponse(messageContext)) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("EncryptedHandler engaged for"
							+ messageContext.getProperty(RESTConstants.REST_API_CONTEXT));
				}
				doHandleResponse(messageContext);
			}
		} catch (Exception e) {
			final var errorMessage = getErrorMessage(e);
			LOGGER.error(errorMessage + " : " + messageContext.getProperty(RESTConstants.SYNAPSE_REST_API), e);
			onError(messageContext, 500, errorMessage);
			return ABORT;
		}

		// Si on renvoie false, WSO2 ne transmet plus la réponse et laisse tomber le client en timeout (socket hang up)
		return CONTINUE;
	}

	/**
	 * @return true si ce handler doit être engagé pour traiter la réponse renvoyée par l'API Manager
	 */
	protected boolean engageResponse(MessageContext messageContext) throws Exception {
		return false; // Par défaut un handler ne traite pas la réponse
	}

	protected void doHandleResponse(MessageContext messageContext) throws Exception {
		// Par défaut un handler ne traite pas la réponse
	}

	protected void replaceBody(MessageContext messageContext, BodyReplacer bodyReplacer,
			Function<MessageContext, String> contentTypeComputer,
			@Nullable Function<MessageContext, String> contentDispositionComputer) throws Exception {

		// On sauvegarde le Content-Type original
		final var originalContentType = getContentType(messageContext);

		// On force le Content-Type pour empêcher WSO2 d'interpréter le body comme du XML
		setContentType(messageContext, BINARY_CONTENT_TYPE);

		// récupération du message Axis2 sous jacent
		final var axis2MC = getAxis2MessageContext(messageContext);

		// construction du message
		RelayUtils.buildMessage(axis2MC, true);

		// On restaure le Content-Type original avant traitement par le contentTypeComputer
		setContentType(messageContext, originalContentType);

		// récupération de la réponse
		// Remplacement du body
		SOAPUtils.replaceBody(axis2MC.getEnvelope(), bodyReplacer);

		// Remplacement du Content-Type
		final var newContentType = contentTypeComputer.apply(messageContext);
		if (newContentType != null) {
			setContentType(messageContext, newContentType);
		}

		// Remplacement du Content-Disposition
		if (contentDispositionComputer != null) {
			final var newContentDisposition = contentDispositionComputer.apply(messageContext);
			if (newContentDisposition != null) {
				setContentDisposition(messageContext, newContentDisposition);
			}
		}
	}

	protected org.apache.axis2.context.MessageContext getAxis2MessageContext(MessageContext messageContext) {
		return ((Axis2MessageContext) messageContext).getAxis2MessageContext();
	}

	protected String getContentType(MessageContext messageContext) {
		final var axis2MC = getAxis2MessageContext(messageContext);
		return Axis2MessageContextUtils.getContentType(axis2MC);
	}

	protected void setContentType(MessageContext messageContext, final String mimeType) {
		final var axis2MC = getAxis2MessageContext(messageContext);
		Axis2MessageContextUtils.setContentType(axis2MC, mimeType);
	}

	@Nullable
	protected String getContentDisposition(MessageContext messageContext) {
		final var axis2MC = getAxis2MessageContext(messageContext);
		return Axis2MessageContextUtils.getContentDisposition(axis2MC);
	}

	protected void setContentDisposition(MessageContext messageContext, final String contentDisposition) {
		final var axis2MC = getAxis2MessageContext(messageContext);
		Axis2MessageContextUtils.setContentDisposition(axis2MC, contentDisposition);
	}

	protected String getErrorMessage(Exception e) {
		return DEFAULT_ERROR_MESSAGE_PREFIX;
	}

	/**
	 * Créé un identifiant unique d'API à partir du context
	 */
	protected APIIdentifier createAPIIdentifier(MessageContext messageContext) {
		final var selectedApi = Utils.getSelectedAPI(messageContext);
		String providerName = (String) messageContext.getProperty(API_UT_API_PUBLISHER_PROPERTY);
		String apiName = (String) messageContext.getProperty(API_UT_API_PROPERTY);
		return new APIIdentifier(providerName, apiName, selectedApi.getVersion());
	}

	protected API getEngagedApi(MessageContext messageContext) throws APIManagementException {
		final var apiIdentifier = createAPIIdentifier(messageContext);
		final var apiManager = getAPIManager();
		return apiManager.getLightweightAPIByUUID(apiIdentifier.getUUID(),
				MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
	}

	/**
	 * Effectue la médiation pour les erreurs
	 */
	protected void onError(MessageContext messageContext, int statusCode, String errorMessage) {
		messageContext.setProperty(APIConstants.CUSTOM_HTTP_STATUS_CODE, statusCode);
		messageContext.setProperty(APIConstants.CUSTOM_ERROR_CODE, statusCode);
		messageContext.setProperty(APIConstants.CUSTOM_ERROR_MESSAGE, errorMessage);
		Mediator resourceMisMatchedSequence = messageContext.getSequence(RESTConstants.NO_MATCHING_RESOURCE_HANDLER);
		if (resourceMisMatchedSequence != null) {
			resourceMisMatchedSequence.mediate(messageContext);
		}
	}

	@Override
	public void init(SynapseEnvironment se) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Initializing EncryptedMediaHandler Handler instance");
		}
	}

	@Override
	public void destroy() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Destroying EncryptedMediaHandler Handler instance");
		}
	}

	private void logAll(MessageContext messageContext, String direction) {
		LOGGER.debug("====================>" + direction + "=>");
		logApiInfo(messageContext);
		logMessageContextProperties(messageContext);
		logAxisMessageContext(messageContext);
	}

	private void logApiInfo(MessageContext messageContext) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("MessageContext:" + messageContext);
			String apiContext = (String) messageContext.getProperty(RESTConstants.REST_API_CONTEXT);
			LOGGER.debug("ApiContext:" + apiContext);
			String apiVersion = (String) messageContext.getProperty(RESTConstants.SYNAPSE_REST_API_VERSION);
			LOGGER.debug("ApiVersion:" + apiVersion);
		}
	}

	@SuppressWarnings("unchecked")
	private void logMessageContextProperties(MessageContext messageContext) {
		if (LOGGER.isDebugEnabled()) {
			// noinspection unchecked : la classe MessageContext n'est pas modifiable
			for (final var key : (Iterable<String>) messageContext.getPropertyKeySet()) {
				Object value = messageContext.getProperty(key);
				if (!key.equals(OPEN_API_STRING_PROPERTY_KEY) && !key.equals(OPEN_API_OBJECT_PROPERTY_KEY)) {
					LOGGER.debug(
							"Property(" + key + " of " + (value != null ? value.getClass() : "null") + ")=" + value);
				}
			}
		}
	}

	private void logAxisMessageContext(MessageContext messageContext) {
		if (LOGGER.isDebugEnabled()) {
			final var axis2MC = ((Axis2MessageContext) messageContext).getAxis2MessageContext();

			Iterator<String> it2 = axis2MC.getPropertyNames();
			while (it2.hasNext()) {
				String key = it2.next();
				LOGGER.debug("Property axis(" + key + ")=" + axis2MC.getProperty(key));
			}
		}
	}

	@SuppressWarnings("unused") // cette méthode est utilisée pour les tests
	protected void setFixedAPIManager(APIManager fixedAPIManager) {
		this.fixedAPIManager = fixedAPIManager;
	}

	APIManager getAPIManager() throws APIManagementException {
		if (fixedAPIManager != null) {
			return fixedAPIManager;
		} else {
			return APIManagerFactory.getInstance().getAPIConsumer("admin");
		}
	}

}
