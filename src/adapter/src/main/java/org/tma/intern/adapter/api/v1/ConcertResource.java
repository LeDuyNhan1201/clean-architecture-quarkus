package org.tma.intern.adapter.api.v1;

import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
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
import org.tma.intern.contract.ResponseDto.CommonResponse;
import org.tma.intern.contract.ResponseDto.ConcertResponse;
import org.tma.intern.contract.ResponseDto.PageResponse;

import java.util.List;

@Path("/v1/concerts")
@Tag(name = "Concerts", description = "Concert operations")
@APIResponse(responseCode = "500", description = "Failed",
    content = @Content(schema = @Schema(implementation = String.class)))
@Produces(MediaType.APPLICATION_JSON)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class ConcertResource extends BaseResource {

    ConcertService concertService;

    @RolesAllowed({"concert:create"})
    @POST
    @Path("")
    @Operation(summary = "Create concert", description = "API to create a new concert.")
    @APIResponse(responseCode = "201", description = "Success",
        content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<RestResponse<CommonResponse<String>>> create(ConcertRequest.Body body) {
        checkRegion();
        return concertService.create(body).onItem().transform(id ->
            RestResponse.ResponseBuilder.ok(
                CommonResponse.<String>builder()
                    .message(messages.get("Action.Success", "Create", "concert"))
                    .data(id).build()
            ).build());
    }

    @RolesAllowed("concert:edit")
    @PUT
    @Path("/{id}")
    @Operation(summary = "Update concert", description = "API to update an exist concert by id.")
    @APIResponse(responseCode = "201", description = "Success",
        content = @Content(schema = @Schema(implementation = String.class)))
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<RestResponse<CommonResponse<String>>> update(@PathParam("id") String id, ConcertRequest.Body body) {
        return concertService.update(id, body).onItem().transform(resultId ->
            RestResponse.ResponseBuilder.ok(
                CommonResponse.<String>builder()
                    .message(messages.get("Action.Success", "Update", "concert"))
                    .data(id).build()
            ).build());

    }

    @RolesAllowed("concert:edit")
    @PATCH
    @Path("/{id}/approve")
    @Operation(summary = "Approve concert", description = "API to approve an exist concert by id.")
    @APIResponse(responseCode = "201", description = "Success",
        content = @Content(schema = @Schema(implementation = String.class)))
    public Uni<RestResponse<CommonResponse<String>>> approve(@PathParam("id") String id) {
        return concertService.approve(id).onItem().transform(resultId ->
            RestResponse.ResponseBuilder.ok(
                CommonResponse.<String>builder()
                    .message(messages.get("Action.Success", "Approve", "concert"))
                    .data(id).build()
            ).build());

    }

    @RolesAllowed("concert:edit")
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete concert", description = "API to soft delete a concert by id.")
    @APIResponse(responseCode = "201", description = "Success",
        content = @Content(schema = @Schema(implementation = String.class)))
    public Uni<RestResponse<CommonResponse<String>>> softDelete(@PathParam("id") String id) {
        return concertService.softDelete(id).onItem().transform(resultId ->
            RestResponse.ResponseBuilder.ok(
                CommonResponse.<String>builder()
                    .message(messages.get("Action.Success", "Soft delete", "concert"))
                    .data(id).build()
            ).build());

    }

    @RolesAllowed("concert:view")
    @GET
    @Path("/{id}")
    @NoCache
    @Operation(summary = "Get concert details", description = "API to get details of a concert by id.")
    @APIResponse(responseCode = "200", description = "Success",
        content = @Content(schema = @Schema(implementation = ConcertResponse.Details.class)))
    public Uni<RestResponse<ConcertResponse.Details>> details(@PathParam("id") String id) {
        return concertService.findById(id).onItem().transform(concert ->
            RestResponse.ResponseBuilder.ok(concert).build());
    }

    @RolesAllowed("concert:view")
    @GET
    @Path("")
    @NoCache
    @Operation(summary = "Get concerts page", description = "API to get a page of concerts by index & limit.")
    @APIResponse(responseCode = "200", description = "Success",
        content = @Content(schema = @Schema(implementation = PageResponse.class)))
    public Uni<RestResponse<PageResponse<ConcertResponse.Preview>>> paging(
        @QueryParam("index") int index,
        @QueryParam("limit") int limit) {
        return concertService.findAll(index, limit).onItem().transform(page ->
            RestResponse.ResponseBuilder.ok(page).build());
    }

    @GET
    @Path("/seed/{count}")
    @NoCache
    @Operation(summary = "Seed concerts", description = "API to seed concerts with count.")
    @APIResponse(responseCode = "200", description = "Success",
        content = @Content(schema = @Schema(implementation = Integer.class)))
    public Uni<RestResponse<CommonResponse<List<String>>>> seed(int count) {
        return concertService.seedData(count).onItem().transform(ids ->
            RestResponse.ok(CommonResponse.<List<String>>builder().data(ids).build()));
    }

}
