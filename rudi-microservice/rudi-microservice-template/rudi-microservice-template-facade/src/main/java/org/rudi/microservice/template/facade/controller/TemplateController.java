package org.rudi.microservice.template.facade.controller;

import java.util.List;

import org.rudi.microservice.template.core.bean.Template;
import org.rudi.microservice.template.facade.controller.api.TemplatesApi;
import org.rudi.microservice.template.service.domaina.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TemplateController implements TemplatesApi {

	@Autowired
	private TemplateService templateService;

	@Override
	public ResponseEntity<List<Template>> getTemplates() {
		return ResponseEntity.ok(templateService.getTemplates());
	}

}
