package org.tma.intern.application.injection;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.tma.intern.domain.entity.IdentityUser;

import java.util.List;
import java.util.Map;

public interface IdentityServerAdmin {

    Multi<String> getRoles();

    Multi<String> getUserIds(int count);

    Uni<String> createUser(IdentityUser entity, String... roles);

    Uni<String> deleteUser(String id);

    Uni<String> createConcertResource(Long concertId, String region);

    Uni<Map<String, String>> getTokens(String username, String password);

    Multi<String> createUsers(List<IdentityUser> entities, String... roles);

}
