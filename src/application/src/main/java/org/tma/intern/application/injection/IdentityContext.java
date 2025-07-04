package org.tma.intern.application.injection;

import io.smallrye.mutiny.Uni;

public interface IdentityContext {

    Uni<String> getCurrentUser();

}
