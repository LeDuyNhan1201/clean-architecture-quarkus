package org.tma.intern.application.injection;

import io.smallrye.mutiny.Uni;
import org.tma.intern.domain.entity.IdentityUser;

import java.util.List;
import java.util.Map;

public interface IdentityProviderClient {

    List<String> getRoles();

    List<String> getUserIds(int count);

    String create(IdentityUser entity, String... roles);

    String delete(String id);

    Uni<Map<String, String>> getTokens(String username, String password);

    boolean createUsers(List<IdentityUser> entities, String... roles);

}
