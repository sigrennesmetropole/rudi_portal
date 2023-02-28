package org.rudi.facet.generator.pdf.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PDFAComponents {

	private String inputFilePath;
	private String embedFilePath;
	private String colorProfilePath;
	private String outputFilePath;
	private String documentType;
	private String documentFileName;
	private String documentVersion;
	private String xmpTemplatePath;

	/**
	 * * Use for set file path (input and output)
	 * 
	 * @param inputFilePath    : path of input PDF file, e.g. Test.pdf, invoice.pdf
	 * @param embedFilePath    : path of attached file, e.g. example.xml
	 * @param colorProfilePath : path of color profile file, e.g. sRGB Color Space Profile.icm
	 * @param outputFilePath   : name of output PDF/A-3 file, e.g. invoiceA3.pdf
	 * @param documentType     : document type of PDF ,e.g. "Tax Invoice", "Credit Note", "Debit Note"
	 * @param documentFileName : name of embed file name
	 * @param documentVersion  : document version
	 */
	public PDFAComponents(String inputFilePath, String embedFilePath, String colorProfilePath, String outputFilePath,
			String documentType, String documentFileName, String documentVersion, String xmpTemplatePath) {
		super();
		this.inputFilePath = inputFilePath;
		this.outputFilePath = outputFilePath;
		this.embedFilePath = embedFilePath;
		this.colorProfilePath = colorProfilePath;
		this.documentType = documentType;
		this.documentFileName = documentFileName;
		this.documentVersion = documentVersion;
		this.xmpTemplatePath = xmpTemplatePath;
	}
}
