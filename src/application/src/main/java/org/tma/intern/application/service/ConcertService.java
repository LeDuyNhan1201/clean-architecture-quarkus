package org.tma.intern.application.service;

import io.smallrye.mutiny.Uni;
import org.tma.intern.contract.RequestDto.ConcertRequest;
import org.tma.intern.contract.ResponseDto.ConcertResponse;
import org.tma.intern.contract.ResponseDto.PageResponse;

public interface ConcertService {

    Uni<Long> create(ConcertRequest.Body request);

    Uni<Long> update(Long id, ConcertRequest.Body request);

    Uni<Long> softDelete(Long id);

    Uni<ConcertResponse.Preview> findById(Long id);

    Uni<PageResponse<ConcertResponse.Preview>> findAll(int page, int limit);

    Uni<Integer> seedData(int count);

}
