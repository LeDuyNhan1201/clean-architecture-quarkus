package org.tma.intern.adapter.component;

import jakarta.enterprise.context.RequestScoped;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;
import java.util.ResourceBundle;

@RequestScoped
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class MessagesProvider {

    LocaleProvider localeProvider;

    public String get(String key) {
        Locale locale = localeProvider.getLocale();
        ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
        return bundle.getString(key);
    }

    public String get(String key, Object... args) {
        return String.format(get(key), args);
    }
}
