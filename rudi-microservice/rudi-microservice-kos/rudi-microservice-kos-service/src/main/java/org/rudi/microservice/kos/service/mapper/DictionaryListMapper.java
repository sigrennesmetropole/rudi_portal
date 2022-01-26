package org.rudi.microservice.kos.service.mapper;

import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.rudi.microservice.kos.core.bean.DictionaryList;
import org.rudi.microservice.kos.core.bean.Language;
import org.rudi.microservice.kos.storage.entity.skos.SkosConceptTranslationEntity;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DictionaryListMapper {

    default List<DictionaryList> skosConceptTranslationEntitiesToDictionaryLists(Set<SkosConceptTranslationEntity> skosConceptTranslationEntities) {

        if (CollectionUtils.isNotEmpty(skosConceptTranslationEntities)) {
            return skosConceptTranslationEntities
                    .stream()
                    .collect(Collectors.groupingBy(SkosConceptTranslationEntity::getLang,
                         Collectors.mapping(SkosConceptTranslationEntity::getText,
                             Collectors.toList())))
                    .entrySet().stream()
                    .map(entry -> new DictionaryList().lang(Language.fromValue(entry.getKey())).text(entry.getValue()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    default Set<SkosConceptTranslationEntity> dictionaryListsToSkosConceptTranslationEntities(List<DictionaryList> dictionaryLists) {
        if (CollectionUtils.isNotEmpty(dictionaryLists)) {
            Set<SkosConceptTranslationEntity> skosConceptTranslationEntities = new HashSet<>();
            for (DictionaryList dictionaryList: dictionaryLists) {
                dictionaryList.getText().forEach(text -> {
                    SkosConceptTranslationEntity skosConceptTranslationEntity = new SkosConceptTranslationEntity();
                    skosConceptTranslationEntity.setLang(dictionaryList.getLang().getValue());
                    skosConceptTranslationEntity.setText(text);
                    skosConceptTranslationEntity.setUuid(UUID.randomUUID());
                    skosConceptTranslationEntities.add(skosConceptTranslationEntity);
                });
            }
            return skosConceptTranslationEntities;
        }
        return Collections.emptySet();
    }
}
