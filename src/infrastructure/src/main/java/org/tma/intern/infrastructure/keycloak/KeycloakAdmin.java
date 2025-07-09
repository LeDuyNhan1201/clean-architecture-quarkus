package org.tma.intern.infrastructure.keycloak;

import io.quarkus.arc.Unremovable;
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
import org.keycloak.admin.client.resource.AuthorizationResource;
import org.keycloak.admin.client.resource.ResourcesResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;
import org.tma.intern.application.injection.IdentityServerAdmin;
import org.tma.intern.domain.entity.IdentityUser;
import org.tma.intern.infrastructure.OAuth2Config;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class KeycloakAdmin implements IdentityServerAdmin {

    Keycloak keycloak;

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
    public Uni<String> createUser(IdentityUser entity, String... roles) {
        UserRepresentation user = new UserRepresentation();
        user.setEmail(entity.getEmail());
        user.setCreatedTimestamp(Instant.now().toEpochMilli());
        user.setEnabled(true);
        user.setCredentials(Collections.singletonList(createPasswordCredential(entity.getPassword())));
        user.setRealmRoles(List.of(roles));
        user.setAttributes(Map.of("region", Collections.singletonList(entity.getRegion())));

        return Uni.createFrom().item(() ->
                createUserBlocking(user)).runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }

    @Override
    public Uni<String> deleteUser(String id) {
        return Uni.createFrom().item(() -> deleteUserBlocking(id));
    }

    @Override
    public Multi<String> createUsers(List<IdentityUser> entities, String... roles) {
        List<Uni<String>> creations = entities.stream()
                .map(entity -> createUser(entity, roles)).toList();

        return Uni.combine().all().unis(creations)
                .with(objects -> objects.stream().map(Object::toString).toList())
                .onItem().transformToMulti(Multi.createFrom()::iterable);
    }

    @Override
    public Uni<String> createConcertResource(Long concertId, String region) {
        return Uni.createFrom().item(() ->
            createConcertResourceBlocking(concertId.toString(), region)).runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
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

    public String createConcertResourceBlocking(String concertId, String region) {
        String clientId = "concert-service";

        // 1. Find the client
        List<ClientRepresentation> clients = keycloak.realm(REALM)
            .clients()
            .findByClientId(clientId);
        if (clients.isEmpty()) {
            throw new IllegalStateException("Client not found: " + clientId);
        }
        String clientUuid = clients.get(0).getId();

        // 2. Get Authorization Resource
        AuthorizationResource authz = keycloak.realm(REALM)
            .clients()
            .get(clientUuid)
            .authorization();

        ResourcesResource resources = authz.resources();

        // 3. Build ResourceRepresentation
        ResourceRepresentation resource = new ResourceRepresentation();
        resource.setName("Concert " + concertId);
        resource.setType("concert");
        resource.setUris(Set.of("/concerts/" + concertId));
        resource.setScopes(Set.of(
            new ScopeRepresentation("view"),
            new ScopeRepresentation("edit"),
            new ScopeRepresentation("approve")));
        resource.setOwnerManagedAccess(false);
        resource.setAttributes(Map.of("region", List.of(region)));

        // 4. Create Resource
        try (Response response = resources.create(resource)) {
            if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
                String location = response.getHeaderString("Location");
                if (location == null) {
                    throw new RuntimeException("No Location header returned for created resource");
                }
                return location.substring(location.lastIndexOf('/') + 1);
            } else {
                String errorMessage = response.readEntity(String.class);
                throw new RuntimeException("Concert resource creation failed with status: "
                    + response.getStatus() + " - " + errorMessage);
            }
        }
    }

    private CredentialRepresentation createPasswordCredential(String password) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);
        return credential;
    }

    private String createUserBlocking(UserRepresentation user) {
        UsersResource users = keycloak.realm(REALM).users();

        try (Response response = users.create(user)) {
            int status = response.getStatus();

            if (status == Response.Status.CREATED.getStatusCode()) {
                String location = response.getHeaderString("Location");
                if (location == null) {
                    throw new RuntimeException("User creation succeeded but no Location header returned");
                }
                return location.substring(location.lastIndexOf('/') + 1);
            } else {
                String error = response.readEntity(String.class);
                throw new RuntimeException("Keycloak user creation failed with status: " + status + " - " + error);
            }
        }
    }

    private String deleteUserBlocking(String id) {
        UsersResource users = keycloak.realm(REALM).users();
        try (Response response = users.delete(id)) {
            int status = response.getStatus();
            if (status != Response.Status.NO_CONTENT.getStatusCode()) {
                throw new RuntimeException("Keycloak user deletion failed with status: " + status);
            }
            return id;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete user in Keycloak: " + id, e);
        }
    }

}
