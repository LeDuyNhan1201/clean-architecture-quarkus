package org.tma.intern.application.service.impl;

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
import org.tma.intern.application.service.UserService;
import org.tma.intern.contract.RequestDto.UserRequest;
import org.tma.intern.domain.entity.IdentityUser;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserServiceImpl implements UserService {

    IdentityProviderClient keycloakClient;

    Faker faker = new Faker();

    @Override
    public String create(UserRequest.Creation request) {
        IdentityUser user = IdentityUser.builder()
            .username(request.username())
            .email(request.email())
            .password(request.password())
            .build();
        try {
            return keycloakClient.create(user, "user");
        } catch (Exception exception) {
            log.error("Keycloak user creation failed:  {}", exception.getMessage());
            throw new HttpException(Error.ACTION_FAILED, exception.getCause(),
                Response.Status.NOT_IMPLEMENTED, "Create", "user");
        }
    }

    @Override
    public String delete(String id) {
        try {
            return keycloakClient.delete(id);
        } catch (Exception exception) {
            log.error("Keycloak user deletion failed:  {}", exception.getMessage());
            throw new HttpException(Error.ACTION_FAILED, exception.getCause(),
                Response.Status.NOT_IMPLEMENTED, "Delete", "user");
        }
    }

    @Override
    public long seedUsers(int count, String... roles) {
        List<IdentityUser> fakeUsers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            fakeUsers.add(IdentityUser.builder()
                .username(faker.oscarMovie().character())
                .email(faker.naruto() + "@lmao.com")
                .password("123456")
                .build());
        }
        if (keycloakClient.createUsers(fakeUsers, roles)) return count;
        return 0;
    }

}
