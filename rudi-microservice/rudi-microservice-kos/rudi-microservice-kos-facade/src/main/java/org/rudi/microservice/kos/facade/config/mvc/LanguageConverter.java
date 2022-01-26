package org.rudi.microservice.kos.facade.config.mvc;

import org.rudi.microservice.kos.core.bean.Language;
import org.springframework.core.convert.converter.Converter;

import javax.annotation.Nonnull;

public class LanguageConverter implements Converter<String, Language> {
    @Override
    public Language convert(@Nonnull String s) {
        return Language.fromValue(s);
    }
}
