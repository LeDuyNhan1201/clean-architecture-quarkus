package org.tma.intern.adapter.api.v1;

import io.smallrye.mutiny.Uni;
import io.vertx.core.Vertx;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestResponse;
import org.tma.intern.adapter.api.BaseResource;
import org.tma.intern.application.service.AuthService;
import org.tma.intern.application.service.ProfileService;
import org.tma.intern.application.service.UserService;
import org.tma.intern.contract.RequestDto.AuthRequest;
import org.tma.intern.contract.RequestDto.UserRequest;
import org.tma.intern.contract.ResponseDto.AuthResponse;
import org.tma.intern.contract.ResponseDto.CommonResponse;
import org.tma.intern.domain.entity.Profile;

@Path("/v1/auth")
@Tag(name = "Auth", description = "Auth operations")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthResource extends BaseResource {

    UserService userService;

    AuthService authService;

    ProfileService profileService;

    @POST
    @Path("/sign-up")
    @Operation(summary = "Sign up", description = "Sign up new user")
    @APIResponse(responseCode = "500", description = "Failed",
            content = @Content(schema = @Schema(implementation = String.class)))
    @APIResponse(responseCode = "201", description = "Success",
            content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    public Uni<RestResponse<CommonResponse<String>>> signUp(UserRequest.Creation body) {
        return userService.create(body)
                .flatMap(userId ->
                        Uni.createFrom().emitter(emitter ->
                                Vertx.currentContext().runOnContext(v ->
                                        profileService.create(Profile.builder().userId(userId).build())
                                                .onFailure().call(() -> userService.delete(userId))
                                                .replaceWith(userId)
                                                .subscribe().with(emitter::complete, emitter::fail)))
                )
                .map(userId -> RestResponse.ResponseBuilder.ok(
                        CommonResponse.<String>builder()
                                .message(messages.get("Action.Success", "Create", "user"))
                                .data(userId.toString()).build(),
                        MediaType.APPLICATION_JSON
                ).build());
    }

    @POST
    @Path("/sign-in")
    @Operation(summary = "Sign in", description = "Sign in and get tokens")
    @APIResponse(responseCode = "500", description = "Failed",
            content = @Content(schema = @Schema(implementation = String.class)))
    @APIResponse(responseCode = "200", description = "Success",
            content = @Content(schema = @Schema(implementation = AuthResponse.Tokens.class)))
    public Uni<RestResponse<AuthResponse.Tokens>> signIn(@Valid AuthRequest.SignIn body) {
        return authService.getTokens(body).map(tokens ->
                RestResponse.ResponseBuilder.ok(tokens, MediaType.APPLICATION_JSON).build());
    }

}
