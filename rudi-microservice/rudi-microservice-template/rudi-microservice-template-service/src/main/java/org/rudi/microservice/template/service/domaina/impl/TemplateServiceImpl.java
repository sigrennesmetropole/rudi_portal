package org.rudi.microservice.template.service.domaina.impl;

import org.apache.commons.lang3.StringUtils;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.microservice.template.core.bean.Template;
import org.rudi.microservice.template.service.domaina.TemplateService;
import org.rudi.microservice.template.service.mapper.TemplateMapper;
import org.rudi.microservice.template.storage.dao.domaina.TemplateDao;
import org.rudi.microservice.template.storage.entity.domaina.TemplateEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * @author FNI18300
 *
 */
@Service
@Transactional(readOnly = true)
public class TemplateServiceImpl implements TemplateService {

	@Autowired
	private UtilContextHelper utilContextHelper;

	@Autowired
	private TemplateDao templateDao;

	@Autowired
	private TemplateMapper templateMapper;

	@Override
	public List<Template> getTemplates() {
		return templateMapper.entitiesToDto(templateDao.findAll(Sort.by("id")));
	}

	@Override
	@Transactional // readOnly = false
	public Template createTemplate(Template template) {
		final var entity = templateMapper.dtoToEntity(template);
		entity.setUuid(UUID.randomUUID());
		validEntity(entity);
		final var savedEntity = templateDao.save(entity);
		return templateMapper.entityToDto(savedEntity);
	}

	@Override
	@Transactional // readOnly = false
	public Template updateTemplate(Template template) {
		if (template.getUuid() == null) {
			throw new IllegalArgumentException("UUID manquant");
		}
		TemplateEntity entity = templateDao.findByUuid(template.getUuid());
		if (entity == null) {
			throw new IllegalArgumentException("Resource inexistante:" + template.getUuid());
		}
		templateMapper.dtoToEntity(template, entity);
		validEntity(entity);
		templateDao.save(entity);
		return templateMapper.entityToDto(entity);
	}

	@Override
	@Transactional // readOnly = false
	public void deleteTemplate(UUID uuid) {
		TemplateEntity entity = templateDao.findByUuid(uuid);
		if (entity == null) {
			throw new IllegalArgumentException("Resource inexistante:" + uuid);
		}
		templateDao.delete(entity);
	}

	private void validEntity(TemplateEntity entity) {
		if (StringUtils.isEmpty(entity.getComment())) {
			throw new IllegalArgumentException("Invalid empty comment:" + entity);
		}
	}
}
