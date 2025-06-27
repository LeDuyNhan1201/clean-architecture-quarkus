package org.tma.intern.domain;


import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;

import java.util.List;

public interface UserRepository extends PanacheRepository<User> {

    Uni<User> create(User entity);

    Uni<User> update(User entity);

    Uni<Void> delete(User entity);

    Uni<User> findById(String id);

    Uni<List<User>> findAll(String id);

}
