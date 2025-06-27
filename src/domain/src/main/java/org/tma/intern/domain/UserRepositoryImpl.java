package org.tma.intern.domain;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class UserRepositoryImpl implements UserRepository {

    @Override
    public Uni<User> create(User entity) {
        return persist(entity);
    }

    @Override
    public Uni<User> update(User entity) {
        return null;
    }

    @Override
    public Uni<Void> delete(User entity) {
        return null;
    }

    @Override
    public Uni<User> findById(String id) {
        return null;
    }

    @Override
    public Uni<List<User>> findAll(String id) {
        return null;
    }

}
