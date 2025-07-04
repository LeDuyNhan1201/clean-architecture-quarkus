package org.tma.intern.infrastructure;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.MultiMap;
import io.vertx.mutiny.ext.web.client.WebClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.tma.intern.application.injection.IdentityProviderClient;
import org.tma.intern.domain.entity.IdentityUser;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class KeycloakClient implements IdentityProviderClient {

    Keycloak keycloak;

    AuthzClient keycloakAuthorization;

    WebClient webClient;

    OAuth2Config oAuth2;

    @ConfigProperty(name = "quarkus.keycloak.admin-client.realm")
    @NonFinal
    String REALM;

    @Override
    public Multi<String> getRoles() {
        return Multi.createFrom().items(() ->
                keycloak.realm(REALM).roles().list()
                        .stream()
                        .map(RoleRepresentation::getName)
        ).runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }

    @Override
    public Multi<String> getUserIds(int count) {
        return Multi.createFrom().items(() ->
                keycloak.realm(REALM).users().search("", 0, count)
                        .stream().map(UserRepresentation::getId)
        ).runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }

    @Override
    public Uni<String> create(IdentityUser entity, String... roles) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(entity.getUsername());
        user.setEmail(entity.getEmail());
        user.setCreatedTimestamp(Instant.now().toEpochMilli());
        user.setEnabled(true);
        user.setCredentials(Collections.singletonList(createPasswordCredential(entity.getPassword())));
        user.setRealmRoles(List.of(roles));

        return Uni.createFrom().item(() ->
                createBlocking(user)).runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }

    @Override
    public Uni<String> delete(String id) {
        return Uni.createFrom().item(() -> deleteBlocking(id));
    }

    @Override
    public Multi<String> createUsers(List<IdentityUser> entities, String... roles) {
        List<Uni<String>> creations = entities.stream()
                .map(entity -> create(entity, roles)).toList();

        return Uni.combine().all().unis(creations)
                .with(objects -> objects.stream().map(Object::toString).toList())
                .onItem().transformToMulti(Multi.createFrom()::iterable);
    }


    @Override
    public Uni<Map<String, String>> getTokens(String username, String password) {
        return webClient.postAbs(oAuth2.tokenEndpoint())
                .putHeader("Content-Type", "application/x-www-form-urlencoded")
                .sendForm(MultiMap.caseInsensitiveMultiMap().addAll(Map.of(
                        "grant_type", "password",
                        "client_id", oAuth2.clientId(),
                        "client_secret", oAuth2.clientSecret(),
                        "scope", oAuth2.scope(),
                        "username", username,
                        "password", password
                ))).onItem()
                .transform(response -> {
                    if (response.statusCode() != 200) {
                        log.error("Sign In failed: {}", response.statusMessage());
                        throw new RuntimeException("Sign In failed: " + response.statusMessage());
                    }
                    JsonObject json = response.bodyAsJsonObject();
                    return Map.of(
                            "accessToken", json.getString("access_token"),
                            "refreshToken", json.getString("refresh_token"));
                });
    }

    private CredentialRepresentation createPasswordCredential(String password) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);
        return credential;
    }

    private String createBlocking(UserRepresentation user) {
        try (Response response = keycloak.realm(REALM).users().create(user)) {
            if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
                String location = response.getHeaderString("Location");
                return location.substring(location.lastIndexOf('/') + 1);
            } else {
                throw new RuntimeException("Keycloak user creation failed with status: " + response.getStatus());
            }
        }
    }

    private String deleteBlocking(String id) {
        UsersResource users = keycloak.realm(REALM).users();
        try (Response response = users.delete(id)) {
            int status = response.getStatus();
            if (status != Response.Status.NO_CONTENT.getStatusCode()) {
                throw new RuntimeException("Keycloak user deletion failed with status: " + status);
            }
            return id;
        } catch (Exception e) {
            throw new RuntimeException("Keycloak user deletion threw exception", e);
        }
    }

}
