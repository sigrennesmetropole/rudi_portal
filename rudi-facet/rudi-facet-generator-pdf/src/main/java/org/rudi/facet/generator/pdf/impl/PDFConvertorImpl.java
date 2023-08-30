/**
 * 
 */
package org.rudi.facet.generator.pdf.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.multipdf.PDFCloneUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.encoding.Encoding;
import org.apache.pdfbox.pdmodel.graphics.color.PDOutputIntent;
import org.apache.pdfbox.preflight.PreflightDocument;
import org.apache.pdfbox.preflight.ValidationResult;
import org.apache.pdfbox.preflight.ValidationResult.ValidationError;
import org.apache.pdfbox.preflight.parser.PreflightParser;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.AdobePDFSchema;
import org.apache.xmpbox.schema.DublinCoreSchema;
import org.apache.xmpbox.schema.PDFAIdentificationSchema;
import org.apache.xmpbox.schema.XMPBasicSchema;
import org.apache.xmpbox.type.BadFieldValueException;
import org.apache.xmpbox.xml.XmpSerializer;
import org.ghost4j.Ghostscript;
import org.ghost4j.GhostscriptException;
import org.rudi.common.core.DocumentContent;
import org.rudi.facet.generator.TemporaryHelper;
import org.rudi.facet.generator.model.GenerationFormat;
import org.rudi.facet.generator.pdf.PDFConvertor;
import org.rudi.facet.generator.pdf.config.PDFConverterConfiguration;
import org.rudi.facet.generator.pdf.exception.ConvertorException;
import org.rudi.facet.generator.pdf.exception.ValidationException;
import org.rudi.facet.generator.pdf.model.ValidationResultItem;
import org.springframework.stereotype.Component;

