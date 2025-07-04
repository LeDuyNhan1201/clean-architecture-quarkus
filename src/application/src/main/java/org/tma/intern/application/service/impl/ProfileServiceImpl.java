package org.tma.intern.application.service.impl;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.tma.intern.application.Error;
import org.tma.intern.application.HttpException;
import org.tma.intern.application.injection.IdentityProviderClient;
import org.tma.intern.application.service.ProfileService;
import org.tma.intern.contract.RequestDto.ProfileRequest;
import org.tma.intern.contract.ResponseDto.ProfileResponse;
import org.tma.intern.contract.helper.FakerHelper;
import org.tma.intern.contract.mapper.ProfileMapper;
import org.tma.intern.domain.entity.Profile;
import org.tma.intern.domain.enums.Gender;
import org.tma.intern.domain.repository.ProfileRepository;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileServiceImpl implements ProfileService {

    ProfileRepository profileRepository;

    IdentityProviderClient keycloakClient;

    ProfileMapper profileMapper;

    Faker faker = new Faker();

    @WithTransaction
    @Override
    public Uni<Integer> seedData(int count) {
        return Multi.createFrom().range(0, count)
            .onItem().transform(index -> createRandomProfile())
            .onItem().transformToUniAndConcatenate(profileRepository::persist)
            .collect().asList().map(List::size);
    }

    private Profile createRandomProfile() {
        return Profile.builder()
            .gender(FakerHelper.randomEnum(Gender.class))
            .userId(FakerHelper.randomElement(keycloakClient.getUserIds(10)))
            .dayOfBirth(Date.from(faker.timeAndDate().birthday().atStartOfDay(ZoneId.systemDefault()).toInstant()))
            .build();
    }

    @WithTransaction
    @Override
    public Uni<Long> create(Profile entity) {
        return profileRepository.persist(entity)
            .onItem().transform(Profile::getId)
            .onFailure().transform(throwable ->
                new HttpException(Error.ACTION_FAILED, throwable,
                    Response.Status.NOT_IMPLEMENTED, "Create", "profile"));
    }

    @WithTransaction
    @Override
    public Uni<Long> update(Long id, ProfileRequest.Update body) {
        return findById(id)
            .onItem().ifNotNull().transformToUni(profile ->
                profileRepository.persist(Profile.builder()
                        .id(id)
                        .gender(body.gender())
                        .dayOfBirth(body.dayOfBirth()).build())
                    .onItem().transform(Profile::getId)
                    .onFailure().transform(throwable ->
                        new HttpException(Error.ACTION_FAILED, throwable,
                            Response.Status.NOT_IMPLEMENTED, "Update", "profile")));
    }

    @WithSession
    @Override
    public Uni<ProfileResponse.PreviewProfile> findById(Long id) {
        return profileRepository.findById(id)
            .onItem().ifNull().failWith(() ->
                new HttpException(Error.RESOURCE_NOT_FOUND, null, Response.Status.NOT_IMPLEMENTED, "Profile"))
            .onItem().ifNotNull().transform(profileMapper::toDto);
    }

}
