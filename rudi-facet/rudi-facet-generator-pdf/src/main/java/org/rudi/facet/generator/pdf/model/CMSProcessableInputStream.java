/**
 * RUDI Portail
 */
package org.rudi.facet.generator.pdf.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSTypedData;

/**
 * @author FNI18300
 *
 */
public class CMSProcessableInputStream implements CMSTypedData {

	InputStream inputStream;
	private final ASN1ObjectIdentifier contentType;

	public CMSProcessableInputStream(InputStream is) {
		this(new ASN1ObjectIdentifier(CMSObjectIdentifiers.data.getId()), is);
	}

	public CMSProcessableInputStream(ASN1ObjectIdentifier type, InputStream is) {
		contentType = type;
		inputStream = is;
	}

	@Override
	public Object getContent() {
		return inputStream;
	}

	@Override
	public void write(OutputStream outputStream) throws IOException, CMSException {
		// read the content only one time
		IOUtils.copy(inputStream, outputStream);
		inputStream.close();
	}

	@Override
	public ASN1ObjectIdentifier getContentType() {
		return contentType;
	}
}