import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service de conversion docx en PDF et de PDF en PDFA<br/>
 * 
 * @author FNI18300
 *
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PDFConvertorImpl implements PDFConvertor {

	private static final String RUDI_CREATOR = "Rudi";

	private static final String S_RGB_IEC61966_2_1 = "sRGB IEC61966-2.1";

	private final TemporaryHelper temporaryHelper;

	private final PDFConverterConfiguration pdfConverterConfiguration;

	@Override
	public DocumentContent convertDocx2PDF(DocumentContent input) throws ConvertorException, IOException {
		DocumentContent output = null;
		checkInputData(input);
		File generateFile = temporaryHelper.createOutputFile();

		try (InputStream is = openInputFile(input); OutputStream os = new FileOutputStream(generateFile)) {
			XWPFDocument document = new XWPFDocument(is);
			PdfOptions options = PdfOptions.create();
			PdfConverter.getInstance().convert(document, os, options);

			output = new DocumentContent(buildOutputFileName(input), GenerationFormat.PDF.getMimeType(), generateFile);

		} catch (Exception e) {
			throw new ConvertorException("Failed to convert to pdf" + input.getFile(), e);
		}
		return output;
	}

	private String buildOutputFileName(DocumentContent input) {
		String name = input.getFileName();
		int index = name.lastIndexOf('.');
		if (index >= 0) {
			name = name.substring(0, index);
		}
		return GenerationFormat.PDF.generateFileName(name);
	}

	private void checkInputData(DocumentContent input) {
		if (input == null || !input.getContentType().equals(GenerationFormat.DOCX.getMimeType())) {
			throw new IllegalArgumentException("Unsupported format");
		}
	}

	private InputStream openInputFile(DocumentContent input) throws FileNotFoundException {
		if (input.isFile() || input.isStream()) {
			return input.getFileStream();
		} else {
			return null;
		}
	}

	private void checkSource(DocumentContent source) throws ConvertorException {
		// la source doit etre de type pdf
		if (!GenerationFormat.PDF.getMimeType().equals(source.getContentType())) {
			throw new ConvertorException("Input must be with format " + GenerationFormat.PDF.getMimeType());
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
	 * 
	 * <b>Attention:</b> la conversion en PDFA n'est que partielle elle requiert un document origine respectant déjà un certain nombre de règles parmis
	 * celles listées ci-dessous.
	 * 
	 * Les éléments clés de la conformité au PDF/A incluent :
	 * 
	 * <ul>
	 * <li>Les contenus audio et vidéo dans le document sont interdits.</li>
	 * <li>Les lancements de fichiers JavaScript et d'exécutables sont interdits.</li>
	 * <li>Toutes les polices (de caractères) doivent être intégrées au fichier du document et doivent pouvoir être intégrées légalement aussi pour un
	 * rendu universel et non limité. Cela s'applique aussi aux polices dites PostScript standard telles que Times ou Helvetica.</li>
	 * <li>Les espaces de couleur spécifiés de manière indépendante du périphérique.</li>
	 * <li>Le chiffrement du document est interdit. L'utilisation de métadonnées normalisées est requise.</li>
	 * <li>Les références de contenu externes au document sont interdites.</li>
	 * <li>L’algorithme de compression de données Lempel-Ziv-Welch(LZW) est interdit en raison de contraintes de propriété intellectuelle. Les modèles de
	 * compression d'image au format JPEG 2000 ne sont pas autorisés dans la norme PDF/A-1 (basée sur PDF 1.4) parce qu'il a été introduit pour la
	 * première fois dans PDF 1.5.</li>
	 * <li>La compression au format JPEG 2000 est autorisée dans les normes PDF/A-2 et PDF/A-3.</li>
	 * <li>Les objets et calques transparents (groupes de contenu facultatifs) sont interdits dans la norme PDF/A-1, mais sont autorisés dans la norme
	 * PDF/A-2.</li>
	 * <li>Les dispositions relatives aux signatures numériques conformément à la norme PAdES (PDF Advanced Electronic Signatures) sont prises en charge à
	 * partir de PDF/A-2.</li>
	 * <li>Les fichiers tiers (comme des documents bureautiques MS Word, MS Excel, etc.) intégrés dans un document PDF sont interdits dans la norme
	 * PDF/A-1, mais la norme PDF/A-2 permet l'incorporation uniquement de fichiers à la norme PDF/A, facilitant l'archivage d'ensembles de documents
	 * PDF/A dans un seul fichier. PDF/ A-3 permet d'incorporer n'importe quel format de fichier tel que XML, documents de DAO/CAO et autres dans des
	 * documents à la norme PDF/A.</li>
	 * <li>L'utilisation de formulaires basés sur XML comme XML Forms Architecture (XFA) est interdite dans la norme PDF/A (les données de formulaire XFA
	 * peuvent être conservées dans un fichier PDF/A-2 en passant de la clé XFA à l'arborescence Noms (Names) qui est elle-même la valeur de la clé
	 * XFAResources du dictionnaire Noms (Names) du dictionnaire de catalogue de documents).</li>
	 * <li>Les champs de formulaire PDF interactif doivent avoir un dictionnaire d'apparence (appearance dictionary) associé aux données du champ. Le
	 * dictionnaire d'apparence doit être utilisé lors du rendu du champ.</li>
	 * </ul>
	 * 
	 * Il est possible de valider un PDFA sur le site https://tools.pdfforge.org/fr/valider-pdfa
	 * 
	 */
	@Override
	public DocumentContent convertPDF2PDFA(DocumentContent input) throws ConvertorException, IOException {
		DocumentContent result = null;
		checkSource(input);

		File output = temporaryHelper.createOutputFile();
		try {

			DocumentContent ghost = null;
			if (pdfConverterConfiguration.isGhostscriptEnabled()) {
				runGhost4j(getSourceFile(input), output);
				ghost = new DocumentContent(input.getFileName(), input.getContentType(), output);
			} else {
				ghost = input;
			}

			File preflightOutput = preflightPDF2PDFA(ghost);

			result = new DocumentContent(input.getFileName(), input.getContentType(), preflightOutput);
		} catch (Exception e) {
			throw new ConvertorException("Failed to convert to pdfA", e);
		}

		return result;
	}

	protected void runGhost4j(File input, File output) throws GhostscriptException {
		Ghostscript gs = Ghostscript.getInstance();
		ByteArrayOutputStream baosErr = new ByteArrayOutputStream();
		ByteArrayOutputStream baosOut = new ByteArrayOutputStream();
		gs.setStdErr(baosErr);
		gs.setStdOut(baosOut);
		try {
			String[] gsArgs = createGhostscriptArgs(output);
			gs.initialize(gsArgs);
			gs.runFile(input.getAbsolutePath());
			gs.exit();
		} catch (Exception e) {
			log.warn("Error:" + baosErr.toString());
			log.warn("Output:" + baosOut.toString());
			throw e;
		}
	}

	protected String protectString(String input) {
		if (input.startsWith("\"")) {
			return input.trim();
		} else {
			return "\"" + input.trim() + "\"";
		}
	}

	protected String[] createGhostscriptArgs(File output) {
		List<String> args = new ArrayList<>();
		String[] g = pdfConverterConfiguration.getGhostscriptArgs().split(" ");
		args.addAll(List.of(g));
		args.add("-dBATCH");
		args.add("-dQUIET");
		args.add("-dNOPAUSE");
		args.add("-dSAFER");
		args.add("-sOutputFile=" + output.getAbsolutePath());

		return args.toArray(String[]::new);
	}

	protected File preflightPDF2PDFA(DocumentContent input)
			throws IOException, BadFieldValueException, TransformerException {
		File output = temporaryHelper.createOutputFile();

		PDDocument inputDoc = PDDocument.load(getSourceFile(input));
		PDDocument outputDoc = new PDDocument();
		outputDoc.getDocument().setVersion(inputDoc.getDocument().getVersion());
		outputDoc.setDocumentInformation(inputDoc.getDocumentInformation());
		outputDoc.getDocumentCatalog().setViewerPreferences(inputDoc.getDocumentCatalog().getViewerPreferences());

		// ajout d'un schéma de couleur
		addICC(outputDoc);
		// ajout des métadonnées PDFA
		addMetadata(outputDoc, input.getFileName());

		// copie des pages
		PDFCloneUtility cloner = new PDFCloneUtility(outputDoc);
		PDPageTree pages = inputDoc.getDocumentCatalog().getPages();
		for (PDPage inputPage : pages) {

			COSDictionary outputPageDictionnary = (COSDictionary) cloner.cloneForNewDocument(inputPage);
			PDPage outputPage = new PDPage(outputPageDictionnary);
			outputPage.setActions(null);
			outputPage.setAnnotations(null);
			outputDoc.addPage(outputPage);
		}

		removeActions(outputDoc);
		outputDoc.save(output);
		outputDoc.close();

		return output;
	}

	protected PDFont loadFont(PDDocument document, String fontPath) throws IOException {
		InputStream fontStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fontPath);
		return PDTrueTypeFont.load(document, fontStream, Encoding.getInstance(COSName.WIN_ANSI_ENCODING));
	}

	protected void removeActions(PDDocument document) {
		document.getDocumentCatalog().setOpenAction(null);
	}

	protected void addICC(PDDocument document) throws IOException {
		InputStream colorProfile = Thread.currentThread().getContextClassLoader().getResourceAsStream("icm/sRGB.icc");
		PDOutputIntent oi = new PDOutputIntent(document, colorProfile);
		oi.setInfo(S_RGB_IEC61966_2_1);
		oi.setOutputCondition(S_RGB_IEC61966_2_1);
		oi.setOutputConditionIdentifier(S_RGB_IEC61966_2_1);
		oi.setRegistryName("http://www.color.org");

		document.getDocumentCatalog().addOutputIntent(oi);
	}

	protected void addMetadata(PDDocument document, String title)
			throws BadFieldValueException, TransformerException, IOException {
		Calendar c = Calendar.getInstance();
		document.getDocumentInformation().setAuthor(RUDI_CREATOR);
		document.getDocumentInformation().setProducer(RUDI_CREATOR);
		document.getDocumentInformation().setCreationDate(c);
		document.getDocumentInformation().setModificationDate(c);
		// add XMP metadata
		XMPMetadata xmp = XMPMetadata.createXMPMetadata();

		AdobePDFSchema as = xmp.createAndAddAdobePDFSchema();
		as.setProducer(RUDI_CREATOR);
		XMPBasicSchema bs = xmp.createAndAddXMPBasicSchema();
		bs.setCreateDate(c);
		bs.setModifyDate(c);

		xmp.createAndAddPDFAExtensionSchemaWithDefaultNS();

		DublinCoreSchema dc = xmp.createAndAddDublinCoreSchema();
		dc.setTitle(title);
		dc.addCreator(RUDI_CREATOR);

		PDFAIdentificationSchema id = xmp.createAndAddPDFAIdentificationSchema();
		id.setPart(1);
		id.setConformance("B");

		XmpSerializer serializer = new XmpSerializer();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializer.serialize(xmp, baos, true);

		PDMetadata metadata = new PDMetadata(document);
		metadata.importXMPMetadata(baos.toByteArray());
		document.getDocumentCatalog().setMetadata(metadata);
	}

	@Override
	public org.rudi.facet.generator.pdf.model.ValidationResult validatePDFA(DocumentContent input)
			throws ValidationException, IOException {
		org.rudi.facet.generator.pdf.model.ValidationResult result = new org.rudi.facet.generator.pdf.model.ValidationResult();
		PreflightParser parser = new PreflightParser(input.getFile());
		parser.parse();
		try (PreflightDocument document = parser.getPreflightDocument()) {
			document.validate();
			ValidationResult internalResult = document.getResult();
			result.setValid(internalResult.isValid());
			if (CollectionUtils.isNotEmpty(internalResult.getErrorsList())) {
				for (ValidationError error : internalResult.getErrorsList()) {
					result.addItem(ValidationResultItem.builder().cause(error.getCause())
							.throwable(error.getThrowable()).details(error.getDetails()).errorCode(error.getErrorCode())
							.pageNumber(error.getPageNumber()).isWarning(error.isWarning()).build());
				}
			}
		} catch (Exception e) {
			throw new ValidationException("Failed to validate document", e);
		}
		return result;
	}

}
