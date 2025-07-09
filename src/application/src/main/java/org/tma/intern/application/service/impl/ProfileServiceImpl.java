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
import org.tma.intern.application.exception.Error;
import org.tma.intern.application.exception.HttpException;
import org.tma.intern.application.injection.IdentityServerAdmin;
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
import java.util.stream.IntStream;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileServiceImpl implements ProfileService {

    ProfileRepository profileRepository;

    IdentityServerAdmin keycloakClient;

    ProfileMapper profileMapper;

    Faker faker;

//    @WithTransaction
    @Override
    public Multi<Long> seedData(int count) {
        List<Profile> profiles = IntStream.range(0, count)
                .mapToObj(i -> createRandomProfile("1")).toList();
        return profileRepository.persist(profiles).replaceWith(profiles)
                .onItem().transformToMulti(list ->
                        Multi.createFrom().items(list.stream().map(Profile::getId)));
    }

    private Profile createRandomProfile(String userId) {
        return Profile.builder()
                .gender(FakerHelper.randomEnum(Gender.class))
                .userId(userId)
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
