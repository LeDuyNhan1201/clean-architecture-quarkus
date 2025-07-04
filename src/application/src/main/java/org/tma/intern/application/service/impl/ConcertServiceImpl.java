package org.tma.intern.application.service.impl;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.panache.common.Page;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.tma.intern.application.exception.Error;
import org.tma.intern.application.exception.HttpException;
import org.tma.intern.application.service.ConcertService;
import org.tma.intern.contract.RequestDto.ConcertRequest;
import org.tma.intern.contract.ResponseDto.ConcertResponse;
import org.tma.intern.contract.ResponseDto.PageResponse;
import org.tma.intern.contract.mapper.ConcertMapper;
import org.tma.intern.domain.entity.AuditEntity;
import org.tma.intern.domain.entity.Concert;
import org.tma.intern.domain.repository.ConcertRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.IntStream;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConcertServiceImpl implements ConcertService {

    ConcertRepository concertRepository;

    ConcertMapper concertMapper;

    Faker faker;

//    @WithTransaction
    @Override
    public Multi<Long> seedData(int count) {
        List<Concert> concerts = IntStream.range(0, count)
                .mapToObj(i -> createRandomConcert()).toList();
        return concertRepository.persist(concerts) // Uni<Void> or Uni<List<Concert>>
                .replaceWith(concerts).onItem().transformToMulti(list ->
                        Multi.createFrom().items(list.stream().map(AuditEntity::getId)));
    }

    private Concert createRandomConcert() {
        Instant now = Instant.now();

        Instant startTime = now.plus(faker.number().numberBetween(1, 24 * 60 * 60), ChronoUnit.SECONDS);
        Instant endTime = startTime.plus(faker.number().numberBetween(1, 7 * 24 * 60 * 60), ChronoUnit.SECONDS);

        return Concert.builder()
                .title(faker.book().title())
                .description(faker.lorem().sentence(20))
                .location(faker.location().building())
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

    @WithTransaction
    @Override
    public Uni<Long> create(ConcertRequest.Body request) {
        return concertRepository.persist(concertMapper.toEntity(request))
                .onItem().transform(Concert::getId)
                .onFailure().transform(throwable ->
                        new HttpException(Error.ACTION_FAILED, throwable,
                                Response.Status.NOT_IMPLEMENTED, "Create", "Concert"));
    }

    @WithTransaction
    @Override
    public Uni<Long> update(Long id, ConcertRequest.Body request) {
        return findById(id).onItem().ifNotNull().transformToUni(concert ->
                concertRepository.persist(concertMapper.toEntity(request))
                        .onItem().transform(Concert::getId)
                        .onFailure().transform(throwable ->
                                new HttpException(Error.ACTION_FAILED, throwable,
                                        Response.Status.NOT_IMPLEMENTED, "Update", "concert")));
    }

    @WithTransaction
    @Override
    public Uni<Long> softDelete(Long id) {
        return findById(id).onItem().ifNotNull().transformToUni(concert ->
                concertRepository.persist(Concert.builder().id(concert.getId()).build())
                        .onItem().transform(Concert::getId)
                        .onFailure().transform(throwable ->
                                new HttpException(Error.ACTION_FAILED, throwable,
                                        Response.Status.NOT_IMPLEMENTED, "Soft delete", "concert")));
    }

    @WithSession
    @Override
    public Uni<ConcertResponse.Preview> findById(Long id) {
        return concertRepository.findById(id)
                .onItem().ifNotNull().transform(concertMapper::toDto)
                .onItem().ifNull().failWith(() ->
                        new HttpException(Error.RESOURCE_NOT_FOUND, null, Response.Status.NOT_FOUND, "Concert"));
    }

    @WithSession
    @Override
    public Uni<PageResponse<ConcertResponse.Preview>> findAll(int index, int limit) {
        return Uni.combine().all().unis(
                concertRepository.findAll().page(Page.of(index, limit)).list(),
                concertRepository.count()).asTuple().map(tuple ->
                PageResponse.of(
                        tuple.getItem1().stream().map(concertMapper::toDto).toList(),
                        index, limit, tuple.getItem2()));
    }

}
