package org.tma.intern.adapter.api.v1;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import io.quarkus.security.Authenticated;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.tma.intern.adapter.api.BaseResource;

@Path("/api/admin")
@Tag(name = "Admin", description = "Admin operations")
@Authenticated
public class AdminResource extends BaseResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String admin() {
        return "granted";
    }
}