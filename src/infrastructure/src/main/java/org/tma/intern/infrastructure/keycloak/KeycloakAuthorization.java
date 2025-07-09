package org.tma.intern.infrastructure.keycloak;

import io.quarkus.security.ForbiddenException;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.authorization.client.AuthorizationDeniedException;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.representations.idm.authorization.AuthorizationRequest;
import org.tma.intern.application.injection.AuthorizationService;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class KeycloakAuthorization implements AuthorizationService {

    AuthzClient keycloakAuthorization;

    @Override
    public Uni<Void> enforcePermission(String accessToken, String resourceId, String scope) {
        return Uni.createFrom().item(() -> {
            AuthorizationRequest request = new AuthorizationRequest();
            request.addPermission(resourceId, scope);
            request.setRpt(accessToken);

            try {
                keycloakAuthorization.authorization(accessToken).authorize(request);
            } catch (AuthorizationDeniedException e) {
                throw new ForbiddenException("Not authorized for resource " + resourceId + " and scope " + scope);
            }

            return null;
        }).emitOn(Infrastructure.getDefaultExecutor()).replaceWithVoid();
    }

}
