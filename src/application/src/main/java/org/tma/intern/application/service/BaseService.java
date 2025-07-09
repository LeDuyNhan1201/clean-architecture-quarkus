package org.tma.intern.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.tma.intern.application.injection.IdentityContext;
import org.tma.intern.application.injection.MessagesProvider;

@ApplicationScoped
@FieldDefaults(level = AccessLevel.PUBLIC)
@AllArgsConstructor
@NoArgsConstructor
public class BaseService {

    @Inject
    IdentityContext identityContext;

    @Inject
    MessagesProvider messages;

}