/**
 * RUDI Portail
 */
package org.rudi.facet.generator.pdf.impl;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdfwriter.compress.CompressParameters;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureOptions;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDSignatureField;
import org.apache.pdfbox.util.Matrix;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.rudi.common.core.DocumentContent;
import org.rudi.facet.generator.TemporaryHelper;
import org.rudi.facet.generator.model.GenerationFormat;
import org.rudi.facet.generator.pdf.PDFSigner;
import org.rudi.facet.generator.pdf.config.PDFSignerConfiguration;
import org.rudi.facet.generator.pdf.exception.SignerException;
import org.rudi.facet.generator.pdf.model.CMSProcessableInputStream;
import org.rudi.facet.generator.pdf.model.SignatureDescription;
import org.rudi.facet.generator.pdf.util.SignatureUtils;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author FNI18300
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class PDFSignerImpl implements PDFSigner, SignatureInterface {

	private static final int ROTATION_0 = 0;

	private static final int ROTATION_270 = 270;

	private static final int ROTATION_180 = 180;

	private static final int ROTATION_90 = 90;

	private static final int DEFAULT_LEFT = 20;

	private static final int DEFAULT_TOP = 20;

	private static final int DEFAULT_WIDTH = 200;

	private static final int DEFAULT_HEIGHT = 100;

	private final PDFSignerConfiguration pdfSignerConfiguration;

	private final TemporaryHelper temporaryHelper;

	private Certificate[] certificateChains = null;

	private PrivateKey privateKey = null;

	@Override
	public DocumentContent sign(DocumentContent source, SignatureDescription signatureDescription)
			throws SignerException, IOException {
		DocumentContent result = null;
		// controle de la source
		checkSource(source);

		// création de la destination
		File destinationFile = temporaryHelper.createOutputFile();
		if (log.isDebugEnabled()) {
			log.debug("Fichier temporaire de génération:" + destinationFile);
		}

		// récupération du fichier source
		File sourceFile = getSourceFile(source);

		try (OutputStream os = new FileOutputStream(destinationFile);
				PDDocument document = Loader.loadPDF(new RandomAccessReadBufferedFile(sourceFile))) {

			int accessPermissions = SignatureUtils.getMDPPermission(document);
			if (accessPermissions == 1) {
				throw new IllegalStateException(
						"No changes to the document are permitted due to DocMDP transform parameters dictionary");
			}

			// création de la signature
			PDSignature signature = createSignature(document, signatureDescription);

			if (accessPermissions == 0) {
				SignatureUtils.setMDPPermission(document, signature, 2);
			}

			// register signature dictionary and sign interface
			// les options permettent d'ajouter un élément visuel dans la signature
			SignatureOptions options = createOptions(document, signatureDescription);
			if (options == null) {
				document.addSignature(signature, this);
			} else {
				document.addSignature(signature, this, options);
			}

			// sauvegarde du document initial
			document.save(os, CompressParameters.NO_COMPRESSION);

			result = new DocumentContent(source.getFileName(), source.getContentType(), destinationFile);
		} catch (Exception e) {
			throw new SignerException("Failed to sign " + source, e);
		}

		return result;
	}

	private void checkSource(DocumentContent source) throws SignerException {
		// la source doit etre de type pdf
		if (!GenerationFormat.PDF.getMimeType().equals(source.getContentType())) {
			throw new SignerException("Input must be with format " + GenerationFormat.PDF.getMimeType());
		}
	}

	private File getSourceFile(DocumentContent source) throws IOException {
		File sourceFile = null;
		// la source peut être un fichier ou un stream
		if (source.isFile()) {
			sourceFile = source.getFile();
		} else if (source.isStream()) {
			// et est converti en fichier si nécessaire
			sourceFile = temporaryHelper.createOutputFile();
			FileUtils.copyInputStreamToFile(source.getFileStream(), sourceFile);
		} else {
			throw new IOException("Input must be a stream or a file");
		}
		return sourceFile;
	}

	/**
	 * liste les alias possibles
	 *
	 * @param ks
	 * @throws KeyStoreException
	 */
	protected void exploreKeyStore(KeyStore ks) throws KeyStoreException {
		Enumeration<String> aliases = ks.aliases();
		if (aliases != null) {
			while (aliases.hasMoreElements()) {
				String alias = aliases.nextElement();
				try {
					Certificate[] chain0 = ks.getCertificateChain(alias);
					Certificate cert = ks.getCertificate(alias);
					Key key = ks.getKey(alias, pdfSignerConfiguration.getKeyStoreKeyPasswordChars());
					if (log.isDebugEnabled()) {
						log.debug("Alias(" + alias + "): chain=" + Arrays.toString(chain0) + " / " + cert + ", key="
								+ key);
					}
				} catch (Exception e) {
					log.warn("Alias(" + alias + ")", e);
				}
			}
		}
	}

	protected SignatureOptions createOptions(PDDocument document, SignatureDescription signatureDescription)
			throws IOException, SignerException {
		// si pas d'info alors par de partie visible
		if (signatureDescription.getVisibleSignatureDescription() == null) {
			return null;
		}

		// on s'assure d'avoir un formulaire
		PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
		if (acroForm == null) {
			acroForm = new PDAcroForm(document);
			document.getDocumentCatalog().setAcroForm(acroForm);
		}
		acroForm.getCOSObject().setNeedToBeUpdated(true);

		PDRectangle rectangle = createOptionsRectangle(acroForm, signatureDescription, document);

		if (acroForm.getNeedAppearances()) {
			// PDFBOX-3738 NeedAppearances true results in visible signature becoming invisible
			// with Adobe Reader
			if (acroForm.getFields().isEmpty()) {
				// we can safely delete it if there are no fields
				acroForm.getCOSObject().removeItem(COSName.NEED_APPEARANCES);
				// note that if you've set MDP permissions, the removal of this item
				// may result in Adobe Reader claiming that the document has been changed.
				// and/or that field content won't be displayed properly.
				// ==> decide what you prefer and adjust your code accordingly.
			} else {
				log.warn("/NeedAppearances is set, signature may be ignored by Adobe Reader");
			}
		}

		return createOptions(signatureDescription, rectangle);
	}

	protected PDSignature createSignature(PDDocument document, SignatureDescription signatureDescription) {
		PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();

		PDSignature signature = null;
		// sign a PDF with an existing empty signature, as created by the CreateEmptySignatureForm example.
		if (acroForm != null) {
			signature = findExistingSignature(acroForm, signatureDescription.getFieldname());
		}

		if (signature == null) {
			// create signature dictionary
			signature = new PDSignature();
		}

		signature.setFilter(PDSignature.FILTER_ADOBE_PPKLITE); // default filter
		// subfilter for basic and PAdES Part 2 signatures
		signature.setSubFilter(PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED);
		signature.setName(signatureDescription.getName());
		signature.setLocation(signatureDescription.getLocation());
		signature.setReason(signatureDescription.getReason());
		// the signing date, needed for valid signature
		if (signatureDescription.getDate() != null) {
			Instant instant = signatureDescription.getDate().atZone(ZoneId.systemDefault()).toInstant();
			Date date = Date.from(instant);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			signature.setSignDate(calendar);
		} else {
			signature.setSignDate(Calendar.getInstance());
		}
		return signature;
	}

	protected SignatureOptions createOptions(SignatureDescription signatureDescription, PDRectangle rectangle)
			throws IOException {
		// création des options de signature (pour la partie visible)
		SignatureOptions signatureOptions = new SignatureOptions();
		signatureOptions.setPage(signatureDescription.getVisibleSignatureDescription().getPage());
		if (signatureDescription.getVisibleSignatureDescription().getImage() != null) {
			signatureOptions.setVisualSignature(createVisualSignatureTemplate(createPseudoFile(), 0, rectangle,
					signatureDescription.getVisibleSignatureDescription().getImage()));
		}

		return signatureOptions;
	}

	protected PDDocument createPseudoFile() throws IOException {
		// on créé un fichier qui contient l'image de signature
		File result = temporaryHelper.createOutputFile();
		try (PDDocument document = new PDDocument()) {
			PDPage page = new PDPage(PDRectangle.A4);
			document.addPage(page);
			PDPageContentStream contentStream = new PDPageContentStream(document, page);
			contentStream.setFont(new PDType1Font(FontName.HELVETICA), 14);

			PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
			if (acroForm == null) {
				acroForm = new PDAcroForm(document);
				document.getDocumentCatalog().setAcroForm(acroForm);
			}
			acroForm.getCOSObject().setNeedToBeUpdated(true);

			acroForm.setSignaturesExist(true);
			acroForm.setAppendOnly(true);
			acroForm.getCOSObject().setDirect(true);
			PDSignatureField signatureField = new PDSignatureField(acroForm);
			PDAnnotationWidget firstWidget = signatureField.getWidgets().get(0);
			// backward linking
			firstWidget.setPage(page);

			// Make sure that the content stream is closed
			contentStream.close();

			// Save the results and ensure that the document is properly closed
			document.save(result);
			return document;
		}

	}

	// create a template PDF document with empty signature and return it as a stream.
	protected File createVisualSignatureTemplate(PDDocument sourceDocument, int pageNumber, PDRectangle rectangle,
			DocumentContent image) throws IOException {
		File result = temporaryHelper.createOutputFile();
		try (PDDocument document = new PDDocument()) {
			PDPage page = new PDPage(sourceDocument.getPage(pageNumber).getMediaBox());
			document.addPage(page);
			PDAcroForm acroForm = new PDAcroForm(document);
			document.getDocumentCatalog().setAcroForm(acroForm);
			PDSignatureField signatureField = new PDSignatureField(acroForm);
			PDAnnotationWidget widget = signatureField.getWidgets().get(0);
			List<PDField> acroFormFields = acroForm.getFields();
			acroForm.setSignaturesExist(true);
			acroForm.setAppendOnly(true);
			acroForm.getCOSObject().setDirect(true);
			acroFormFields.add(signatureField);

			widget.setRectangle(rectangle);

			// from PDVisualSigBuilder.createHolderForm()
			PDStream stream = new PDStream(document);
			PDFormXObject form = new PDFormXObject(stream);
			PDResources res = new PDResources();
			form.setResources(res);
			form.setFormType(1);
			PDRectangle bbox = new PDRectangle(rectangle.getWidth(), rectangle.getHeight());
			Matrix initialScale = null;
			switch (sourceDocument.getPage(pageNumber).getRotation()) {
			case ROTATION_90:
				form.setMatrix(AffineTransform.getQuadrantRotateInstance(1));
				initialScale = Matrix.getScaleInstance(bbox.getWidth() / bbox.getHeight(),
						bbox.getHeight() / bbox.getWidth());
				break;
			case ROTATION_180:
				form.setMatrix(AffineTransform.getQuadrantRotateInstance(2));
				break;
			case 270:
				form.setMatrix(AffineTransform.getQuadrantRotateInstance(3));
				initialScale = Matrix.getScaleInstance(bbox.getWidth() / bbox.getHeight(),
						bbox.getHeight() / bbox.getWidth());
				break;
			case ROTATION_0:
			default:
				break;
			}
			form.setBBox(bbox);

			// from PDVisualSigBuilder.createAppearanceDictionary()
			PDAppearanceDictionary appearance = new PDAppearanceDictionary();
			appearance.getCOSObject().setDirect(true);
			PDAppearanceStream appearanceStream = new PDAppearanceStream(form.getCOSObject());
			appearance.setNormalAppearance(appearanceStream);
			widget.setAppearance(appearance);

			try (PDPageContentStream cs = new PDPageContentStream(document, appearanceStream)) {
				// for 90° and 270° scale ratio of width / height
				// not really sure about this
				// why does scale have no effect when done in the form matrix???
				if (initialScale != null) {
					cs.transform(initialScale);
				}

				// show background (just for debugging, to see the rect size + position)
				cs.setNonStrokingColor(Color.WHITE);
				cs.addRect(-5000, -5000, 10000, 10000);
				cs.fill();

				// show background image
				// save and restore graphics if the image is too large and needs to be scaled
				cs.saveGraphicsState();

				PDImageXObject img = PDImageXObject.createFromFileByExtension(getSourceFile(image), document);
				float sx = rectangle.getWidth() / img.getWidth();
				float sy = rectangle.getHeight() / img.getHeight();
				float ss = Math.min(sx, sy);
				cs.transform(Matrix.getScaleInstance(ss, ss));
				cs.drawImage(img, 0, 0);
				cs.restoreGraphicsState();
			}

			document.save(result);
		}
		return result;
	}

	protected PDRectangle createSignatureRectangle(PDDocument document, Rectangle2D requestRectangle) {
		float x = (float) requestRectangle.getX();
		float y = (float) requestRectangle.getY();
		float width = (float) requestRectangle.getWidth();
		float height = (float) requestRectangle.getHeight();
		PDPage page = document.getPage(0);
		PDRectangle pageRect = page.getCropBox();
		PDRectangle rect = new PDRectangle();
		// signing should be at the same position regardless of page rotation.
		switch (page.getRotation()) {
		case ROTATION_90:
			rect.setLowerLeftY(x);
			rect.setUpperRightY(x + width);
			rect.setLowerLeftX(y);
			rect.setUpperRightX(y + height);
			break;
		case ROTATION_180:
			rect.setUpperRightX(pageRect.getWidth() - x);
			rect.setLowerLeftX(pageRect.getWidth() - x - width);
			rect.setLowerLeftY(y);
			rect.setUpperRightY(y + height);
			break;
		case ROTATION_270:
			rect.setLowerLeftY(pageRect.getHeight() - x - width);
			rect.setUpperRightY(pageRect.getHeight() - x);
			rect.setLowerLeftX(pageRect.getWidth() - y - height);
			rect.setUpperRightX(pageRect.getWidth() - y);
			break;
		case ROTATION_0:
		default:
			rect.setLowerLeftX(x);
			rect.setUpperRightX(x + width);
			rect.setLowerLeftY(pageRect.getHeight() - y - height);
			rect.setUpperRightY(pageRect.getHeight() - y);
			break;
		}
		return rect;
	}

	// Find an existing signature (assumed to be empty). You will usually not need this.
	protected PDSignature findExistingSignature(PDAcroForm acroForm, String sigFieldName) {
		PDSignature signature = null;
		PDSignatureField signatureField;
		if (acroForm != null) {
			signatureField = (PDSignatureField) acroForm.getField(sigFieldName);
			if (signatureField != null) {
				// retrieve signature dictionary
				signature = signatureField.getSignature();
				if (signature == null) {
					signature = new PDSignature();
					// after solving PDFBOX-3524
					// signatureField.setValue(signature)
					// until then:
					signatureField.getCOSObject().setItem(COSName.V, signature);
				} else {
					throw new IllegalStateException("The signature field " + sigFieldName + " is already signed.");
				}
			}
		}
		return signature;
	}

	protected Certificate[] loadCertificates() throws IOException, KeyStoreException, NoSuchAlgorithmException,
			CertificateException, UnrecoverableKeyException {
		// chargement du certificat et de la clé
		if (certificateChains == null || privateKey == null) {

			File f = new File(pdfSignerConfiguration.getKeyStorePath());

			if (f.exists() && f.isFile()) {
				try (InputStream keyStoreStream = new FileInputStream(pdfSignerConfiguration.getKeyStorePath());) {
					certificateChains = loadCertificate(keyStoreStream);
				}
			} else {
				try (InputStream keyStoreStream = Thread.currentThread().getContextClassLoader()
						.getResourceAsStream(pdfSignerConfiguration.getKeyStorePath());) {
					certificateChains = loadCertificate(keyStoreStream);
				}
			}
		}

		return certificateChains;
	}

	protected Certificate[] loadCertificate(InputStream keyStoreStream) throws KeyStoreException,
			NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException {
		Security.addProvider(new BouncyCastleProvider());
		KeyStore ks = KeyStore.getInstance(pdfSignerConfiguration.getKeyStoreType());
		ks.load(keyStoreStream, pdfSignerConfiguration.getKeyStorePasswordChars());
		if (pdfSignerConfiguration.isDebug()) {
			exploreKeyStore(ks);
		}
		privateKey = (PrivateKey) ks.getKey(pdfSignerConfiguration.getKeyStoreKeyAlias(),
				pdfSignerConfiguration.getKeyStoreKeyPasswordChars());

		return ks.getCertificateChain(pdfSignerConfiguration.getKeyStoreKeyAlias());
	}

	@Override
	public byte[] sign(InputStream content) throws IOException {
		// signature du document
		try {
			CMSSignedDataGenerator signedDataGenerator = new CMSSignedDataGenerator();
			Certificate[] chains = loadCertificates();
			X509Certificate cert = (X509Certificate) chains[0];
			ContentSigner sha1Signer = new JcaContentSignerBuilder("SHA256WithRSA").build(privateKey);
			signedDataGenerator.addSignerInfoGenerator(
					new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().build())
							.build(sha1Signer, cert));
			signedDataGenerator.addCertificates(new JcaCertStore(Arrays.asList(chains)));
			CMSProcessableInputStream msg = new CMSProcessableInputStream(content);
			CMSSignedData signedData = signedDataGenerator.generate(msg, false);

			return signedData.getEncoded();
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	private PDRectangle createOptionsRectangle(PDAcroForm acroForm, SignatureDescription signatureDescription,
			PDDocument document) {
		PDRectangle rectangle = null;
		// sign a PDF with an existing empty signature, as created by the CreateEmptySignatureForm example.
		PDSignature signature = findExistingSignature(acroForm, signatureDescription.getFieldname());
		if (signature != null) {
			rectangle = acroForm.getField(signatureDescription.getFieldname()).getWidgets().get(0).getRectangle();
		}

		if (rectangle == null) {
			Rectangle sourceRectangle = signatureDescription.getVisibleSignatureDescription().getRectangle();
			Rectangle2D defaultRectangle = new Rectangle2D.Double(
					sourceRectangle != null ? sourceRectangle.getX() : DEFAULT_LEFT,
					sourceRectangle != null ? sourceRectangle.getY() : DEFAULT_TOP,
					sourceRectangle != null ? sourceRectangle.getWidth() : DEFAULT_WIDTH,
					sourceRectangle != null ? sourceRectangle.getHeight() : DEFAULT_HEIGHT);
			rectangle = createSignatureRectangle(document, defaultRectangle);
		}

		return rectangle;
	}
}
