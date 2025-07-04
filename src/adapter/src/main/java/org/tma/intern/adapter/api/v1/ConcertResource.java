package org.tma.intern.adapter.api.v1;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
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
import org.jboss.resteasy.reactive.NoCache;
import org.jboss.resteasy.reactive.RestResponse;
import org.tma.intern.adapter.api.BaseResource;
import org.tma.intern.application.service.ConcertService;
import org.tma.intern.contract.RequestDto.ConcertRequest;
import org.tma.intern.contract.ResponseDto.ConcertResponse;
import org.tma.intern.contract.ResponseDto.PageResponse;

@Path("/v1/concert")
@Tag(name = "Concerts", description = "Concert operations")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class ConcertResource extends BaseResource {

    ConcertService concertService;

    @POST
    @Path("")
    @Operation(summary = "Create concert", description = "API to create a new concert.")
    @APIResponse(responseCode = "500", description = "Failed",
        content = @Content(schema = @Schema(implementation = String.class)))
    @APIResponse(responseCode = "201", description = "Success",
        content = @Content(schema = @Schema(implementation = String.class)))
    public Uni<RestResponse<String>> create(ConcertRequest.Body body) {
        return concertService.create(body)
            .onItem().transform(resultId ->
                RestResponse.ResponseBuilder.ok(
                    messages.get("Action.Success", "create", "concert"),
                    MediaType.APPLICATION_JSON).build());

    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update concert", description = "API to update an exist concert by id.")
    @APIResponse(responseCode = "500", description = "Failed",
        content = @Content(schema = @Schema(implementation = String.class)))
    @APIResponse(responseCode = "201", description = "Success",
        content = @Content(schema = @Schema(implementation = String.class)))
    public Uni<RestResponse<String>> update(Long id, ConcertRequest.Body body) {
        return concertService.update(id, body)
            .onItem().transform(resultId ->
                RestResponse.ResponseBuilder.ok(
                    messages.get("Action.Success", "update", "concert"),
                    MediaType.APPLICATION_JSON).build());

    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete concert", description = "API to soft delete a concert by id.")
    @APIResponse(responseCode = "500", description = "Failed",
        content = @Content(schema = @Schema(implementation = String.class)))
    @APIResponse(responseCode = "201", description = "Success",
        content = @Content(schema = @Schema(implementation = String.class)))
    public Uni<RestResponse<String>> softDelete(Long id) {
        return concertService.softDelete(id)
            .onItem().transform(resultId ->
                RestResponse.ResponseBuilder.ok(
                    messages.get("Action.Success", "delete", "concert"),
                    MediaType.APPLICATION_JSON).build());

    }

    @GET
    @Path("/{id}")
    @NoCache
    @Operation(summary = "Get concert details", description = "API to get details of a concert by id.")
    @APIResponse(responseCode = "404", description = "Not found !!!",
        content = @Content(schema = @Schema(implementation = String.class)))
    @APIResponse(responseCode = "200", description = "Success",
        content = @Content(schema = @Schema(implementation = ConcertResponse.Preview.class)))
    public Uni<RestResponse<ConcertResponse.Preview>> details(Long id) {
        return concertService.findById(id)
            .onItem().transform(concert ->
                RestResponse.ResponseBuilder.ok(concert, MediaType.APPLICATION_JSON).build());
    }

    @GET
    @Path("")
    @NoCache
    @Operation(summary = "Get concerts page", description = "API to get a page of concerts by index & limit.")
    @APIResponse(responseCode = "404", description = "Not found !!!",
        content = @Content(schema = @Schema(implementation = String.class)))
    @APIResponse(responseCode = "200", description = "Success",
        content = @Content(schema = @Schema(implementation = ConcertResponse.Preview.class)))
    public Uni<RestResponse<PageResponse<ConcertResponse.Preview>>> paging(
        @QueryParam("index") int index,
        @QueryParam("limit") int limit) {
        return concertService.findAll(index, limit)
            .onItem().transform(page ->
                RestResponse.ResponseBuilder.ok(page, MediaType.APPLICATION_JSON).build()
            );
    }

    @GET
    @Path("/seed/{count}")
    @NoCache
    @Operation(summary = "Seed concerts", description = "API to seed concerts with count.")
    @APIResponse(responseCode = "500", description = "Seed data failed !!!",
        content = @Content(schema = @Schema(implementation = String.class)))
    @APIResponse(responseCode = "200", description = "Success",
        content = @Content(schema = @Schema(implementation = Integer.class)))
    public Uni<RestResponse<Integer>> seed(int count) {
        return concertService.seedData(count)
            .onItem().transform(size ->
                RestResponse.ResponseBuilder.ok(size, MediaType.APPLICATION_JSON).build());
    }

}
