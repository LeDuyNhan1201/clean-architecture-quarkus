package org.tma.intern.infrastructure;

import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.MultiMap;
import io.vertx.mutiny.ext.web.client.WebClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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

    static String REALM = "quarkus";

    @Override
    public List<String> getRoles() {
        return keycloak.realm(REALM).roles().list()
                .stream().map(RoleRepresentation::getName).toList();
    }

    @Override
    public List<String> getUserIds(int count) {
        return keycloak.realm(REALM).users().search("", 0, count)
            .stream().map(UserRepresentation::getId).toList();
    }

    @Override
    public String create(IdentityUser entity, String... roles) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(entity.getUsername());
        user.setEmail(entity.getEmail());
        user.setCreatedTimestamp(Instant.now().toEpochMilli());
        user.setEnabled(true);
        user.setCredentials(Collections.singletonList(createPasswordCredential(entity.getPassword())));
        user.setRealmRoles(List.of(roles));

        var token = keycloak.tokenManager().getAccessToken();
        log.warn("Access token: {}", token.getToken());

        String location;
        try (Response response = keycloak.realm(REALM).users().create(user)) {
            int status = response.getStatus();
            if (status != 201) {
                log.error("Keycloak user creation failed: {}", response.getStatusInfo().getReasonPhrase());
                throw new RuntimeException("Keycloak user creation failed");
            }
            location = response.getHeaderString("Location");
            return location.substring(location.lastIndexOf('/') + 1);
        } catch (Exception e) {
            throw new RuntimeException("Keycloak user creation threw exception", e);
        }
    }

    @Override
    public String delete(String id) {
        UsersResource users = keycloak.realm(REALM).users();
        try (Response response = users.delete(id)) {
            int status = response.getStatus();
            if (status != 204) {
                throw new RuntimeException("Keycloak user deletion failed with status: " + status);
            }
            return id;
        } catch (Exception e) {
            throw new RuntimeException("Keycloak user deletion threw exception", e);
        }
    }

    @Override
    public boolean createUsers(List<IdentityUser> entities, String... roles) {
        for (IdentityUser entity : entities) create(entity, roles);
        return true;
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

}
