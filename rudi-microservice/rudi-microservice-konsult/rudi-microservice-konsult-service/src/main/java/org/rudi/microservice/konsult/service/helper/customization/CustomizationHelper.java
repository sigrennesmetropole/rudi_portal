/**
 * RUDI Portail
 */
package org.rudi.microservice.konsult.service.helper.customization;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.ehcache.Cache;
import org.rudi.common.core.DocumentContent;
import org.rudi.common.core.resources.ResourcesHelper;
import org.rudi.microservice.konsult.core.customization.CustomizationDescriptionData;
import org.rudi.microservice.konsult.core.customization.KeyFigureData;
import org.rudi.microservice.konsult.service.customization.KeyFigureComputer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import static org.rudi.microservice.konsult.service.constant.BeanIds.CUSTOMIZATION_RESOURCES_CACHE;

/**
 * @author FNI18300
 *
 */
@Component
@Slf4j
public class CustomizationHelper extends ResourcesHelper {

	@Getter(AccessLevel.PROTECTED)
	@Value("${customization.base-package:customization}")
	private String basePackage;

	@Getter(AccessLevel.PROTECTED)
	@Value("${customization.base-directory:}")
	private String baseDirectory;

	@Getter(AccessLevel.PROTECTED)
	private final Cache<String, DocumentContent> cache;

	@Value("${customization.filename:customization.json}")
	private String customizationFilename;

	private final ObjectMapper objectMapper;

	private final List<KeyFigureComputer> keyFigureComputers;

	private CustomizationDescriptionData customizationDescriptionData = null;

	CustomizationHelper(@Qualifier(CUSTOMIZATION_RESOURCES_CACHE) Cache<String, DocumentContent> cache, List<KeyFigureComputer> keyFigureComputers, ObjectMapper objectMapper){
		this.cache = cache;
		this.keyFigureComputers = keyFigureComputers;
		this.objectMapper = objectMapper;
	}

	protected CustomizationDescriptionData loadCustomizationDescription()
			throws IOException {
		CustomizationDescriptionData result = null;
		File f = new File(baseDirectory, customizationFilename);
		if (f.exists() && f.isFile()) {
			try (JsonParser p = objectMapper.createParser(f)) {
				result = p.readValueAs(CustomizationDescriptionData.class);
			}
		} else {
			try (JsonParser p = objectMapper.createParser(Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(basePackage + "/" + customizationFilename))) {
				result = p.readValueAs(CustomizationDescriptionData.class);
			}
		}

		fillKeyFiguresData(result);

		return fillResourceMappingAndReplaceData(result);
	}

	/**
	 * Remplit le resourceMapping avec les valeurs receuillis dans le JSON et les remplace dans l'objet de retour par des UUIDs ou des identifiants.
	 *
	 *
	 * @param data données issues de la lecture du fichier .json
	 * @return ces mêmes data, en ayant remplacé les chemins vers les fichiers par des UUIDs
	 */
	private CustomizationDescriptionData fillResourceMappingAndReplaceData(CustomizationDescriptionData data) {
		data.setOverrideCssFile(fillResourceMapping(data.getOverrideCssFile(), "overrideCssFile.css"));

		data.setMainLogo(fillResourceMapping(data.getMainLogo(), UUID.randomUUID().toString()));

		data.getHeroDescription().setLeftImage(fillResourceMapping(data.getHeroDescription().getLeftImage(), UUID.randomUUID().toString()));

		data.getHeroDescription().setRightImage(fillResourceMapping(data.getHeroDescription().getRightImage(), UUID.randomUUID().toString()));

		data.getKeyFiguresDescription().setKeyFiguresLogo(fillResourceMapping(data.getKeyFiguresDescription().getKeyFiguresLogo(), UUID.randomUUID().toString()));

		return data;
	}

	public CustomizationDescriptionData getCustomizationDescriptionData() {
		if (customizationDescriptionData == null) {
			try {
				customizationDescriptionData = loadCustomizationDescription();
			} catch (Exception e) {
				log.error("Failed to load customization", e);
			}
		}
		return customizationDescriptionData;
	}


	private void fillKeyFiguresData(CustomizationDescriptionData data){
		if (data.getKeyFiguresDescription() != null
				&& CollectionUtils.isNotEmpty(data.getKeyFiguresDescription().getKeyFigures())) {

			// les chiffres clé figurent dans le template, on charge les valeurs demandées
			for (KeyFigureData keyFigure : data.getKeyFiguresDescription().getKeyFigures()) {

				// Parcours de la liste des KeyFigureComputer pour trouver celui qui correspond au KeyFigure
				for (KeyFigureComputer computer: keyFigureComputers) {
					if(computer.accept(keyFigure.getType())){
						computer.compute(keyFigure);
					}
				}
			}
		}
	}

}
