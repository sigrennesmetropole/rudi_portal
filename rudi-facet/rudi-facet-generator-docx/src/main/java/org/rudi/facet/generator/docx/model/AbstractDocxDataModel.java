/**
 * 
 */
package org.rudi.facet.generator.docx.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.rudi.facet.generator.model.GenerationFormat;
import org.rudi.facet.generator.model.impl.AbstractDataModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.opensagres.xdocreport.document.images.ByteArrayImageProvider;
import fr.opensagres.xdocreport.document.images.FileImageProvider;
import fr.opensagres.xdocreport.document.images.IImageProvider;
import lombok.Getter;
import lombok.Setter;

/**
 * Data model pour les données injectées dans les modèles de documents
 * 
 * @author FNI18300
 *
 */
public abstract class AbstractDocxDataModel extends AbstractDataModel implements DocxDataModel {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDocxDataModel.class);

	@Getter
	@Setter
	private String modelFileName;

	/**
	 * Constructeur pour DataModel
	 * 
	 * @param format
	 */
	protected AbstractDocxDataModel(GenerationFormat format) {
		this(format, null);
	}

	protected AbstractDocxDataModel(GenerationFormat format, String modelFileName) {
		super(format);
		this.modelFileName = modelFileName;
	}

	/**
	 * Ajout d'une propriété image à partir d'un information en base64
	 * 
	 * @param datas
	 * @param key
	 * @param encodedImage
	 * @param useImageSize
	 */
	protected void addB64Image(Map<String, Object> datas, String key, String encodedImage, boolean useImageSize) {
		try {
			byte[] decodedBytes = Base64.getDecoder().decode(encodedImage);
			IImageProvider mainMap = new ByteArrayImageProvider(decodedBytes, useImageSize);
			datas.put(key, mainMap);
		} catch (Exception e) {
			LOGGER.warn("Failed to inject image byte array {}", key);
		}
	}

	/**
	 * Ajout d'une propriété image
	 * 
	 * @param datas
	 * @param key
	 * @param file
	 * @param useImageSize
	 */
	protected void addFileImage(Map<String, Object> datas, String key, File file, boolean useImageSize) {
		IImageProvider mainMap = new FileImageProvider(file, useImageSize);
		datas.put(key, mainMap);
	}

	@Override
	public List<MetadataFieldNameDescription> getFieldMetadataNames() {
		return new ArrayList<>();
	}

	/**
	 * format an enum value as a string litteral
	 * 
	 * @param e
	 * @return
	 */
	public String formatEnum(Enum<?> e) {
		if (e != null) {
			return e.name();
		} else {
			return "";
		}
	}
}
