/**
 * 
 */
package org.rudi.facet.bpmn.helper.form;

import java.io.IOException;

import org.rudi.facet.bpmn.bean.form.FormDefinition;
import org.rudi.facet.bpmn.exception.FormDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

import net.minidev.json.parser.ParseException;

/**
 * Helper pour la sérialisation des définitions de formulaire
 * 
 * @author FNI18300
 *
 */
@Component
public class FormDefinitionHelper {

	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * Parse une définition de formulaire
	 * 
	 * @param formDefinition
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public FormDefinition hydrateForm(String formDefinition) throws FormDefinitionException {
		ObjectReader objectReader = objectMapper.readerFor(FormDefinition.class);
		try {
			return objectReader.readValue(formDefinition);
		} catch (IOException e) {
			throw new FormDefinitionException("Failed to hydrate:" + formDefinition, e);
		}
	}

	/**
	 * Serialize une définition de formulaire
	 * 
	 * @param form
	 * @return
	 * @throws IOException
	 */
	public String deshydrateForm(FormDefinition form) throws FormDefinitionException {
		ObjectWriter objectWriter = objectMapper.writer();
		try {
			return objectWriter.writeValueAsString(form);
		} catch (JsonProcessingException e) {
			throw new FormDefinitionException("Failed to deshydrate:" + form, e);
		}
	}

}
