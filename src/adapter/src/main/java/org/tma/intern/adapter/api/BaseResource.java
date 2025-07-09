package org.tma.intern.adapter.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.tma.intern.application.exception.Error;
import org.tma.intern.application.exception.HttpException;
import org.tma.intern.application.injection.IdentityContext;
import org.tma.intern.application.injection.MessagesProvider;

@FieldDefaults(level = AccessLevel.PROTECTED)
@AllArgsConstructor
@NoArgsConstructor
public class BaseResource {

    @Inject
    IdentityContext identityContext;

    @Inject
    MessagesProvider messages;

    protected void checkRegion() {
        if (!identityContext.getRegion().equals(messages.getRegion()))
            throw new HttpException(Error.INVALID_AUTH_INFO, null, Response.Status.FORBIDDEN);
    }

}
