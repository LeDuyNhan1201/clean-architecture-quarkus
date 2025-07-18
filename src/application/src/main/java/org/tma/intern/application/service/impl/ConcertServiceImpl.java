package org.tma.intern.application.service.impl;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.panache.common.Page;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.bson.types.ObjectId;
import org.tma.intern.application.exception.Error;
import org.tma.intern.application.exception.HttpException;
import org.tma.intern.application.injection.IdentityContext;
import org.tma.intern.application.service.BaseService;
import org.tma.intern.application.service.ConcertService;
import org.tma.intern.contract.RequestDto.ConcertRequest;
import org.tma.intern.contract.ResponseDto.ConcertResponse;
import org.tma.intern.contract.ResponseDto.PageResponse;
import org.tma.intern.contract.mapper.ConcertMapper;
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
public class ConcertServiceImpl extends BaseService implements ConcertService {

    ConcertRepository concertRepository;

    ConcertMapper concertMapper;

    Faker faker;

    @WithTransaction
    @Override
    public Uni<List<String>> seedData(int count) {
        List<Concert> concerts = IntStream.range(0, count).mapToObj(i -> createRandomConcert()).toList();
        return concertRepository.persist(concerts).replaceWith(concerts)
            .onItem().transformToUni(list ->
                Uni.createFrom().item(list.stream().map(concert -> concert.getId().toHexString()).toList()));
    }

    @WithTransaction
    @Override
    public Uni<String> create(ConcertRequest.Body request) {
        Concert entity = concertMapper.toEntity(request);
        entity.setRegion(messages.getRegion());
        entity.setCreatedBy(identityContext.getCurrentUser());
        return concertRepository.persist(entity)
            .onItem().transform(result -> result.getId().toHexString())
            .onFailure().transform(throwable ->
                new HttpException(Error.ACTION_FAILED, throwable,
                    Response.Status.NOT_IMPLEMENTED, "Create", "Concert"));
    }

    @WithTransaction
    @Override
    public Uni<String> update(String id, ConcertRequest.Body request) {
        return findById(id).onItem().ifNotNull().transformToUni(concert -> {
            Concert entity = concertMapper.toEntity(request);
            entity.setUpdatedAt(Instant.now());
            entity.setUpdatedBy(identityContext.getCurrentUser());
            return concertRepository.persist(concertMapper.toEntity(request))
                .onItem().transform(result -> result.getId().toHexString())
                .onFailure().transform(throwable ->
                    new HttpException(Error.ACTION_FAILED, throwable,
                        Response.Status.NOT_IMPLEMENTED, "Update", "concert"));
        });
    }

    @Override
    public Uni<String> approve(String id) {
        return findById(id).onItem().ifNotNull().transformToUni(concert ->
            concertRepository.persist(Concert.builder().id(new ObjectId(concert.getId()))
                    .isApproved(true)
                    .updatedAt(Instant.now())
                    .updatedBy(identityContext.getCurrentUser()).build())
                .onItem().transform(result -> result.getId().toHexString())
                .onFailure().transform(throwable ->
                    new HttpException(Error.ACTION_FAILED, throwable,
                        Response.Status.NOT_IMPLEMENTED, "Approve", "concert")));
    }

    @WithTransaction
    @Override
    public Uni<String> softDelete(String id) {
        return findById(id).onItem().ifNotNull().transformToUni(concert ->
            concertRepository.persist(Concert.builder()
                    .id(new ObjectId(concert.getId()))
                    .isDeleted(true)
                    .updatedAt(Instant.now())
                    .updatedBy(identityContext.getCurrentUser()).build())
                .onItem().transform(result -> result.getId().toHexString())
                .onFailure().transform(throwable ->
                    new HttpException(Error.ACTION_FAILED, throwable,
                        Response.Status.NOT_IMPLEMENTED, "Soft delete", "concert")));
    }

    @WithSession
    @Override
    public Uni<ConcertResponse.Details> findById(String id) {
        return concertRepository.findById(new ObjectId(id))
            .onItem().ifNotNull().transform(concertMapper::toDetailsDto)
            .onItem().ifNull().failWith(() ->
                new HttpException(Error.RESOURCE_NOT_FOUND, null, Response.Status.NOT_FOUND, "Concert"));
    }

    @WithSession
    @Override
    public Uni<PageResponse<ConcertResponse.Preview>> findAll(int index, int limit) {
        return Uni.combine().all().unis(
            concertRepository.find("is_deleted", false).page(Page.of(index, limit)).list(),
            concertRepository.count()).asTuple().map(tuple ->
            PageResponse.of(
                tuple.getItem1().stream().map(concertMapper::toPreviewDto).toList(),
                index, limit, tuple.getItem2()));
    }

    private Concert createRandomConcert() {
        Instant now = Instant.now();

        Instant startTime = now.plus(faker.number().numberBetween(1, 24 * 60 * 60), ChronoUnit.SECONDS);
        Instant endTime = startTime.plus(faker.number().numberBetween(1, 7 * 24 * 60 * 60), ChronoUnit.SECONDS);

        return Concert.builder()
            .title(faker.book().title())
            .location(faker.location().building())
            .region(faker.languageCode().iso639())
            .startTime(startTime)
            .endTime(endTime)
            .build();
    }

}
