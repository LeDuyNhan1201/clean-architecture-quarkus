package org.tma.intern.adapter.component;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.core.HttpHeaders;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

@RequestScoped
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class LocaleProvider {

    HttpHeaders headers;

    public Locale getLocale() {
        return Locale.forLanguageTag(headers.getAcceptableLanguages()
                .stream().map(Locale::getLanguage)
                .findFirst()
                .orElse(Locale.getDefault().getLanguage()));
    }
}
