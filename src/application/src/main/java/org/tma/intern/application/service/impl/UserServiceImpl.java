package org.tma.intern.application.service.impl;

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
import org.tma.intern.application.injection.IdentityProviderClient;
import org.tma.intern.application.service.UserService;
import org.tma.intern.contract.RequestDto.UserRequest;
import org.tma.intern.domain.entity.IdentityUser;

import java.util.List;
import java.util.stream.IntStream;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserServiceImpl implements UserService {

    IdentityProviderClient keycloakClient;

    Faker faker = new Faker();

    @Override
    public Uni<String> create(UserRequest.Creation request) {
        return keycloakClient.create(IdentityUser.builder()
                .username(request.username())
                .email(request.email())
                .password(request.password())
                .build()).onFailure().transform(throwable ->
                new HttpException(Error.ACTION_FAILED, throwable,
                        Response.Status.NOT_IMPLEMENTED, "Create", "user"));
    }

    @Override
    public Uni<String> delete(String id) {
        try {
            return keycloakClient.delete(id);
        } catch (Exception exception) {
            log.error("Keycloak user deletion failed:  {}", exception.getMessage());
            throw new HttpException(Error.ACTION_FAILED, exception.getCause(),
                    Response.Status.NOT_IMPLEMENTED, "Delete", "user");
        }
    }

    @Override
    public Multi<String> seedUsers(int count, String... roles) {
        List<IdentityUser> fakeUsers = IntStream.range(0, count)
                .mapToObj(i -> IdentityUser.builder()
                        .username(faker.oscarMovie().character())
                        .email(faker.naruto() + "@lmao.com")
                        .password("123456")
                        .build()).toList();

        return keycloakClient.createUsers(fakeUsers, roles).invoke(id -> log.info("Created userId: {}", id));
    }

}
