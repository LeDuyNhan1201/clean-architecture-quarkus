package org.tma.intern.adapter.api;

import jakarta.inject.Inject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.tma.intern.adapter.component.MessagesProvider;
import org.tma.intern.application.injection.IdentityContext;

@FieldDefaults(level = AccessLevel.PROTECTED)
@AllArgsConstructor
@NoArgsConstructor
public class BaseResource {

    @Inject
    IdentityContext identityContext;

    @Inject
    MessagesProvider messages;

}
