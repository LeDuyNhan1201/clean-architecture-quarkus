package org.tma.intern.application.injection;

import io.smallrye.mutiny.Uni;

public interface AuthorizationService {

    Uni<Void> enforcePermission(String accessToken, String resourceId, String scope);

}
