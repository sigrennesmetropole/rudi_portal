/**
 * 
 */
package org.rudi.facet.generator.docx.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.rudi.common.core.DocumentContent;
import org.rudi.facet.generator.TemporaryHelper;
import org.rudi.facet.generator.docx.DocxMerger;
import org.rudi.facet.generator.exception.GenerationException;
import org.rudi.facet.generator.model.GenerationFormat;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author FNI18300
 *
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DocxMergerImpl implements DocxMerger {

	@Getter(value = AccessLevel.PROTECTED)
	private final TemporaryHelper temporaryHelper;

	@Override
	public DocumentContent merge(String filename, List<DocumentContent> documents)
			throws GenerationException, IOException {
		List<DocumentContent> filteredDocuments = documents.stream()
				.filter(item -> GenerationFormat.DOCX.getMimeType().equalsIgnoreCase(item.getContentType()))
				.collect(Collectors.toList());
		if (CollectionUtils.isEmpty(filteredDocuments)) {
			log.info("Empty list of documents to merge {}", documents);
			return null;
		} else if (filteredDocuments.size() == 1) {
			log.info("One document to merge {}", documents);
			DocumentContent item = filteredDocuments.get(0);
			return new DocumentContent(GenerationFormat.DOCX.generateFileName(filename),
					GenerationFormat.DOCX.getMimeType(), item.getFile());
		} else {
			return internalMerge(filename, filteredDocuments);
		}
	}

	private DocumentContent internalMerge(String filename, List<DocumentContent> documents)
			throws IOException, GenerationException {
		File generateFile = getTemporaryHelper().createOutputFile();
		DocumentContent result = null;
		// ouverture du flux de sortie et du premier document
		try (OutputStream out = new FileOutputStream(generateFile);
				InputStream is = new FileInputStream(documents.get(0).getFile());
				XWPFDocument doc = new XWPFDocument(is);) {
			log.info("Handle merge 0 of {}", documents.get(0));
			CTBody body = doc.getDocument().getBody();
			// parcourt des n autres doucment
			for (int i = 1; i < documents.size(); i++) {
				DocumentContent document = documents.get(i);
				log.info("Handle merge {} of {}", i, document);
				appendDocument(body, document);
			}
			doc.write(out);

			result = new DocumentContent(GenerationFormat.DOCX.generateFileName(filename),
					GenerationFormat.DOCX.getMimeType(), generateFile);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new GenerationException("Failed to merges documents", e);
		}

		return result;
	}

	private void appendDocument(CTBody body, DocumentContent document) throws GenerationException {
		try (InputStream itemStream = new FileInputStream(document.getFile());
				XWPFDocument itemDocument = new XWPFDocument(itemStream);) {

			CTBody itemBody = itemDocument.getDocument().getBody();
			appendBody(body, itemBody);

		} catch (Exception e) {
			log.warn("Skip file {}/{}", document.getFileName(), document.getFile());
		}
	}

	private void appendBody(CTBody src, CTBody append) throws XmlException {
		XmlOptions optionsOuter = new XmlOptions();
		optionsOuter.setSaveOuter();
		String appendString = append.xmlText(optionsOuter);
		String srcString = src.xmlText();
		String prefix = srcString.substring(0, srcString.indexOf('>') + 1);
		String mainPart = srcString.substring(srcString.indexOf('>') + 1, srcString.lastIndexOf('<'));
		String suffix = srcString.substring(srcString.lastIndexOf('<'));
		String addPart = appendString.substring(appendString.indexOf(">") + 1, appendString.lastIndexOf('<'));
		CTBody makeBody = CTBody.Factory.parse(prefix + mainPart + addPart + suffix);

		src.set(makeBody);
	}

}
