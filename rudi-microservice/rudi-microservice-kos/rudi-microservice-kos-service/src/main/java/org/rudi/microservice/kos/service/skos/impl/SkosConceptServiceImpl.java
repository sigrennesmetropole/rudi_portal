package org.rudi.microservice.kos.service.skos.impl;

import java.util.Collections;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.microservice.kos.core.bean.Language;
import org.rudi.microservice.kos.core.bean.SimpleSkosConcept;
import org.rudi.microservice.kos.core.bean.SkosConceptLabel;
import org.rudi.microservice.kos.core.bean.SkosConceptSearchCriteria;
import org.rudi.microservice.kos.service.mapper.SimpleSkosConceptMapper;
import org.rudi.microservice.kos.service.skos.SkosConceptService;
import org.rudi.microservice.kos.storage.dao.skos.SkosConceptCustomDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SkosConceptServiceImpl implements SkosConceptService {

	@Autowired
	private SkosConceptCustomDao skosConceptCustomDao;

	@Autowired
	private SimpleSkosConceptMapper simpleSkosConceptMapper;

	private static final String SKOS_CONCEPT_CRITERIA_TEXT_CODES_MISSING = "Au moins un des paramètres text, codes ou rôle doit être fourni!";

	@Override
	public Page<SimpleSkosConcept> searchSkosConcepts(SkosConceptSearchCriteria searchCriteria, Pageable pageable) {
		if (searchCriteria == null) {
			searchCriteria = SkosConceptSearchCriteria.builder().build();
		}
		if (StringUtils.isEmpty(searchCriteria.getText()) && CollectionUtils.isEmpty(searchCriteria.getCodes())
				&& CollectionUtils.isEmpty(searchCriteria.getRoles())) {
			throw new IllegalArgumentException(SKOS_CONCEPT_CRITERIA_TEXT_CODES_MISSING);
		}
		if (searchCriteria.getLang() == null) {
			searchCriteria.setLang(Language.FR);
		}
		if (CollectionUtils.isEmpty(searchCriteria.getLabels())) {
			searchCriteria.setLabels(Collections.singletonList(SkosConceptLabel.PREFERRED));
		}

		return simpleSkosConceptMapper.entitiesToDto(skosConceptCustomDao.searchSkosConcepts(searchCriteria, pageable),
				pageable);
	}

}
