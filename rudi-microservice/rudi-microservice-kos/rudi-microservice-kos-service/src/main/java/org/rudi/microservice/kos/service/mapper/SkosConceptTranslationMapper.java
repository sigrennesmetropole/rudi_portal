package org.rudi.microservice.kos.service.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.kos.core.bean.DictionaryEntry;
import org.rudi.microservice.kos.core.bean.Language;
import org.rudi.microservice.kos.storage.entity.skos.SkosConceptTranslationEntity;
import org.rudi.microservice.kos.storage.entity.skos.SkosSchemeTranslationEntity;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class })
public interface SkosConceptTranslationMapper extends AbstractMapper<SkosConceptTranslationEntity, DictionaryEntry> {

    @Override
    @InheritInverseConfiguration
    SkosConceptTranslationEntity dtoToEntity(DictionaryEntry dto);

    @Override
    @Mapping(target = "lang", ignore = true, source = "lang")
    DictionaryEntry entityToDto(SkosConceptTranslationEntity entity);

    @Override
    @InheritConfiguration
    void dtoToEntity(DictionaryEntry dto, @MappingTarget SkosConceptTranslationEntity entity);

    @AfterMapping
    default void updateSkosConceptTranslationEntityUuid(DictionaryEntry dto, @MappingTarget SkosConceptTranslationEntity entity) {
        if ( dto.getLang() != null ) {
            entity.setLang( dto.getLang().getValue() );
        }
        entity.setUuid(UUID.randomUUID());
    }

    @AfterMapping
    default void updateDictionaryEntry(SkosConceptTranslationEntity entity, @MappingTarget  DictionaryEntry dto) {
        if ( entity.getLang() != null ) {
            dto.setLang(Language.fromValue(entity.getLang()));
        }
    }

}
