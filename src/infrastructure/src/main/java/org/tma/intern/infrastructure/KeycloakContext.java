package org.tma.intern.infrastructure;

import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.RequestScoped;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.tma.intern.application.injection.IdentityContext;

@RequestScoped
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class KeycloakContext implements IdentityContext {

    SecurityIdentity securityIdentity;

    @Override
    public Uni<String> getCurrentUser() {
        return Uni.createFrom().item(() -> !securityIdentity.isAnonymous()
                ? securityIdentity.getPrincipal().getName() : "Anonymous");
    }

}
