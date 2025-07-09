package org.tma.intern.application.service;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.tma.intern.contract.RequestDto.ConcertRequest;
import org.tma.intern.contract.ResponseDto.ConcertResponse;
import org.tma.intern.contract.ResponseDto.PageResponse;

import java.util.List;

public interface ConcertService {

    Uni<String> create(ConcertRequest.Body request);

    Uni<String> update(String id, ConcertRequest.Body request);

    Uni<String> approve(String id);

    Uni<String> softDelete(String id);

    Uni<ConcertResponse.Details> findById(String id);

    Uni<PageResponse<ConcertResponse.Preview>> findAll(int page, int limit);

    Uni<List<String>> seedData(int count);

}
