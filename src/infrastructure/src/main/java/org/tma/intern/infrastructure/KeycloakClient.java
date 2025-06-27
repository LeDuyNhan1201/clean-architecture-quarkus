package org.tma.intern.infrastructure;

import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RoleRepresentation;
import org.tma.intern.application.IdentityProviderClient;

@ApplicationScoped
public class KeycloakClient implements IdentityProviderClient {

    private static final Logger log = Logger.getLogger(KeycloakClient.class);

    @Inject
    Keycloak keycloak;

    @Override
    public Multi<String> getRoles() {
        return Multi.createFrom().items(() ->
                keycloak.realm("quarkus")
                        .roles().list().stream().map(RoleRepresentation::getName));
    }

}
