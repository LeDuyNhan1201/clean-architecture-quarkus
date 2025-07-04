package org.tma.intern.adapter.api;

import io.quarkus.vertx.web.*;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.*;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.tma.intern.application.injection.IdentityContext;
import org.tma.intern.application.service.UserService;
import org.tma.intern.contract.RequestDto.UserRequest;
import org.tma.intern.contract.ResponseDto.UserResponse;

import java.util.UUID;

@ApplicationScoped
@RouteBase(path = "user", produces = ReactiveRoutes.APPLICATION_JSON)
@SecurityScheme(securitySchemeName = "basic_bearer", description = "This is for Authorization Header Bearer Token",
    type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
@Tag(name = "User APIs", description = "This is REST APIs to manage user")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserRoute {

    UserService userService;

    IdentityContext identityContext;

    @Route(methods = Route.HttpMethod.PUT, path = ":id")
    Uni<String> update(@Param("id") String id,
                       @Body UserRequest.UpdateBody request, RoutingContext context) {
        return Uni.createFrom().item(() -> id);
    }

    @Route(methods = Route.HttpMethod.DELETE, path = ":id")
    Uni<String> delete(@Param("id") String id, RoutingContext context) {
        return Uni.createFrom().item(() -> id);
    }

    @Route(methods = Route.HttpMethod.GET, path = "")
    Uni<String> readAll(RoutingContext context) {
        return Uni.createFrom().item(() -> UUID.randomUUID().toString());
    }

    @Route(methods = Route.HttpMethod.GET, path = ":id")
    Uni<UserResponse.PreviewUser> readSingle(@Param String id, RoutingContext context) {
        return Uni.createFrom().item(UserResponse.PreviewUser::new);
    }

    @Operation(summary = "Get current username", description = "API to get name of current user")
    @SecurityRequirement(name = "basic_bearer")
    @Route(path = "me", methods = Route.HttpMethod.GET)
    void me(RoutingContext rc) {
        identityContext.getCurrentUser() // returns Uni<String>
            .subscribe().with(username -> {
                JsonObject json = new JsonObject()
                    .put("username", username);
                rc.response()
                    .putHeader("Content-Type", "application/json")
                    .end(json.encode());
            }, failure -> rc.response()
                .setStatusCode(500)
                .end("Error: " + failure.getMessage()));
    }

}
