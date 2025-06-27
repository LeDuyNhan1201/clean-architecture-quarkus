package org.tma.intern.adapter;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestResponse;
import org.tma.intern.application.IdentityProviderClient;
import org.tma.intern.common.RequestDto;

@ApplicationScoped
@Path("/test")
public class TestResource {

    private final Logger log = Logger.getLogger(TestResource.class);

    private final IdentityProviderClient keycloakClient;

    public TestResource(IdentityProviderClient keycloakClient) {
        this.keycloakClient = keycloakClient;
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("roles")
    public RestResponse<Multi<String>> getRoles() {
        return RestResponse.ResponseBuilder
                .ok(keycloakClient.getRoles(), MediaType.APPLICATION_JSON)
                .build();
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
    public String updateAvatar(@PathParam("id") String id, RequestDto.UpdateAvatar body) {
        log.info("File name: " + body.image.uploadedFile().getFileName());
        return id;
    }

}
