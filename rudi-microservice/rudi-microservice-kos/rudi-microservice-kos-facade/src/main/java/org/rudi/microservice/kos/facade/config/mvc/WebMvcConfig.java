package org.rudi.microservice.kos.facade.config.mvc;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Nonnull;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(@Nonnull FormatterRegistry registry) {
        registry.addConverter(new LanguageConverter());
    }
}
