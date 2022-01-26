/**
 * 
 */
package org.rudi.microservice.template.service.domaina;

import java.util.List;
import java.util.UUID;

import org.rudi.microservice.template.core.bean.Template;

/**
 * @author FNI18300
 *
 */
public interface TemplateService {

	/**
	 * List all template
	 * 
	 * @param active
	 * @return template list
	 */
	List<Template> getTemplates();

	/**
	 * Create a template
	 * 
	 * @param template
	 * @return
	 */
	Template createTemplate(Template template);

	/**
	 * Update a template entity
	 * 
	 * @param template
	 * @return
	 */
	Template updateTemplate(Template template);

	/**
	 * Delete a template entity
	 * 
	 * @param uuid
	 */
	void deleteTemplate(UUID uuid);

}
