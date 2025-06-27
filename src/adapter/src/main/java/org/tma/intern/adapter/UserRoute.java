package org.tma.intern.adapter;

import io.quarkus.vertx.web.*;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.RoutingContext;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.*;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.tma.intern.application.UserService;
import org.tma.intern.common.RequestDto;
import org.tma.intern.common.ResponseDto;
import java.util.UUID;

@ApplicationScoped
@SecurityScheme(securitySchemeName = "basic_bearer", description = "This is for Authorization Header Bearer Token",
        type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
//@SecurityScheme(securitySchemeName = "oauth2_bearer", description = "This is for Authorization Header Bearer Token",
//        type = SecuritySchemeType.OAUTH2,
//        flows = @OAuthFlows(
//                authorizationCode = @OAuthFlow(
//                        authorizationUrl = "${openapi.security.oauthflow.authorization-url}",
//                        tokenUrl = "${openapi.security.oauthflow.token-url}",
//                        scopes = {
//                                @OAuthScope(name = "openid", description = "openid")
//                        })))
@RouteBase(path = "user", produces = ReactiveRoutes.APPLICATION_JSON)
@Tag(name = "User APIs", description = "This is REST APIs to manage user")
public class UserRoute {

    private final UserService userService;

    public UserRoute(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Create new user", description = "API to Create new user")
    @APIResponse(responseCode = "common/no-content", description = "No content", content = @Content())
    @APIResponse(responseCode = "common/not-found", description = "Not Found",
            content = @Content(schema = @Schema(implementation = String.class)))
    @APIResponse(responseCode = "400", description = "Bad Request",
            content = @Content(schema = @Schema(implementation = String.class)))
    @Route(methods = Route.HttpMethod.POST, path = "")
    Uni<String> create(@Body RequestDto.CreateUser request, RoutingContext context) {
        return userService.create(request)
                .onItem().transform(success -> {
                    if (success) {
                        return UUID.randomUUID().toString();
                    } else {
                        throw new RuntimeException("User creation failed");
                    }
                });
    }

    @Route(methods = Route.HttpMethod.PUT, path = ":id")
    Uni<String> update(@Param("id") String id,
                       @Body RequestDto.UpdateUser request, RoutingContext context) {
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
    Uni<ResponseDto.PreviewUser> readSingle(@Param String id, RoutingContext context) {
        return Uni.createFrom().item(() -> new ResponseDto.PreviewUser("ldnhan"));
    }

}
