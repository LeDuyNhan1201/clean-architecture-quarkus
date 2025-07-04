package org.tma.intern.adapter.api.v1;

import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
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
import org.tma.intern.application.service.UserService;
import org.tma.intern.contract.RequestDto.AuthRequest;
import org.tma.intern.contract.RequestDto.UserRequest;
import org.tma.intern.contract.ResponseDto.AuthResponse;

@Path("/v1/auth")
@Tag(name = "Auth", description = "Auth operations")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthResource extends BaseResource {

    UserService userService;

    AuthService authService;

    @POST
    @Path("/sign-up")
    @Blocking
    @Operation(summary = "Sign up", description = "Sign up new user")
    @APIResponse(responseCode = "500", description = "Failed",
        content = @Content(schema = @Schema(implementation = String.class)))
    @APIResponse(responseCode = "201", description = "Success",
        content = @Content(schema = @Schema(implementation = String.class)))
    public RestResponse<String> signUp(UserRequest.Creation body) {
        String userId = userService.create(body);
        return RestResponse.ResponseBuilder.ok(
            messages.get("Action.Success", "Create", "user"),
            MediaType.TEXT_PLAIN).build();
    }

    @POST
    @Path("/sign-in")
    @Operation(summary = "Sign in", description = "Sign in and get tokens")
    @APIResponse(responseCode = "500", description = "Failed",
        content = @Content(schema = @Schema(implementation = String.class)))
    @APIResponse(responseCode = "201", description = "Success",
        content = @Content(schema = @Schema(implementation = String.class)))
    public Uni<RestResponse<AuthResponse.Tokens>> signIn(@Valid AuthRequest.SignIn body) {
        return authService.getTokens(body).map(tokens ->
            RestResponse.ResponseBuilder.ok(tokens, MediaType.APPLICATION_JSON).build());
    }

}
