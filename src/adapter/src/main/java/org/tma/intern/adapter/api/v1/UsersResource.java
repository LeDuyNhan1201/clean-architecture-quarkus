package org.tma.intern.adapter.api.v1;

import io.quarkus.security.Authenticated;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.NoCache;

import org.jboss.resteasy.reactive.RestResponse;
import org.tma.intern.adapter.api.BaseResource;
import org.tma.intern.application.service.UserService;
import org.tma.intern.contract.ResponseDto.UserResponse;

@Path("/v1/user")
@Tag(name = "Users", description = "User operations")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UsersResource extends BaseResource {

    UserService userService;

    @GET
    @Path("/me")
    @NoCache
    @Authenticated
    @Operation(summary = "Get current username", description = "API to get name of current user")
    @APIResponse(responseCode = "401", description = "Unauthenticated", content = @Content())
    @APIResponse(responseCode = "403", description = "Invalid resource !!!",
        content = @Content(schema = @Schema(implementation = String.class)))
    @APIResponse(responseCode = "200", description = "Success",
        content = @Content(schema = @Schema(implementation = UserResponse.PreviewUser.class)))
    public Uni<RestResponse<UserResponse.PreviewUser>> me() {
        return identityContext.getCurrentUser()
            .onItem().transform(username ->
                RestResponse.ResponseBuilder
                    .ok(UserResponse.PreviewUser.builder().username(username).build(), MediaType.APPLICATION_JSON)
                    .build()
            );
    }

    @GET
    @Path("/seed/{count}")
    @NoCache
    @Operation(summary = "Seed users", description = "API to seed user with [Role]:user.")
    @APIResponse(responseCode = "500", description = "Seed data failed !!!",
        content = @Content(schema = @Schema(implementation = String.class)))
    @APIResponse(responseCode = "200", description = "Success",
        content = @Content(schema = @Schema(implementation = String.class)))
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> seedUsers(@PathParam("count") int count) {
        return userService.seedUsers(count)
            .onItem().transform(userIds -> Response.ok(userIds).build());
    }

}