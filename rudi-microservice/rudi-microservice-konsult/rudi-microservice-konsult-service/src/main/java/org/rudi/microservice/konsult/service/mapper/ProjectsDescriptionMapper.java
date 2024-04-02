package org.rudi.microservice.konsult.service.mapper;

import java.util.Locale;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.rudi.microservice.konsult.core.bean.ProjectsDescription;
import org.rudi.microservice.konsult.core.customization.ProjectsDescriptionData;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MultilingualTextMapper.class })
public interface ProjectsDescriptionMapper {

	@Mapping(source = "titles1", target = "title1" )
	@Mapping(source = "titles2", target = "title2" )
	@Mapping(source = "subtitles", target = "subtitle" )
	@Mapping(source = "descriptions", target = "description" )
	ProjectsDescription dataToDto(ProjectsDescriptionData data, @Context Locale locale);

}
