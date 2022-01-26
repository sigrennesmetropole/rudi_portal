package org.rudi.microservice.kos.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.kos.core.bean.SimpleSkosConcept;
import org.rudi.microservice.kos.core.bean.SimpleSkosConceptProjection;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class })
public interface SimpleSkosConceptMapper extends AbstractMapper<SimpleSkosConceptProjection, SimpleSkosConcept> {
}
