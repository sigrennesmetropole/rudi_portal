package org.rudi.microservice.selfdata.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.selfdata.core.bean.SelfdataTokenTuple;
import org.rudi.microservice.selfdata.storage.entity.selfdatainformationrequest.SelfdataTokenTupleEntity;

/**
 * @author FNI18300
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class })
public interface SelfdataTokenTupleMapper extends AbstractMapper<SelfdataTokenTupleEntity, SelfdataTokenTuple> {
}