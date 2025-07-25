package org.tma.intern.adapter.api;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.RestResponse;
import org.tma.intern.application.injection.IdentityServerAdmin;
import org.tma.intern.contract.RequestDto.ProfileRequest;
import org.tma.intern.contract.ResponseDto.CommonResponse;

import java.util.List;

@Path("/test")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TestResource extends BaseResource {

    IdentityServerAdmin keycloakClient;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("hello/{name}")
    public Uni<RestResponse<String>> hello(@PathParam("name") String name) {
        return Uni.createFrom().item(() -> RestResponse.ResponseBuilder
                .ok(messages.get("greeting", name), MediaType.APPLICATION_JSON)
                .build());
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("roles")
    public Uni<RestResponse<CommonResponse<List<String>>>> getRoles() {
        return keycloakClient.getRoles()
                .collect().asList()
                .map(roles ->
                        RestResponse.ok(CommonResponse.<List<String>>builder().data(roles).build()));
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("error")
    public RestResponse<String> error() {
        return RestResponse.ResponseBuilder
                .create(RestResponse.Status.UNAUTHORIZED, "Unauthenticated")
                .build();
    }

    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @PUT
    @Path("/{id}/avatar")
    public String updateAvatar(@PathParam("id") String id, ProfileRequest.Avatar body) {
        log.info("File name: {}", body.getImage().uploadedFile().getFileName());
        return id;
    }

}
