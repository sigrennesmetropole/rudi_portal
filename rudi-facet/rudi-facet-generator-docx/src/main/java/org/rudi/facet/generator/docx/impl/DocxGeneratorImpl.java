/**
 * 
 */
package org.rudi.facet.generator.docx.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.common.core.DocumentContent;
import org.rudi.facet.generator.TemporaryHelper;
import org.rudi.facet.generator.docx.DocxGenerator;
import org.rudi.facet.generator.docx.model.DocxDataModel;
import org.rudi.facet.generator.docx.model.MetadataFieldNameDescription;
import org.rudi.facet.generator.exception.GenerationException;
import org.rudi.facet.generator.impl.AbstractGenerator;
import org.rudi.facet.generator.model.GenerationFormat;
import org.springframework.stereotype.Component;

import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.images.IImageProvider;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;
import lombok.extern.slf4j.Slf4j;

/**
 * https://github.com/opensagres/xdocreport.samples/blob/master/samples/fr.opensagres.xdocreport.samples.docxandvelocity/src/fr/opensagres/xdocreport/samples/docxandvelocity/DocxProjectWithVelocityAndImageList.java
 * 
 * @author FNI18300
 *
 */
@Component
@Slf4j
public class DocxGeneratorImpl extends AbstractGenerator<DocxDataModel> implements DocxGenerator {

	public DocxGeneratorImpl(TemporaryHelper temporaryHelper) {
		super(temporaryHelper);
	}

	@Override
	public DocumentContent generateDocument(DocxDataModel dataModel) throws GenerationException, IOException {
		checkInputData(dataModel);
		File generateFile = getTemporaryHelper().createOutputFile();

		DocumentContent result = null;
		try (InputStream in = openModeleFile(dataModel.getModelFileName());
				OutputStream out = new FileOutputStream(generateFile)) {
			// 0) Get data from datamodel
			Map<String, Object> datas = dataModel.getDataModel();

			// 1) Load Docx file by filling Freemarker template engine and cache
			// it to the registry
			IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Freemarker);

			// 2) Create fields metadata to manage dynamic image
			createImageMetadataFields(report, datas);

			// 2b) Create field metadata
			createOtherMetadataFields(report, dataModel.getFieldMetadataNames());

			// 3) Create context Java model
			IContext context = report.createContext();
			context.putMap(datas);

			// 4) Generate report by merging Java model with the Docx
			report.process(context, out);

			result = new DocumentContent(dataModel.getOutputFileName(), dataModel.getFormat().getMimeType(),
					generateFile);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new GenerationException("Failed to generate document", e);
		}

		return result;
	}

	private void createOtherMetadataFields(IXDocReport report, List<MetadataFieldNameDescription> fieldMetadataNames) {
		if (CollectionUtils.isNotEmpty(fieldMetadataNames)) {
			FieldsMetadata metadata = report.createFieldsMetadata();
			for (MetadataFieldNameDescription fieldMetadataName : fieldMetadataNames) {
				try {
					metadata.load(fieldMetadataName.getName(), fieldMetadataName.getType(),
							fieldMetadataName.isMultiple());
				} catch (Exception e) {
					log.warn("Failed to create metadatafields {}", fieldMetadataName);
				}
			}
		}
	}

	private void createImageMetadataFields(IXDocReport report, Map<String, Object> datas) {
		if (MapUtils.isNotEmpty(datas)) {
			FieldsMetadata metadata = report.createFieldsMetadata();
			Set<String> keys = datas.keySet();
			for (String key : keys) {
				Object value = datas.get(key);
				if (isFieldImage(value)) {
					metadata.addFieldAsImage(key);
				} else if (value instanceof Collection) {
					createImageMetadataCollectionFields(metadata, key, value);
				}
			}
		}
	}

	private void createImageMetadataCollectionFields(FieldsMetadata metadata, String key, Object value) {
		@SuppressWarnings("unchecked")
		Iterator<Object> it = ((Collection<Object>) value).iterator();
		while (it.hasNext()) {
			if (isFieldImage(it.next())) {
				metadata.addFieldAsImage(key);
				break;
			}
		}
	}

	protected boolean isFieldImage(Object value) {
		return value instanceof IImageProvider;
	}

	private void checkInputData(DocxDataModel dataModel) {
		if (!dataModel.getFormat().equals(GenerationFormat.DOCX)) {
			throw new IllegalArgumentException("Unsupported format " + dataModel.getFormat());
		}
		if (StringUtils.isEmpty(dataModel.getModelFileName())) {
			throw new IllegalArgumentException("Model file is required " + dataModel.getModelFileName());
		}
	}

	private InputStream openModeleFile(String modelFileName) throws FileNotFoundException {
		File f = new File(modelFileName);
		if (f.exists() && f.isFile()) {
			return new FileInputStream(f);
		} else {
			return Thread.currentThread().getContextClassLoader().getResourceAsStream(modelFileName);
		}
	}

}
