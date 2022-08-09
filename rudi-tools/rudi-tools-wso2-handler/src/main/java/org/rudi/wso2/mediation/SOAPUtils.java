package org.rudi.wso2.mediation;

import java.io.IOException;
import java.io.InputStream;

import javax.activation.DataHandler;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.axiom.om.OMText;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.synapse.transport.passthru.util.StreamingOnRequestDataSource;

class SOAPUtils {

	protected static final String BINARY_LOCAL_NAME = "binary";

	private SOAPUtils() {
	}

	static void replaceBody(SOAPEnvelope envelope, BodyReplacer bodyReplacer) throws Exception {
		final var textNode = getBinaryTextNode(envelope);

		if (textNode != null) {
			final var originalBody = getDataHandlerInputStream(textNode);
			final var replacedBody = bodyReplacer.replaceBody(originalBody);

			final var newDataHandler = new DataHandler(new StreamingOnRequestDataSource(replacedBody));
			final var newTextNode = envelope.getOMFactory().createOMText(newDataHandler, true);

			textNode.insertSiblingBefore(newTextNode);
			textNode.detach();
		}
	}

	@Nullable
	static OMText getBinaryTextNode(SOAPEnvelope envelope) {
		final var body = envelope.getBody();

		final var element = body.getFirstElement();
		if (element.getLocalName().equalsIgnoreCase(BINARY_LOCAL_NAME)) {
			final var subChild = element.getFirstOMChild();
			if (subChild instanceof OMText && ((OMText) subChild).isBinary()) {
				return ((OMText) subChild);
			}
		}

		return null;
	}

	@Nullable
	static InputStream getDataHandlerInputStream(@Nonnull OMText textNode) throws IOException {
		final var dataHandler = (DataHandler) textNode.getDataHandler();
		return dataHandler.getInputStream();
	}

	@Nullable
	static InputStream getBinaryTextNodeDataHandlerInputStream(SOAPEnvelope envelope) throws IOException {
		final var textNode = getBinaryTextNode(envelope);
		if (textNode == null) {
			return null;
		}
		return getDataHandlerInputStream(textNode);
	}

}
