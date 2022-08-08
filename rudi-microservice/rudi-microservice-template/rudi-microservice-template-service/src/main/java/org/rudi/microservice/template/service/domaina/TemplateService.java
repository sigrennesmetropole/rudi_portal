package org.rudi.microservice.template.service.domaina;

import org.rudi.microservice.template.core.bean.Template;

import java.util.List;
import java.util.UUID;

/**
 * @author FNI18300
 *
 */
public interface TemplateService {

	/**
	 * List all template
	 * 
	 * @return template list
	 */
	List<Template> getTemplates();

	/**
	 * Create a template
	 */
	Template createTemplate(Template template);

	/**
	 * Update a template entity
	 */
	Template updateTemplate(Template template);

	/**
	 * Delete a template entity
	 */
	void deleteTemplate(UUID uuid);

}
