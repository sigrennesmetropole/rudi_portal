package org.rudi.facet.bpmn.mapper.workflow;

import java.util.Collection;
import java.util.List;

import org.activiti.engine.history.HistoricActivityInstance;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.rudi.bpmn.core.bean.HistoricInformation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 * Exceptionnellement, ce mapper n'utilise pas la classe abstraite car on souhaite utilise seulement la conversion entité-> dto et les entitées
 * passées sont des interface
 * 
 * @author FNI18300
 *
 *
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HistoricInformationMapper {

	/**
	 * @param entity entity to transform to dto
	 * @return dto
	 */
	HistoricInformation entityToDto(HistoricActivityInstance entity);

	/**
	 * @param entities
	 * @return la liste de dtos convertis
	 */
	List<HistoricInformation> entitiesToDto(Collection<HistoricActivityInstance> entities);

	default Page<HistoricInformation> entitiesToDto(Page<HistoricActivityInstance> entities, Pageable pageable) {
		return new PageImpl<>(entitiesToDto(entities.getContent()), pageable, entities.getTotalElements());
	}

}
