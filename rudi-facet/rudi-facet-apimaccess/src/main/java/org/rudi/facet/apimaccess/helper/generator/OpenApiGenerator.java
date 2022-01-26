package org.rudi.facet.apimaccess.helper.generator;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import org.rudi.facet.apimaccess.bean.APIDescription;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OpenApiGenerator {

	private final OpenApiTemplates openApiTemplates;

	public String generate(APIDescription apiDescription, String apiContext) throws APIManagerException {
		final Template template = getTemplate(apiDescription.getInterfaceContract());

		Map<String, Object> templateData = new HashMap<>();
		templateData.put("api_context", apiContext);
		templateData.put("api", apiDescription);

		return generateTemplate(template, templateData);
	}

	private Template getTemplate(String interfaceContract) throws APIManagerException {
		try {
			final Template template = openApiTemplates.findByInterfaceContract(interfaceContract);
			if (template == null) {
				final String message = String.format("Template for interfaceContract \"%s\" not found.", interfaceContract);
				throw new APIManagerException(message);
			}
			return template;
		} catch (IOException e) {
			final String message = String.format("Cannot retrieve template for interfaceContract \"%s\".", interfaceContract);
			throw new APIManagerException(message, e);
		}
	}

	@Nonnull
	private String generateTemplate(Template template, Map<String, Object> templateData) throws APIManagerException {
		try (StringWriter out = new StringWriter()) {

			template.process(templateData, out);
			String result = out.getBuffer().toString();
			out.flush();

			return result;
		} catch (TemplateException | IOException e) {
			throw new APIManagerException("Open API specification generation failed", e);
		}
	}

}
