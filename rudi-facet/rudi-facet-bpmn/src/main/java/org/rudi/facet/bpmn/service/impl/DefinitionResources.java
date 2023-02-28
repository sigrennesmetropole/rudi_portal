package org.rudi.facet.bpmn.service.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.rudi.bpmn.core.bean.SectionDefinition;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.common.service.helper.ResourceHelper;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
class DefinitionResources {
	private static final String FORM_DEFINITION_FILE_NAME_SEPARATOR = "__";

	private final ResourceHelper resourceHelper;
	private final JsonResourceReader jsonResourceReader;

	private Map<String, FormManualDefinition> forms;
	private Collection<SectionDefinition> sections;

	/**
	 * @return null en cas d'erreur
	 */
	@Nullable
	private Map.Entry<String, FormManualDefinition> readFormManualDefinitionEntry(Resource jsonResource) {
		final var filename = jsonResource.getFilename();
		if (filename != null) {
			try {
				final var formManualDefinition = jsonResourceReader.read(jsonResource, FormManualDefinition.class);
				final var formName = FilenameUtils.removeExtension(filename);
				return Map.entry(formName, formManualDefinition);
			} catch (IOException e) {
				log.error("Failed to read process form definition from resource : " + filename, e);
			}
		}
		log.error("Cannot read process form definition, because resource has no filename : " + jsonResource);
		return null;
	}

	@Nonnull
	private String getJsonForSectionDefinition(String filename) throws IOException {
		final var resource = resourceHelper.getResourceFromAdditionalLocationOrFromClasspath(filename);
		if (!resource.exists()) {
			throw new FileNotFoundException("JSON resource for section definition does not exist : " + filename);
		}
		return IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
	}

	Map<String, FormManualDefinition> getForms() throws IOException {
		if (forms == null) {
			forms = loadForms();
		}
		return forms;
	}

	private Map<String, FormManualDefinition> loadForms() throws IOException {
		final Resource[] jsonResources = resourceHelper.getResourcesFromAdditionalLocationOrFromClasspath("bpmn/forms/*.json");

		return Arrays.stream(jsonResources)
				.map(this::readFormManualDefinitionEntry)
				.filter(Objects::nonNull)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	Collection<SectionDefinition> getSections() throws IOException {
		if (sections == null) {
			sections = loadSections();
		}
		return sections;
	}

	private Collection<SectionDefinition> loadSections() throws IOException {
		final var referencedSectionNames = getForms().values().stream()
				.flatMap(form -> form.getSections().stream())
				.map(SectionReference::getName)
				.collect(Collectors.toSet());

		final Collection<SectionDefinition> sectionsByName = new ArrayList<>(referencedSectionNames.size());
		for (final var referencedSectionName : referencedSectionNames) {
			final var jsonForSectionDefinition = getJsonForSectionDefinition("bpmn/sections/" + referencedSectionName + ".json");
			final var sectionManualDefinition = jsonResourceReader.getObjectMapper().readValue(jsonForSectionDefinition, SectionManualDefinition.class);
			final var sectionDefinition = new SectionDefinition()
					.name(referencedSectionName)
					.label(sectionManualDefinition.getLabel())
					.help(sectionManualDefinition.getHelp())
					.definition(jsonForSectionDefinition);
			sectionsByName.add(sectionDefinition);
		}

		return sectionsByName;
	}
}
